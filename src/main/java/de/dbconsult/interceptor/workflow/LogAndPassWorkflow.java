package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;

public class LogAndPassWorkflow implements Workflow {

    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {

    }

    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {

        int rawLen = data.getLen();
        byte[] rawdata = data.getOutput();

        System.out.print("----------------");
        System.out.print("   From " + data.getFormSource().getName());
        System.out.println("   ----------------");
        for (int index=0;index<rawLen;index++) {
            System.out.print(rawdata[index] + ",");
        }
        System.out.println();
        for (int index=0;index<rawLen;index++) {
            System.out.print((char)rawdata[index] + ",");
        }
        return data;
    }
}
