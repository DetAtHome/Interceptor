package de.dbconsult.interceptor.exactheight;


import de.dbconsult.interceptor.SerialsRepository;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.serial.SerialCommunication;

import java.util.Locale;
import java.util.Vector;

public class GCodeSender {

    WorkflowDataStore workflowDataStore;

    public GCodeSender(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
    }

    public Double initialzeZHome() {
        // remove arbitrary junk from serial
        String junk = new String(getCommDescription().readFully().getOutput());
        System.out.println("Initializing" + junk);
        getCommDescription().write("G21\n");
        blockUntilOK();
        getCommDescription().write("G90\n");
        blockUntilOK();
        getCommDescription().write("G00 Z1\n");
        blockUntilOK();
        getCommDescription().write("G91\n");
        blockUntilOK();
        getCommDescription().write("G00 X-1\n");
        blockUntilOK();
        getCommDescription().write("G38.2Z-1.5F25\n");
        String newHome = blockUntilPrb();
        getCommDescription().write("G92Z0\n");
        blockUntilOK();
        getCommDescription().write("G90\n");
        blockUntilOK();
        getCommDescription().write("G00 Z1\n");
        blockUntilOK();
        getCommDescription().write("G00 X0\n");
        blockUntilOK();
        return HeightComputing.parseProbeAnswer(newHome);
    }

    public Double probeAtPoint(Vector<Double> pointToProbe) {
        moveToXYCoords(pointToProbe);
        return HeightComputing.parseProbeAnswer(sendProbeCommand());
    }

    public void moveToXYCoords(Vector<Double> point) {

        String command = String.format(String.format(Locale.US, "G0 X%1$.4f Y%2$.4f\n", point.elementAt(0),point.elementAt(1)));
        getCommDescription().write(command);
        blockUntilOK();
     }

     public void eject() {
         String command = "G28\n";
         getCommDescription().write(command);
         blockUntilOK();

     }

     public void storeEject() {
         String command = "G28.1\n";
         getCommDescription().write(command);
         blockUntilOK();

     }

    private String sendProbeCommand() {
        String returnValue;

        getCommDescription().write("G0 Z1.0000\n");
        blockUntilOK();
        getCommDescription().write("G0 Z0.2F60\n");
        blockUntilOK();
        getCommDescription().write("G91\n");
        blockUntilOK();
        getCommDescription().write("G38.2Z-1.4F25\n");
        returnValue = blockUntilPrb();
        getCommDescription().write("G90\n");
        blockUntilOK();
        getCommDescription().write("G0 Z1\n");
        blockUntilOK();
        return returnValue;
    }




    private String blockUntil(String semaphore) {
        SerialCommunication mill = getCommDescription();
        String answer = "";
        while(!answer.toLowerCase().contains(semaphore)) {
            WorkflowResult result =mill.readFully();
            answer = new String(result.getOutput(),0,result.getLen());
            if (answer.toLowerCase().contains("alarm") || answer.toLowerCase().contains("error"))
                throw new RuntimeException("Unexpected machining error or alarm: " + answer);
        }
        return answer;
    }

    private String blockUntilPrb() {
        return blockUntil("prb");
    }

    private String blockUntilOK() {
        return blockUntil("ok");
    }

    private SerialCommunication getCommDescription() {
        SerialsRepository serialsRepository = (SerialsRepository) workflowDataStore.read("SerialsRepository");
        SerialCommunication toComm = serialsRepository.getMill().getComm();
        return toComm;
    }
}
