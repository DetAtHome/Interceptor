package de.dbconsult.interceptor.internal;

import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.exactposition.ExtraReader;
import de.dbconsult.interceptor.workflow.LogAndPassFrame;

public class ExternalLogger implements Runnable {

    private ExtraReader extraReader;
    private LogAndPassFrame frame;

    public ExternalLogger(WorkflowDataStore workflowDataStore) {
        extraReader = (ExtraReader) workflowDataStore.read("EXTRAREADER");
        frame = (LogAndPassFrame) workflowDataStore.read("UIInstance");
    }

    @Override
    public void run() {
        while (true) {
           // extraReader.send(null);

            frame.extraLog(extraReader.getLastLog(frame.isHeartbeatEnabled()));
        }
    }
}
