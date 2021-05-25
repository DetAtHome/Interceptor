package de.dbconsult.interceptor.internal;

import de.dbconsult.interceptor.*;
import de.dbconsult.interceptor.wifi.WifiCommunication;

public class AdditionalCommunicator {

    private WorkflowDataStore workflowDataStore;

    public AdditionalCommunicator(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
    }


    public String blockUntilIdle() {
        WifiCommunication communication = WifiCommunication.getInstance();
        String answer = "";

        while(!answer.toLowerCase().contains("idle")) {
            communication.directWriteToCNC("?");
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return answer;
    }

    public String blockUntilOk() {
        WifiCommunication communication = WifiCommunication.getInstance();
        String answer = "";

        while(!answer.toLowerCase().contains("ok")) {
            answer = new String(communication.readFully("tocandle").getOutput());
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return answer;
    }

    public void directWrite(String channel, String data) {
        WifiCommunication communication = WifiCommunication.getInstance();
        communication.directWriteToCNC(data);

    }

    public void switchProbingOff() {
        switchProbing("#p0\r");
    }

    public void switchProbingOn() {
        switchProbing("#p1\r");
    }

    public void setSpindleSpeed(double speed) {
        int intSpeed = ((Double)speed).intValue();
        String strData = "#s" + intSpeed + "\r";
        WorkflowResult data = new WorkflowResult(0, null,TargetDevices.EXTRA,strData.getBytes(), strData.length() );
        WifiCommunication communication = WifiCommunication.getInstance();
        communication.writeToSocket(data);
    }

    private void switchProbing(String onOff) {
        WorkflowResult data = new WorkflowResult(0, null,TargetDevices.EXTRA,onOff.getBytes(), onOff.length() );
        WifiCommunication communication = WifiCommunication.getInstance();
        communication.writeToSocket(data);
    }


}
