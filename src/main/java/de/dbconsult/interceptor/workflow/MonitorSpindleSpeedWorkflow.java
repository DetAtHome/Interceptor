package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.TargetDevices;
import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.internal.AdditionalCommunicator;

import java.util.StringTokenizer;


public class MonitorSpindleSpeedWorkflow extends AbstractWorkflow implements Workflow {

    WorkflowDataStore workflowDataStore = null;
    AdditionalCommunicator additionalCommunicator = null;
    double desiredSpindleSpeed=0;

    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {

        this.workflowDataStore = workflowDataStore;
        additionalCommunicator = new AdditionalCommunicator(workflowDataStore);
    }

    public synchronized WorkflowResult process(WorkflowResult data) {

        if (data.getFormSource()!= TargetDevices.CNC) return data;

        String message;
        message = new String(data.getOutput(), 0, data.getLen());
        if (message.contains("[MSG")) return data;
        if (message.contains("[PRB")) return data;
        if (message.contains("Grbl")) return data;
        if (message.toLowerCase().contains("alarm")) {
            additionalCommunicator.setSpindleSpeed(0);
            return data;
        }
        determineSpindleSpeed(message);
        return data;
    }


    private void determineSpindleSpeed(String message) {


        if ((message.contains("[") && message.contains("]")) ||
                (message.contains("<") && message.contains(">"))){

            int sIndex = message.indexOf("S");
            if (sIndex<0) return;
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
                        additionalCommunicator.setSpindleSpeed((Double)WorkflowDataStore.getInstance().read("SpindleSpeed"));
                        WorkflowDataStore.getInstance().update("speedModified","false");
                    }
                } else if(message.contains("M5")) {
                    additionalCommunicator.setSpindleSpeed(0);
                    WorkflowDataStore.getInstance().update("speedModified","true");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.err.println("Val was: '" + val + '"');
            }
        }

    }

}
