package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.TargetDevices;
import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.workflow.internalqueue.InternalQueue;

public class ExecuteAfter implements Workflow {
    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {

    }

    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {
        if(data.getFormSource()!= TargetDevices.CANDLE) return data;
        if(new String(data.getOutput()).contains("ok")) {
         //   InternalQueue.getInstance().dequeue();
        }
        return data;
    }
}
