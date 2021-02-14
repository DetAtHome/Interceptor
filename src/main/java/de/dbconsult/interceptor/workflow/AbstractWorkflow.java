package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;

public abstract class AbstractWorkflow implements Workflow {


    protected String getMessage(WorkflowResult data) {
        return new String(data.getOutput());

    }
    protected String filterComments(String message) {
        String result = message;

        int startCommment = message.indexOf("(");
        if(startCommment >-1) {
            result = message.substring(0,startCommment);
            int endComment = message.lastIndexOf(")");
            if (endComment>-1) {
                result = result + message.substring(endComment);
            } else {
                result = message;
            }
        }
        return result;
    }

    protected boolean isMill(WorkflowResult data) {
        return data.getFormSource().getName().toLowerCase().contains("mill");
    }

    protected boolean isPC(WorkflowResult data) {
        return data.getFormSource().getName().toLowerCase().contains("pc");
    }

}
