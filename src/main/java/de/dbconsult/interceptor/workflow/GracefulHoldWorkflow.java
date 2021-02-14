package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.SerialDescriptor;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;

public class GracefulHoldWorkflow extends AbstractWorkflow {

    WorkflowDataStore workflowDataStore = null;
    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;

    }

    @Override
    public WorkflowResult process(WorkflowResult data) {

        return data;
    }
}
