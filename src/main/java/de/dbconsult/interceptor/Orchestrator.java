package de.dbconsult.interceptor;

import de.dbconsult.interceptor.serial.SerialCommunication;
import de.dbconsult.interceptor.serial.SerialData;

public class Orchestrator {

    private static Orchestrator instance;

    private Orchestrator() {
    }

    public static Orchestrator getInstance() {
        if (instance == null) instance = new Orchestrator();
        return instance;
    }

    public synchronized void enqueueToWorkflow(SerialDescriptor from, byte[] raw, int rawLen) {
        SerialDescriptor out;
        if (from.serialId == 1) {
            out = SerialsRepository.getInstance().getSerialById(2);
        } else {
            out = SerialsRepository.getInstance().getSerialById(1);
        }
        WorkflowResult start = new WorkflowResult(from, out, raw, rawLen);

        for (Workflow flow : WorkflowRepository.getInstance().getConfiguredWorkflows()) {
            start = flow.process(start);
        }

        SerialData data = new SerialData();
        data.setData(start.getOutput());
        data.setLen(start.getLen());
        start.getToDestination().getComm().write(data);
    }
}