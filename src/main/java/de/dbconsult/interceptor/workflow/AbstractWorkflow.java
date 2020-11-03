package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.WorkflowDataStore;

public abstract class AbstractWorkflow {

    protected void storeMessageFragment(String message) {
        String buffered = getMessageFragment();
        if (buffered!=null) {
            message = buffered + message;
        }
        WorkflowDataStore.getInstance().update("buffer", message);
    }

    protected void clearMessageFragment() {
        WorkflowDataStore.getInstance().update("buffer", null);
    }

    protected String getMessageFragment() {
        return (String) WorkflowDataStore.getInstance().read("buffer");
    }

    protected boolean toBeContinued(String message) {
        if (!startsMessage(message)) {
            if (WorkflowDataStore.getInstance().read("continue")==null) {
                return false;
            }
            return (Boolean) WorkflowDataStore.getInstance().read("continue");
        } else {
            WorkflowDataStore.getInstance().update("continue", true);
            return true;

        }

    }

    protected boolean messageComplete(String message) {
        if(endsMessage(message)) {
            WorkflowDataStore.getInstance().update("continue", null);

        }
        if (WorkflowDataStore.getInstance().read("continue")==null) {
            return true;
        } else {
            return false;
        }

    }

    protected boolean startsMessage(String message) {
        if(message.contains("(")) return true;
        if(message.contains("[")) return true;
        if(message.contains("<")) return true;
        return false;
    }

    protected boolean endsMessage(String message) {
        if(message.contains(")")) return true;
        if(message.contains("]")) return true;
        if(message.contains(">")) return true;
        return false;
    }

    protected boolean fullMessage(String message) {

        if(startsMessage(message) && endsMessage(message)) return true;

        return false;
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

}
