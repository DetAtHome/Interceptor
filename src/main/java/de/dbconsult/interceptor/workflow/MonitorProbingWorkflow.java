package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.SerialsRepository;
import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.serial.SerialCommunication;
import de.dbconsult.interceptor.serial.SerialData;

public class MonitorProbingWorkflow extends AbstractWorkflow implements Workflow {

    private WorkflowDataStore workflowDataStore = null;
    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
        workflowDataStore.update("ProbeInProgress", false);
        workflowDataStore.update("probeAnswerCount", 0);

    }

    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {
        String message;
        message = new String(data.getOutput(), 0, data.getLen());
        if(toBeContinued(message)) {
            storeMessageFragment(message);
        }

        if(messageComplete(message)) {
            String fromBuffer = getMessageFragment();
            if (fromBuffer != null) {
                message = fromBuffer;

            }
            clearMessageFragment();
            message = filterComments(message);
        }
        if (data.getFormSource().getName().toLowerCase().contains("mill")) {
            // switch probing off when getting probe result
            if (message.contains("PRB") && (boolean) workflowDataStore.read("ProbeInProgress")) {
                int probeAnswers = (int)workflowDataStore.read("probeAnswerCount");
                System.out.println("Reducing probe answers from " + probeAnswers);
                probeAnswers--;
                workflowDataStore.update("probeAnswerCount", probeAnswers);
                if (probeAnswers==0) {
                    System.out.println("Switching off");
                    switchProbingOff();
                    workflowDataStore.update("ProbeInProgress", false);
                    workflowDataStore.update("probeAnswerCount",0);
                }
            }
        } else {
            if (message.contains("G38.")) {
                workflowDataStore.update("ProbeInProgress", true);
                switchProbingOn();
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

    private void switchProbingOff() {
        switchProbing("p0;");
    }

    private void switchProbingOn() {
        switchProbing("p1;");
    }

    private void switchProbing(String onOff) {
        SerialsRepository serialsRepository = (SerialsRepository) workflowDataStore.read("SerialsRepository");
        SerialCommunication extra = serialsRepository.getExtra().getComm();
        SerialData data = new SerialData();
        data.setLen(onOff.length());
        data.setData(onOff.getBytes());
        data.setAsString(onOff);
        extra.write(data);
    }
}
