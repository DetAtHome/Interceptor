package de.dbconsult.interceptor;

import de.dbconsult.interceptor.workflow.AbstractWorkflow;
import de.dbconsult.interceptor.workflow.LogAndPassFrame;

public class DeviationController extends AbstractWorkflow {

    WorkflowDataStore workflowDataStore;
    LogAndPassFrame ui = null;
    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
        if(workflowDataStore.read("UI")!=null) {
            ui = (LogAndPassFrame)workflowDataStore.read("UI");
        }
    }

    @Override
    public WorkflowResult process(WorkflowResult data) {
        if (ui!=null) {
            ui.showPCQSize((long)workflowDataStore.read("pcQSize"));
            ui.showMillQSize((long)workflowDataStore.read("millQSize"));
        }
        if(data.getLen()==0) return data;
        if (data.getOutput()[0]!='_') return data;
        if((boolean)workflowDataStore.read("deviationActive")) {
            if (getMessage(data).equals("?")) {
                data.setToDestination(TargetDevices.ABORT);
            }
        }
        if(getMessage(data).startsWith("_gracefulHoldON")) {
            workflowDataStore.update("deviationActive", true);
            System.out.println("deviationActive");
        }
        if(getMessage(data).startsWith("_gracefulHoldOFF")) {
            workflowDataStore.update("deviationActive", false);
            System.out.println("deviation deactivated");
        }
        // mark as processed
        data.setLen(0);
        return data;
    }
}
