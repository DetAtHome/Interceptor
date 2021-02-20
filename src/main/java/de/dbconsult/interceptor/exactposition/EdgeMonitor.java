package de.dbconsult.interceptor.exactposition;

import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.exactheight.FileParser;
import de.dbconsult.interceptor.exactheight.FileReader;
import de.dbconsult.interceptor.exactheight.FileWriter;
import javafx.geometry.Point2D;
import javafx.scene.transform.Rotate;

import java.io.File;
import java.util.Locale;
import java.util.Vector;

import static java.lang.Math.abs;

public class EdgeMonitor extends Thread {

    WorkflowDataStore workflowDataStore;
    HeightSensor heightSensor;
    Vector<Double> workpieceOffset = new Vector<>();
    File inputFile;
    private FileParser fileParser;
    private FileWriter fileWriter;

    public EdgeMonitor(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
        heightSensor = new HeightSensor(workflowDataStore);
        inputFile = (File) workflowDataStore.read("RotationFileDirectory");
        workflowDataStore.update("EdgeFindInProgress", true);
        fileParser = new FileParser(workflowDataStore);
        fileWriter = new FileWriter(inputFile.getPath(), "_rotate");
    }


    @Override
    public void run() {
        workpieceOffset.add(0,(Double)workflowDataStore.read("WorkpieceXOffset"));
        workpieceOffset.add(1,(Double)workflowDataStore.read("WorkpieceYOffset"));
        workflowDataStore.update("EdgeFindInProgress", true);
        try {
            //give workflows time to finalize
            Thread.sleep(100);
            Vector<Double> distancesFromEdges = findWorkpieceHome();
            Double angle = findAngle();
//            System.out.println(workflowDataStore.read("CurrentWorkpieceAngle"));
            rotateFile();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workflowDataStore.update("EdgeFindInProgress",null);
        }
    }

    /**
     * Moves machine to left then bottom of PCB
     * Sets Working coords relative to the edges + the offset
     *
     * @return Vector (x,y) containing distances (always positive) from edge to working home
     */
    private Vector<Double> findWorkpieceHome() {
        Vector<Double> machineHome = heightSensor.initialize();

        // lower till button press
        if (heightSensor.findHeightChange("Z") == -99999d) throw new RuntimeException("No Edge detected");
        Double xBorder = heightSensor.findHeightChange("X");
        if (xBorder == -99999d) throw new RuntimeException("No Edge detected");
        Vector<Double> offsets = new Vector<>();
        offsets.add(0, workpieceOffset.get(0));
        offsets.add(1, 0d);
        heightSensor.jogToHomeFromPausedState(offsets);
        if (heightSensor.findHeightChange("Z") == -99999d) throw new RuntimeException("No Edge detected");
        Double yBorder = heightSensor.findHeightChange("Y");
        if (yBorder == -99999d) throw new RuntimeException("No Edge detected");
        offsets.clear();
        offsets.add(0, 0d);
        offsets.add(1, workpieceOffset.get(1));
        heightSensor.jogToHomeFromPausedState(offsets);
        heightSensor.setOrigin();
        Vector<Double> result = new Vector<>();
        result.add(0, abs(machineHome.get(0) - xBorder));
        result.add(1, abs(machineHome.get(1) - yBorder));
        return result;
    }

    /**
     * Expects machine home to be set according to border & offsets.
     * Expects machine to be lifted up
     * @return
     */
    private Double findAngle() {
        Vector<Double> machineHome = heightSensor.initialize();
        if (heightSensor.findHeightChange("Z") == -99999d) throw new RuntimeException("No Edge detected");
        Double yBorder = heightSensor.findHeightChange("Y");
        if (yBorder == -99999d) throw new RuntimeException("No Edge detected");
        heightSensor.moveToOrigin();
        heightSensor.moveRightForAngle();
        if (heightSensor.findHeightChange("Z") == -99999d) throw new RuntimeException("No Edge detected");
        Double yBorderForAngle = heightSensor.findHeightChange("Y");
        if(yBorderForAngle == -99999d)  throw new RuntimeException("No Edge detected");

        Point2D origin = new Point2D(machineHome.get(0), yBorder);

        Point2D baseLine = new Point2D(origin.getX()+20d, yBorder);
        Point2D angledLine = new Point2D(origin.getX()+20d, yBorderForAngle);

        Double angle = getAngle(origin, baseLine, angledLine);
        Vector<Double> result = new Vector();

        heightSensor.cleanUp(angle);
        return angle;

    }

    private void rotateFile() {
        FileReader reader = new FileReader(inputFile);
        String line;
        Rotate rotate = new Rotate((Double)workflowDataStore.read("CurrentWorkpieceAngle"));
        try {
            do {
                line = reader.line();

                if(line==null) break;
                if(line.startsWith("(")) {
                    fileWriter.writeLine(line);
                    continue;
                }
                Vector<Double> originalPointCoords = fileParser.parseLine(line);
                if(originalPointCoords==null) {
                    fileWriter.writeLine(line);
                    continue;
                }
                Point2D newDestination = rotate.transform(originalPointCoords.get(0),originalPointCoords.get(1));
                String originalXAsString = String.format(Locale.ENGLISH,"%.4f", originalPointCoords.get(0));
                String originalYAsString = String.format(Locale.ENGLISH,"%.4f", originalPointCoords.get(1));
                String newXAsString = String.format(Locale.ENGLISH,"%.4f", newDestination.getX());
                String newYAsString = String.format(Locale.ENGLISH,"%.4f", newDestination.getY());

                line = line.replaceAll(originalXAsString, newXAsString);
                line = line.replaceAll(originalYAsString, newYAsString);

                fileWriter.writeLine(line);
            } while (line!=null);
            fileWriter.close();
        } catch (Exception e) {
            throw e;
        }
    }
    private Double getAngle(Point2D origin, Point2D bottomLeft, Point2D bottomForAngle) {
        return origin.angle(bottomForAngle, bottomLeft);
    //    return Math.toDegrees(atan2(bottomForAngle.getY(),bottomForAngle.getX()) - atan2(bottomLeft.getY(),bottomLeft.getX()));
    }

}
