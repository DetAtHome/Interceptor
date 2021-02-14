package de.dbconsult.interceptor.exactheight;

import de.dbconsult.interceptor.WorkflowDataStore;

public class HeightComputing {

    WorkflowDataStore workflowDataStore;
    public HeightComputing(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
    }

    public static Double parseProbeAnswer(String probeAnswer) {
        String[] tokens = probeAnswer.split(",");
        String stringVal = tokens[2].substring(0,tokens[2].indexOf(":"));
        return Double.parseDouble(stringVal);
    }

    public void storeMachineZ0(Double machineZ0) {
        workflowDataStore.update("MachineZ0", machineZ0);
    }

    public Double transformMachineZToWorkZ(Double machineZ) {
        Double machineZ0 = (Double) workflowDataStore.read("MachineZ0");
        return machineZ - machineZ0;
    }
}
