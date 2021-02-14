package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;

public class MonitorLookAheadBuffer extends AbstractWorkflow {

    private WorkflowDataStore workflowDataStore = null;

    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
    }

    @Override
    public WorkflowResult process(WorkflowResult data) {

        if(data.getLen()==1 && data.getOutput()[0]==10) return data;
        if(data.getLen()==1 && data.getOutput()[0]==24) {
            workflowDataStore.setCommandFound(0);
            workflowDataStore.setOkFound(0);
        }

        if(data.getFormSource().getName().toLowerCase().contains("pc")) {
            if(data.getLen()>1) {
                if(getMessage(data).contains("$X")) {
                    workflowDataStore.setCommandFound(0);
                    workflowDataStore.setOkFound(-1); // because $X responds with an ok that is to be ignored
                } else {
                    workflowDataStore.incCommandFound();
                }
            }
        }
        if(data.getFormSource().getName().toLowerCase().contains("mill")) {
            if (getMessage(data).contains("ok") || getMessage(data).contains("error") ) workflowDataStore.incOkFound();
        }
        return data;

    }
}
