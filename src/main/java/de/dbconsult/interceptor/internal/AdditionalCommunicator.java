package de.dbconsult.interceptor.internal;

import de.dbconsult.interceptor.*;

public class AdditionalCommunicator {

    private WorkflowDataStore workflowDataStore;

    public AdditionalCommunicator(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
    }


    public String blockUntilIdle() {
//WIFIFIX        Communication mill = getCommDescrption("mill").getComm();
        String answer = "";

        while(!answer.toLowerCase().contains("idle")) {
//WIFIFIX            mill.write("?");
//WIFIFIX            answer=new String(mill.readFully("tomill").getOutput());
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return answer;
    }

    public String blockUntilOk() {
//WIFIFIX        Communication mill = getCommDescrption("mill").getComm();
        String answer = "";

        while(!answer.toLowerCase().contains("ok")) {
//WIFIFIX            answer=new String(mill.readFully("tomill").getOutput());
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return answer;
    }

    public void directWrite(String to, String data) {
// WIFIFIX        toComm.write(data);
    }

    public void switchProbingOff() {
        switchProbing("#p0;\r");
    }

    public void switchProbingOn() {
        switchProbing("#p1;\r");
    }

    public void setSpindleSpeed(double speed) {
        int intSpeed = ((Double)speed).intValue();
        String strData = "#s" + intSpeed + ";\r";
        WorkflowResult data = new WorkflowResult(0, null,TargetDevices.EXTRA,strData.getBytes(), strData.length() );
 // WIFIFIX       extra.getComm().write(data);
    }

    private void switchProbing(String onOff) {
        WorkflowResult data = new WorkflowResult(0, null,TargetDevices.EXTRA,onOff.getBytes(), onOff.length() );
 // WIFIFIX       extra.getComm().write(data);
    }


}
