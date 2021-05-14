package de.dbconsult.interceptor.internal;

import de.dbconsult.interceptor.*;
import de.dbconsult.interceptor.exactheight.GCodeSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Vector;

public class UIController {

    private AdditionalCommunicator additionalCommunicator;
    private WorkflowDataStore workflowDataStore;

    public UIController(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
        additionalCommunicator = new AdditionalCommunicator(workflowDataStore);
    }

    public void gracefulHoldButton() {
        workflowDataStore.update("doGracefulHold", true);
    }

    public void gracefulResumeButton() {
        workflowDataStore.update("doGracefulResume", true);
    }


    public void eject() {
        GCodeSender sender = new GCodeSender(workflowDataStore);
        sender.eject();
    }
    public void storeEjectPosButton() {

        Vector<Double> coords = new Vector<>();
        coords.add((Double) workflowDataStore.read("WPOS_X"));
        coords.add((Double) workflowDataStore.read("WPOS_Y"));
        coords.add((Double) workflowDataStore.read("WPOS_Z"));
        storePositionToFile("ejectpos.txt", coords);
        GCodeSender sender = new GCodeSender(workflowDataStore);
        sender.storeEject();
    }
    public void storeWorkItemPositionButton() {
        Vector<Double> coords = new Vector<>();
        coords.add((Double) workflowDataStore.read("WPOS_X"));
        coords.add((Double) workflowDataStore.read("WPOS_Y"));
        coords.add((Double) workflowDataStore.read("WPOS_Z"));
        storePositionToFile("machinedata.txt", coords);
    }

    private void storePositionToFile(String filename, Vector<Double> coords) {
        try {
            FileOutputStream fo = new FileOutputStream(new File(filename));
            fo.write((coords.get(0)).toString().getBytes());
            fo.write(13);
            fo.write(10);
            fo.write((coords.get(1)).toString().getBytes());
            fo.write(13);
            fo.write(10);
            fo.write((coords.get(2)).toString().getBytes());
            fo.write(13);
            fo.write(10);

            fo.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void restoreWorkItemHomeButton() {
        try {
            Double wpos_x;
            Double wpos_y;
            Double wpos_z;

            BufferedReader rd = new BufferedReader(new FileReader(new File("machinedata.txt")));
            wpos_x = Double.parseDouble(rd.readLine());
            wpos_y = Double.parseDouble(rd.readLine());
            wpos_z = Double.parseDouble(rd.readLine());
            workflowDataStore.update("WPOS_X", wpos_x);
            workflowDataStore.update("WPOS_Y", wpos_y);
            workflowDataStore.update("WPOS_Z", wpos_z);
            rd.close();
            SerialsRepository serialsRepository = (SerialsRepository) workflowDataStore.read("SerialsRepository");
            Communication mill = serialsRepository.getMill().getComm();
            String homeMessage = "$H\n";
            String resetTo0Message = "G92X0Y0Z0\n";
            String workHomeMessage = "G0X" + wpos_x + "Y" + wpos_y + "Z" + wpos_z + "\n";
            String probeZ = "G21G91G38.2Z-30F100; G0Z1; G38.2Z-2F10\n";
            String saveZMessage = "G0Z10\n";

            WorkflowResult data = new WorkflowResult(0, null, serialsRepository.getMill(), homeMessage.getBytes(), homeMessage.length());
            mill.write(data);
            data = new WorkflowResult(0, null, serialsRepository.getMill(), resetTo0Message.getBytes(), resetTo0Message.length());
            mill.write(data);
            data = new WorkflowResult(0, null, serialsRepository.getMill(), workHomeMessage.getBytes(), workHomeMessage.length());
            mill.write(data);
            data = new WorkflowResult(0, null, serialsRepository.getMill(), resetTo0Message.getBytes(), resetTo0Message.length());
            mill.write(data);
            additionalCommunicator.switchProbingOn();
            data = new WorkflowResult(0, null, serialsRepository.getMill(), probeZ.getBytes(), probeZ.length());
            mill.write(data);
            //probingWorkflow.switchProbingOff();
            data = new WorkflowResult(0, null, serialsRepository.getMill(), resetTo0Message.getBytes(), resetTo0Message.length());
            mill.write(data);
            data = new WorkflowResult(0, null, serialsRepository.getMill(), saveZMessage.getBytes(), saveZMessage.length());
            mill.write(data);
            String answer = additionalCommunicator.blockUntilIdle();
            additionalCommunicator.switchProbingOff();
            additionalCommunicator.directWrite("pc", answer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
