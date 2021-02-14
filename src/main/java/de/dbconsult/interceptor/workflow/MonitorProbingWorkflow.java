package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.internal.AdditionalCommunicator;

public class MonitorProbingWorkflow extends AbstractWorkflow {

    private WorkflowDataStore workflowDataStore = null;
    private AdditionalCommunicator additionalCommunicator = null;
    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
        additionalCommunicator = new AdditionalCommunicator(workflowDataStore);
        workflowDataStore.update("ProbeInProgress", false);
        workflowDataStore.update("probeAnswerCount", 0);

    }

    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {
        String message;
        message = new String(data.getOutput(), 0, data.getLen());
        message = filterComments(message);

        if (isMill(data)) {
            // switch probing off when getting probe result
            if (message.contains("[PRB") && (boolean) workflowDataStore.read("ProbeInProgress")) {
                int probeAnswers = (int)workflowDataStore.read("probeAnswerCount");
                System.out.println("Reducing probe answers from " + probeAnswers);
                probeAnswers--;
                workflowDataStore.update("probeAnswerCount", probeAnswers);
                if (probeAnswers==0) {
                    System.out.println("Switching off");
                    additionalCommunicator.switchProbingOff();
                    workflowDataStore.update("ProbeInProgress", false);
                    workflowDataStore.update("probeAnswerCount",0);
                }
            }
        } else {
            if (message.toUpperCase().startsWith("PROBERESET")) {
                workflowDataStore.update("ProbeInProgress", false);
                workflowDataStore.update("probeAnswerCount",0);
                additionalCommunicator.switchProbingOff();
                data.setOutput("()\n".getBytes());
                data.setLen("()\n".length());
            }
            if (message.toUpperCase().startsWith("PROBEON")) {
                additionalCommunicator.switchProbingOn();
                data.setOutput("()\n".getBytes());
                data.setLen("()\n".length());
//                data.setToDestination(new SerialDescriptor(0,"ABORT","dummy"));
            }
            if (message.toUpperCase().startsWith("PROBEOFF")) {
                additionalCommunicator.switchProbingOff();
                data.setOutput("()\n".getBytes());
                data.setLen("()\n".length());
//                data.setToDestination(new SerialDescriptor(0,"ABORT","dummy"));
            }

            if (message.contains("G38.")) {
                workflowDataStore.update("ProbeInProgress", true);
                additionalCommunicator.switchProbingOn();
                int currentProbeRequests = countProbeRequests(message);
                int pendingProbeRequests = workflowDataStore.read("probeAnswerCount")==null?0:(int)workflowDataStore.read("probeAnswerCount");
                workflowDataStore.update("probeAnswerCount",(currentProbeRequests + pendingProbeRequests));
                System.out.println("On for " + (currentProbeRequests + pendingProbeRequests));
            }
        }
        return data;
    }

    private static int countProbeRequests(String str) {
        int count =  (str.length() - str.replace("G38.", "").length()) / 4;
        System.out.println("Found " + count);
        return count;
    }


}
