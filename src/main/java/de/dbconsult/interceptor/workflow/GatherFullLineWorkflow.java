package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.SerialDescriptor;
import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;

public class GatherFullLineWorkflow extends AbstractWorkflow implements Workflow {

    WorkflowDataStore workflowDataStore = null;
    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
    }

    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {
        String message;
        message = new String(data.getOutput(), 0, data.getLen());
        if(toBeContinued(message)) {
            storeMessageFragment(message);
        }
        if(messageComplete(message)) {
            String fullMessage = getMessageFragment();
            if (fullMessage==null) fullMessage = message;
            data.setOutput(fullMessage.getBytes());
            data.setLen(fullMessage.length());
        } else {
            data.setToDestination(new SerialDescriptor(0, "ABORT", "dontcare"));
        }

        return data;
    }
}
