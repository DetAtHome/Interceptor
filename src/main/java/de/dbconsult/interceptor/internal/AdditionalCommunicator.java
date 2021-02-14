package de.dbconsult.interceptor.internal;

import de.dbconsult.interceptor.*;
import de.dbconsult.interceptor.serial.SerialCommunication;

import java.util.concurrent.locks.Lock;

import static java.lang.Thread.currentThread;

public class AdditionalCommunicator {

    private WorkflowDataStore workflowDataStore;

    public AdditionalCommunicator(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
    }


    public String blockUntilIdle() {
        SerialCommunication mill = getCommDescrption("mill").getComm();
        String answer = "";

        while(!answer.toLowerCase().contains("idle")) {
            mill.write("?");
            answer=new String(mill.readFully().getOutput());
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return answer;
    }

    public void directWrite(String to, String data) {
        SerialCommunication toComm = getCommDescrption(to).getComm();
        toComm.write(data);
    }

    public void switchProbingOff() {
        switchProbing("p0;");
    }

    public void switchProbingOn() {
        switchProbing("p1;");
    }

    public void setSpindleSpeed(double speed) {
        SerialDescriptor extra = getCommDescrption("extra");
        int intSpeed = ((Double)speed).intValue();
        String strData = "s" + intSpeed + ";";
        WorkflowResult data = new WorkflowResult(0, null,extra,strData.getBytes(), strData.length() );
        extra.getComm().write(data);
    }

    private void switchProbing(String onOff) {
        SerialDescriptor extra = getCommDescrption("extra");
        WorkflowResult data = new WorkflowResult(0, null,extra,onOff.getBytes(), onOff.length() );
        extra.getComm().write(data);
    }

    private SerialDescriptor getCommDescrption(String from) {
        SerialsRepository serialsRepository = (SerialsRepository) workflowDataStore.read("SerialsRepository");
        SerialDescriptor extra = null;
        if("mill".equals(from)) {
            extra = serialsRepository.getMill();
        } else if("pc".equals(from)) {
            extra = serialsRepository.getPc();
        } else if("extra".equals(from)) {
            extra = serialsRepository.getExtra();
        } else {
            throw new RuntimeException("Unknown device '" + from + "'");
        }
        return extra;
    }
}
