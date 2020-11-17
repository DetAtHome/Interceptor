package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.workflow.internalqueue.InternalQueue;

public class StoreToQueue implements Workflow {

    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {

    }

    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {
        if(!new String(data.getOutput()).contains("\r")) return data;
        if(data.getLen()>0) InternalQueue.getInstance().enqueue(data);
        return data;
    }
}
