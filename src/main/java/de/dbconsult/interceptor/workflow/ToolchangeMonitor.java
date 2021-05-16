package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.*;
import de.dbconsult.interceptor.internal.AdditionalCommunicator;

public class ToolchangeMonitor extends AbstractWorkflow {

    private WorkflowDataStore workflowDataStore = null;
    private AdditionalCommunicator additionalCommunicator;

    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
        workflowDataStore.update("ToolchangeInProgress", false);
        workflowDataStore.update("ToolLoaded", 0);
        additionalCommunicator = new AdditionalCommunicator(workflowDataStore);
    }

    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {
        if (isMill(data)) {
            if((boolean)workflowDataStore.read("ToolchangeInProgress")) {
                if(getMessage(data).contains("error:20")) {
                    workflowDataStore.update("ToolchangeInProgress", false);
       //             data.setOutput("ok\r".getBytes());
       //             data.setLen(data.getOutput().length);
                    return data;
                }
            }
        }
        if (isPC(data)) {
            int changeResult=0;
            String toolData = "";
            if(getMessage(data).startsWith("T")) {
                workflowDataStore.update("ToolLoaded", Integer.parseInt(getMessage(data).trim().substring(1)));
                return data;
            }

            if(getMessage(data).startsWith("M6")) {
                workflowDataStore.update("ToolchangeInProgress", true);
                int tool = (int) workflowDataStore.read("ToolLoaded");
                if (tool == 0) {
                    System.out.println("\nUnloading to " + tool);
                    workflowDataStore.update("ToolLoaded", 0);
                    toolData = "r" + tool;
                } else if (tool > 0) {
                    System.out.println("\nLoad from " + tool);
                    workflowDataStore.update("ToolLoaded", "1");
                    toolData = "l" + tool;
                } else {
                    // Error case, do sth about that
                    return data;
                }

            } else {
                return data;
            }
//            data.setToDestination(new SerialDescriptor(0,"ABORT","abort"));
            additionalCommunicator.directWrite("mill", "G0 Z0 F500\n");
            additionalCommunicator.blockUntilOk();
            additionalCommunicator.directWrite("mill", "G0 X0 F500\n");
            additionalCommunicator.blockUntilOk();
            additionalCommunicator.blockUntilIdle();
            additionalCommunicator.directWrite("mill", "M0\n");
            changeResult = blockUntilUnLoaded(toolData);
            additionalCommunicator.directWrite("mill", "~");
            additionalCommunicator.blockUntilOk();
        }
        return data;
    }

    private int blockUntilUnLoaded(String toolData) {
        SerialsRepository serialsRepository = (SerialsRepository) workflowDataStore.read("SerialsRepository");
        Communication extra = serialsRepository.getExtra().getComm();
        String toolMessage = ">" + toolData + ";";
        System.out.println("Sending to extra> " + toolData);
        WorkflowResult data = new WorkflowResult(0, null,TargetDevices.EXTRA,toolMessage.getBytes(), toolMessage.length() );
        extra.write(data);
        long start = System.currentTimeMillis();
        String message = getMessage(extra.readFully("toextra"));
        while(!message.contains("ok")) {
   //         if (message.length()>3) System.out.println(">" + message);
            message = getMessage(extra.readFully("toextra"));
   //         if(System.currentTimeMillis()>start+1000) return 9999;
        }
        System.out.println("found ok");
        return 0;
    }
}
