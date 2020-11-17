package de.dbconsult.interceptor;

public interface Workflow {

    void initialize(WorkflowDataStore workflowDataStore);
    WorkflowResult process(WorkflowResult data);

}
