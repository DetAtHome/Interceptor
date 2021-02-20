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

    public Double queryMachineCoord(String coord) {
        String allData = queryMachineState();
        String[] mposCoords = allData.substring(allData.indexOf("MPos:")+5).split("[,|]");
        try {
            switch (coord) {
                case "X":
                    return Double.parseDouble(mposCoords[0]);
                case "Y":
                    return Double.parseDouble(mposCoords[1]);
                case "Z":
                    return Double.parseDouble(mposCoords[2]);
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return -1d;
    }

    public String queryMachineState() {
        getCommDescription().write("?");
        return blockUntil("<");

    }

    public void blockUntilIdle() {
        String answer;
        do {
           answer = queryMachineState();
        } while (!(answer.toLowerCase().contains("idle"))||(answer.toLowerCase().contains("error")));
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

    public void resetWorkXYCoords() {
        getCommDescription().write("G92X0Y0\n");
        blockUntilOK();
    }
    public void moveToOrigin() {
        getCommDescription().write("G90G21G0X0Y0\n");
        blockUntilOK();
    }

    public void jogLittleUp() {
        jog("Z",2d);
    }
    public void moveToXYCoords(Vector<Double> point) {

//        String command = String.format(String.format(Locale.US, "G0 X%f$.4f Y%f$.4f\n", point.elementAt(0),point.elementAt(1)));
//        getCommDescription().write(command);
//        blockUntilOK();
        jog("X", point.elementAt(0));
        jog("Y",point.elementAt(1));
     }

    public void jog(String axis, Double distance) {
        String command = String.format(String.format(Locale.US,"$J=G21G91%s%fF2000\n", axis, distance));
        getCommDescription().write(command);

        blockUntilOK();

    }
     public void miniJog(String axis) {
         String command = String.format(String.format(Locale.US,"$J=G21G91%s-0.01F2000\n", axis));
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
        while(!answer.toLowerCase().contains(semaphore.toLowerCase())) {
            WorkflowResult result = mill.readFully();
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
