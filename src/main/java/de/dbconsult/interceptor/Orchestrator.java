package de.dbconsult.interceptor;

import de.dbconsult.interceptor.serial.SerialData;

public class Orchestrator {

    private SerialsRepository serialsRepository;
    private WorkflowRepository workflowRepository;

    public Orchestrator(SerialsRepository serialsRepository, WorkflowRepository workflowRepository) {
        this.serialsRepository = serialsRepository;
        this.workflowRepository = workflowRepository;
    }

    public synchronized void enqueueToWorkflow(long index, SerialDescriptor from, byte[] raw, int rawLen) {

        SerialDescriptor out;
        if (from.serialId == 1) {
            out = serialsRepository.getSerialById(2);
        } else {
            out = serialsRepository.getSerialById(1);
        }
        WorkflowResult start = new WorkflowResult(index, from, out, raw, rawLen);

        for (Workflow flow : workflowRepository.getConfiguredWorkflows()) {
            start = flow.process(start);
            if(start.getToDestination().getName().equals("ABORT")) return;
        }

        SerialData data = new SerialData();
        data.setData(start.getOutput());
        data.setLen(start.getLen());
        if(!from.getName().startsWith("test"))
            start.getToDestination().getComm().write(data);
    }
}