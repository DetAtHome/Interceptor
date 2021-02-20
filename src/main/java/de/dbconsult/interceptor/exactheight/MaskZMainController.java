package de.dbconsult.interceptor.exactheight;

import de.dbconsult.interceptor.WorkflowDataStore;

import java.io.File;
import java.util.Vector;

public class MaskZMainController extends Thread {
    private WorkflowDataStore workflowDataStore;
    private HeightComputing heightComputing;
    private String originalFileName;
    private File inputFile;
    private FileParser fileParser;
    private FileWriter fileWriter;
    private String downMarker;

    public MaskZMainController(WorkflowDataStore workflowDataStore, String downMarker) {
        this.workflowDataStore = workflowDataStore;
        heightComputing = new HeightComputing(workflowDataStore);
        originalFileName = (String) workflowDataStore.read("MaskFileName");
        inputFile = (File) workflowDataStore.read("MaskFile");
        System.out.println(originalFileName);
        this.downMarker = downMarker;
        workflowDataStore.update("MaskDownMarker", downMarker);
        fileParser = new FileParser(workflowDataStore);
        fileWriter = new FileWriter(inputFile.getPath(), "_adjust");
    }


    @Override
    public void run() {
        workflowDataStore.update("ZMaskInProgress", true);
        try {
            //give workflows time to finalize
            Thread.sleep(100);

        GCodeSender sender = new GCodeSender(workflowDataStore);
        Double probe1 = sender.initialzeZHome();
        Double probe2 = sender.initialzeZHome();
        System.out.println("p1: " + probe1 + " p2:" + probe2);
        heightComputing.storeMachineZ0(probe2);
/*
        Vector<Double> coords= new Vector<>();
        coords.add(0d);
        coords.add(0d);
        Double heightAt0 = sender.probeAtPoint(coords);
        Double writeNewZ = heightComputing.transformMachineZToWorkZ(heightAt0);
        System.out.println("Recomputed: " + writeNewZ);

*/
        FileReader reader = new FileReader(inputFile);
        String line;
        do {
            line = reader.line();
            if(line==null) break;
            fileParser.trackXYCoords(line);
            if(fileParser.isMarkerLine(line)) {
                System.out.println("Current X" + workflowDataStore.read("MaskCurrentX") + " Y" + workflowDataStore.read("MaskCurrentY"));
                Double heightAtPoint = sender.probeAtPoint((Vector<Double>) workflowDataStore.read("MaskCurrentVector"));
                Double transformed = heightComputing.transformMachineZToWorkZ(heightAtPoint);
                System.out.println("Generated> G01 Z" + transformed);
                line = "G01 Z" + transformed;
            }
            fileWriter.writeLine(line);
        } while (line!=null);
        fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workflowDataStore.update("ZMaskInProgress",null);
        }
    }
}

