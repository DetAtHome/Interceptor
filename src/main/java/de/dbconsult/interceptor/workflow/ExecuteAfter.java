package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.workflow.internalqueue.InternalQueue;

public class ExecuteAfter implements Workflow {
    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {
        if(!data.getFormSource().getName().equalsIgnoreCase("pc")) return data;
        if(new String(data.getOutput()).contains("ok")) {
            InternalQueue.getInstance().dequeue();
        }
        return data;
    }
}
