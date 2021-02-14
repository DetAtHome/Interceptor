package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;

public class MaskZWorkflow extends AbstractWorkflow {

    private WorkflowDataStore workflowDataStore = null;

    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
    }

    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {
        if (true==(boolean)workflowDataStore.read("ZMaskInProgress")) {
            if (isMill(data)) {
                String messege = getMessage(data);
                if(messege!=null) {
                    while(!"ack".equalsIgnoreCase((String)workflowDataStore.read("lastResponse"))) {}
                    workflowDataStore.update("lastResponse", messege);
                }
            }
        }
        return data;
    }
}
