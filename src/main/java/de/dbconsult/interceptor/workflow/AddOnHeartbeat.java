package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.Communication;
import de.dbconsult.interceptor.SerialsRepository;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;

public class AddOnHeartbeat extends AbstractWorkflow {

    private WorkflowDataStore workflowDataStore = null;

    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
        workflowDataStore.update("ProbeInProgress", false);
        workflowDataStore.update("probeAnswerCount", 0);

    }

    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {
        if (isMill(data)) {
            if (!getMessage(data).startsWith("[")) return data;
            String message = getMessage(data).substring(0, getMessage(data).indexOf(']'));
            String state = (boolean)workflowDataStore.read("ProbeInProgress")?"ON":"OFF";
            String heartbeatResult = sendHeartbeat();
            if (heartbeatResult==null) state = "ERROR";
            else if (heartbeatResult.contains("ON")) state = "ON";
            else if (heartbeatResult.contains("OFF")) state = "OFF";
            else state = "?";
            message = message + " P:" + state + "]\n";
            data.setOutput(message.getBytes());
            data.setLen(message.length());
        }
        return data;
    }

    private String sendHeartbeat() {
        SerialsRepository serialsRepository = (SerialsRepository) workflowDataStore.read("SerialsRepository");
        Communication extra = serialsRepository.getExtra().getComm();
        String pingMessage = ">?1;";
        WorkflowResult data = new WorkflowResult(0, null,serialsRepository.getExtra(),pingMessage.getBytes(), pingMessage.length() );
        extra.write(data);
        long start = System.currentTimeMillis();
        String message = getMessage(extra.readFully("toextra"));
        while(!message.contains("probe")) {
            message = getMessage(extra.readFully("toextra"));
            if(System.currentTimeMillis()>start+1000) return null;
        }

        return message;
    }
}
