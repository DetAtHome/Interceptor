package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;

public class MonitorVersionWorkflow extends AbstractWorkflow {

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
            if (getMessage(data).contains("Grbl 0.9")) {
                String version = "0.9";
                System.out.println("Gbl Version> " + version);
                workflowDataStore.update("GrblVersion", version);
            } else if (getMessage(data).startsWith("Grbl 1.1")) {
                String version = "0.9";
                System.out.println("Gbl Version> " + version);
                workflowDataStore.update("GrblVersion", version);
            }

        }
        return data;
    }
}
