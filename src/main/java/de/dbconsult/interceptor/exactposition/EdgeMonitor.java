package de.dbconsult.interceptor.exactposition;

import de.dbconsult.interceptor.WorkflowDataStore;
import javafx.geometry.Point2D;

import java.util.Vector;

import static java.lang.Math.abs;

public class EdgeMonitor extends Thread {

    WorkflowDataStore workflowDataStore;
    HeightSensor heightSensor;
    Vector<Double> workpieceOffset = new Vector<>();

    public EdgeMonitor(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
        heightSensor = new HeightSensor(workflowDataStore);
        workflowDataStore.update("EdgeFindInProgress", true);
    }


    @Override
    public void run() {
        workpieceOffset.add(0,(Double)workflowDataStore.read("WorkpieceXOffset"));
        workpieceOffset.add(1,(Double)workflowDataStore.read("WorkpieceYOffset"));
        workflowDataStore.update("EdgeFindInProgress", true);
        try {
            //give workflows time to finalize
            Thread.sleep(100);
            findBottomLeftCoordAndAngle();
            System.out.println(workflowDataStore.read("CurrentWorkpieceAngle"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workflowDataStore.update("EdgeFindInProgress",null);
        }
    }

    private void findBottomLeftCoordAndAngle() {
        Vector<Double> machineHome = heightSensor.initialize();

        // lower till button press
        if (heightSensor.findHeightChange("Z") == -99999d) throw new RuntimeException("No Edge detected");
        Double xBorder = heightSensor.findHeightChange("X");
        if(xBorder == -99999d)  throw new RuntimeException("No Edge detected");
     //   heightSensor.moveToWorkOrigin();
     //   machineHome.set(0, heightSensor.resetWorkOriginToBorderPlusOffset("X", xBorder,workpieceOffset.get(0)));
        Vector<Double> offsets = new Vector<>();
        offsets.add(0, workpieceOffset.get(0));
        offsets.add(1,0d);
        heightSensor.moveToHomeFromPausedState(offsets);
        if (heightSensor.findHeightChange("Z") == -99999d) throw new RuntimeException("No Edge detected");
        Double yBorder = heightSensor.findHeightChange("Y");
        if(yBorder == -99999d)  throw new RuntimeException("No Edge detected");
        offsets.clear();
        offsets.add(0, 0d);
        offsets.add(1,workpieceOffset.get(1));
        heightSensor.moveToHomeFromPausedState(offsets);
        heightSensor.setOrigin();
        heightSensor.moveRightForAngle();
        if (heightSensor.findHeightChange("Z") == -99999d) throw new RuntimeException("No Edge detected");
        Double yBorderForAngle = heightSensor.findHeightChange("Y");
        if(yBorderForAngle == -99999d)  throw new RuntimeException("No Edge detected");
        Point2D origin = new Point2D(machineHome.get(0), yBorder);

        Point2D baseLine = new Point2D(origin.getX()+20d,yBorder);
        Point2D angledLine = new Point2D(origin.getX()+20d, yBorderForAngle);
      //  heightSensor.moveToWorkOrigin();
      //  heightSensor.resetWorkOriginToBorderPlusOffset("Y", yBorder,workpieceOffset.get(1));

        Double angle = getAngle(origin, baseLine, angledLine);
        Vector<Double> result = new Vector();
        result.add(xBorder);
        result.add(yBorder);
        result.add(angle);
        heightSensor.cleanUp(result);
        return;
        // position back to workstart
//        heightSensor.findHeightChange("Y");
        // position tox + 2cm
//        heightSensor.findHeightChange("Y");

    }

    private Double getAngle(Point2D origin, Point2D bottomLeft, Point2D bottomForAngle) {
        return origin.angle(bottomForAngle, bottomLeft);
    //    return Math.toDegrees(atan2(bottomForAngle.getY(),bottomForAngle.getX()) - atan2(bottomLeft.getY(),bottomLeft.getX()));
    }

}
