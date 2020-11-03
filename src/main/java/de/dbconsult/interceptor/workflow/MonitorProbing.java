package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.SerialsRepository;
import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.serial.SerialCommunication;
import de.dbconsult.interceptor.serial.SerialData;

public class MonitorProbing extends AbstractWorkflow implements Workflow {

    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {
        String message;
        message = new String(data.getOutput(), 0, data.getLen());
        if(toBeContinued(message)) {
            storeMessageFragment(message);
        }

        if(messageComplete(message)) {
            String fromBuffer = getMessageFragment();
            if (fromBuffer != null) {
                message = fromBuffer;

            }
            clearMessageFragment();
            message = filterComments(message);
        }
        if (data.getFormSource().getName().toLowerCase().contains("mill")) {
            // switch probing off when getting probe result
            if (message.contains("[PRB")) {
                switchProbingOff();
                System.out.println("Off");
            }
        } else {
            if (message.contains("G38") || message.contains("G 38")) {
                switchProbingOn();
                System.out.println("On");
            }
        }
        return data;
    }

    private void switchProbingOff() {
        SerialCommunication extra = SerialsRepository.getInstance().getExtra().getComm();
        SerialData data = new SerialData();
        String strData = "p0;";
        data.setLen(strData.length());
        data.setData(strData.getBytes());
        data.setAsString(strData);
        extra.write(data);
    }

    private void switchProbingOn() {
        SerialCommunication extra = SerialsRepository.getInstance().getExtra().getComm();
        SerialData data = new SerialData();
        String strData = "p1;";
        data.setLen(strData.length());
        data.setData(strData.getBytes());
        data.setAsString(strData);
        extra.write(data);

    }
}
