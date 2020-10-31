package de.dbconsult.interceptor.workflow;

import com.pi4j.io.gpio.*;
import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.gpio.GPIOController;
import de.dbconsult.interceptor.gpio.GPIOControllerImpl;

import java.util.StringTokenizer;


public class MonitorSpindleSpeed implements Workflow {

// create gpio controller instance

    double desiredSpindleSpeed=0;

    private GPIOController gpioController;

    public void customInit(GPIOController controller) {
        gpioController = controller;
    }

    public WorkflowResult process(WorkflowResult data) {

        if (!data.getFormSource().getName().toLowerCase().contains("mill")) return data;

        String message;
        message = new String(data.getOutput(), 0, data.getLen());
        if(toBeContinued(message)) {
            storeMessageFragment(message);
        }

        if(messageComplete(message)) {
            String fromBuffer = getMessageFragment();
            if(fromBuffer!=null) {
                message=fromBuffer;

            }
            clearMessageFragment();
            message = filterComments(message);
            determineSpindleSpeed(message);
        }
        return data;
    }

    private void storeMessageFragment(String message) {
        String buffered = getMessageFragment();
        if (buffered!=null) {
            message = buffered + message;
        }
        WorkflowDataStore.getInstance().update("buffer", message);
    }

    private void clearMessageFragment() {
        WorkflowDataStore.getInstance().update("buffer", null);
    }

    private String getMessageFragment() {
        return (String) WorkflowDataStore.getInstance().read("buffer");
    }

    private boolean toBeContinued(String message) {
        if (!startsMessage(message)) {
            if (WorkflowDataStore.getInstance().read("continue")==null) {
                return false;
            }
            return (Boolean) WorkflowDataStore.getInstance().read("continue");
        } else {
            WorkflowDataStore.getInstance().update("continue", true);
            return true;

        }

    }

    private boolean messageComplete(String message) {
        if(endsMessage(message)) {
            WorkflowDataStore.getInstance().update("continue", null);

        }
        if (WorkflowDataStore.getInstance().read("continue")==null) {
            return true;
        } else {
            return false;
        }

    }

    private boolean startsMessage(String message) {
        if(message.contains("(")) return true;
        if(message.contains("[")) return true;
        if(message.contains("<")) return true;
        return false;
    }

    private boolean endsMessage(String message) {
        if(message.contains(")")) return true;
        if(message.contains("]")) return true;
        if(message.contains(">")) return true;
        return false;
    }

    private boolean fullMessage(String message) {

        if(startsMessage(message) && endsMessage(message)) return true;

        return false;
    }

    private String filterComments(String message) {
        String result = message;
        int startCommment = message.indexOf("(");
        if(startCommment >-1) {
            result = message.substring(0,startCommment);
            int endComment = message.lastIndexOf(")");
            if (endComment>-1) {
                result = result + message.substring(endComment);
            } else {
                result = message;
            }
        }
        return result;
    }

    private void determineSpindleSpeed(String message) {

        if (message.contains("MSG")) return;
        if (message.toLowerCase().contains("grbl")) return;
        if (message.toLowerCase().contains("alarm")) {
           setSpindleSpeed(0);
            return;
        }

        if ((message.contains("[") && message.contains("]")) ||
                (message.contains("<") && message.contains(">"))){

            int sIndex = message.indexOf("S");
            message = message.concat(" ");
            String param = message.substring(sIndex + 1).trim();
            StringTokenizer tokenizer = new StringTokenizer(param, "]\n\r ");
            String val = tokenizer.nextToken();

            if(message.indexOf(":", sIndex)==sIndex+1) {
                int cIndex=message.indexOf(",",sIndex);
                param = message.substring(cIndex + 1).trim();
                tokenizer = new StringTokenizer(param, "|]\n\r> ");
                val = tokenizer.nextToken();
            }

            try {
                desiredSpindleSpeed = Double.parseDouble(val);
                if (WorkflowDataStore.getInstance().read("SpindleSpeed")==null || (Double)WorkflowDataStore.getInstance().read("SpindleSpeed")!=desiredSpindleSpeed) {
                    WorkflowDataStore.getInstance().update("SpindleSpeed", desiredSpindleSpeed);
                    WorkflowDataStore.getInstance().update("speedModified","true");
                }
                if(message.contains("M3")) {
                    Object speedModified = WorkflowDataStore.getInstance().read("speedModified");
                    if("true".equals(speedModified)) {
                        setSpindleSpeed((Double)WorkflowDataStore.getInstance().read("SpindleSpeed"));
                        WorkflowDataStore.getInstance().update("speedModified","false");
                    }
                } else if(message.contains("M5")) {
                    setSpindleSpeed(0);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.err.println("Val was: '" + val + '"');
            }
        }

    }
    private void setSpindleSpeed(double speed) {
        gpioController.setPin(7,speed);
    }
}
