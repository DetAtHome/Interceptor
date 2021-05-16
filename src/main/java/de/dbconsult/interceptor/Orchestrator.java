package de.dbconsult.interceptor;

import de.dbconsult.interceptor.workflow.internalqueue.InternalQueue;

public class Orchestrator {

    private SerialsRepository serialsRepository;
    private WorkflowRepository workflowRepository;
    private WorkflowDataStore workflowDataStore;
    private InternalQueue internalQueue;

    public Orchestrator(SerialsRepository serialsRepository,
                        WorkflowRepository workflowRepository,
                        WorkflowDataStore workflowDataStore,
                        InternalQueue internalQueue) {
        this.serialsRepository = serialsRepository;
        this.workflowRepository = workflowRepository;
        this.workflowDataStore = workflowDataStore;
        this.internalQueue = internalQueue;
    }

    public synchronized WorkflowResult enqueueToWorkflow(WorkflowResult start) {

        for (Workflow flow : workflowRepository.getConfiguredWorkflows()) {
            start = flow.process(start);
            if(start.getToDestination().equals(TargetDevices.ABORT)) return start;
        }
        return start;



    }
}