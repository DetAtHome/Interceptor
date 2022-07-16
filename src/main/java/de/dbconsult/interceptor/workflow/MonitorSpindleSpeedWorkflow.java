package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.TargetDevices;
import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.exactposition.ExtraReader;
import de.dbconsult.interceptor.internal.AdditionalCommunicator;

import java.util.StringTokenizer;


public class MonitorSpindleSpeedWorkflow extends AbstractWorkflow implements Workflow {

    WorkflowDataStore workflowDataStore = null;
    ExtraReader additionalCommunicator = null;
    double desiredSpindleSpeed=0;

    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {

        this.workflowDataStore = workflowDataStore;
        System.out.println("extrareader: " + workflowDataStore.read("EXTRAREADER"));
        additionalCommunicator = (ExtraReader) workflowDataStore.read("EXTRAREADER");
    }

    public synchronized WorkflowResult process(WorkflowResult data) {

        if (data.getFormSource()!= TargetDevices.CNC) return data;

        String message;
        message = new String(data.getOutput(), 0, data.getLen());
        if (message.contains("[MSG")) return data;
        if (message.contains("[PRB")) return data;
        if (message.contains("Grbl")) return data;
        if (message.toLowerCase().contains("alarm")) {
            additionalCommunicator.send("S0;");
            return data;
        }
        determineSpindleSpeed(message);
        return data;
    }


    private void determineSpindleSpeed(String message) {

/* valid strings from mill:
1.) <Idle|MPos:-187.000,-3.000,-8.000|FS:0,20000|Ov:100,100,100|A:S>
2.) <Idle|MPos:-187.000,-3.000,-8.000|FS:0,0>
3.) [GC:G0 G54 G17 G21 G90 G94 M5 M9 T0 F0 S20000]
1 Valid, running at 20000
2 Valid, stopped
3 invalid, showing max speed but in state M5

 */

    try {
        Double val = 0.1;
        String[] parts = message.split("\\|");
        for (String p :parts) {
            if (p.startsWith("FS:")) {
                String numVal = p.substring(3);
                numVal = numVal.split(",")[1];
                if(numVal.contains(">")) numVal = numVal.substring(0,numVal.length()-2);
                try {
                    desiredSpindleSpeed = Double.parseDouble(numVal);
                }   catch (NumberFormatException e) {
                    System.out.println(numVal);
                    System.out.println(numVal.length());
                    System.out.println(e.getMessage());
                }
                String speedCmd = "S" + desiredSpindleSpeed + ";";
                additionalCommunicator.send(speedCmd);

                break;
            }
        }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

}


