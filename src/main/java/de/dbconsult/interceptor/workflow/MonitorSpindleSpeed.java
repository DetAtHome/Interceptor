package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.SerialsRepository;
import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.serial.SerialCommunication;
import de.dbconsult.interceptor.serial.SerialData;

import java.util.StringTokenizer;


public class MonitorSpindleSpeed extends AbstractWorkflow implements Workflow {

    double desiredSpindleSpeed=0;


    public synchronized WorkflowResult process(WorkflowResult data) {

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


    private void determineSpindleSpeed(String message) {

        if (message.contains("MSG")) return;
        if (message.contains("PRB")) return;
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
        SerialCommunication extra = SerialsRepository.getInstance().getExtra().getComm();
        SerialData data = new SerialData();
        int intSpeed = ((Double)speed).intValue();
        String strData = "s" + intSpeed + ";";
        data.setLen(strData.length());
        data.setData(strData.getBytes());
        data.setAsString(strData);
        extra.write(data);
    }
}
