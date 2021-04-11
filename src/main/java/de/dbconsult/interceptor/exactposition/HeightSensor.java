package de.dbconsult.interceptor.exactposition;

import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.exactheight.GCodeSender;

import java.util.Vector;

import static java.lang.Math.abs;

public class HeightSensor {
    WorkflowDataStore workflowDataStore;
    ExtraReader extraReader;
    GCodeSender sender;

    Double machineOriginX = 0d;
    Double machineOriginY = 0d;

    public HeightSensor(WorkflowDataStore workflowDataStore) {
        extraReader = new ExtraReader(workflowDataStore);
        sender = new GCodeSender(workflowDataStore);
        this.workflowDataStore = workflowDataStore;

    }


    public Vector<Double> initialize() {
        machineOriginX = sender.queryMachineCoord("X");
        machineOriginY = sender.queryMachineCoord("Y");
        Vector<Double> homeInMachine = new Vector<>();
        homeInMachine.add(0, machineOriginX);
        homeInMachine.add(1, machineOriginY);
        return homeInMachine;

    }

    public void initializeHeight() {
        extraReader.initializeHeight();
    }

    public void cleanUp(Double result) {
        workflowDataStore.update("CurrentWorkpieceAngle",result);
        moveToOrigin();
    }

    public void setOrigin() {
        sender.blockUntilIdle();
        sender.resetWorkXYCoords();
    }

    public void moveToOrigin() {
        sender.jogLittleUp();
        sender.blockUntilIdle();
        sender.moveToOrigin();

    }

    public void normalYMovement() {
        sender.blockUntilIdle();
        sender.normalYMovement();
    }

    public void invertYMovement() {
        sender.blockUntilIdle();
        sender.inversYMovement();

    }

    public void jogToHomeFromPausedState(Vector<Double> offsets) {
        sender.jogLittleUp();
        Double newX = sender.queryMachineCoord("X");
        Double newY = sender.queryMachineCoord("Y");
/*
        Double xDistance = abs(newX + offsets.get(0));
        Double yDistance = abs(newY + offsets.get(1));
        if (newX>machineOriginX) xDistance = xDistance*-1;
        if (newY>machineOriginY) yDistance = yDistance*-1;
*/
        Vector<Double> origin = new Vector<>();
        origin.add(0,offsets.get(0));
        origin.add(1,offsets.get(1));
//        origin.add(0,xDistance);
//        origin.add(1,yDistance);
        sender.moveToXYCoords(origin);

    }
    public void moveToWorkOrigin() {
        sender.jogLittleUp();
        Double newX = sender.queryMachineCoord("X");
        Double newY = sender.queryMachineCoord("Y");
        Double xDistance = abs(machineOriginX - newX);
        Double yDistance = abs(machineOriginY - newY);
        if (newX>machineOriginX) xDistance = xDistance*-1;
        if (newY>machineOriginY) yDistance = yDistance*-1;
        Vector<Double> origin = new Vector<>();
        origin.add(0,xDistance);
        origin.add(1,yDistance);
        sender.moveToXYCoords(origin);
  //      waitToFinalizeMoveMent(origin);
    }

    public void moveRightForAngle() {
        Vector<Double> origin = new Vector<>();
        origin.add(0,20d);
        origin.add(1,0d);
        sender.blockUntilIdle();
        sender.moveToXYCoords(origin);
    }


    public Double findHeightChange(String axis) {
        long probeDistance = 2000;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if ("Z".equals(axis)) probeDistance = 500;
        Double changeVal = -99999d;
        long startHeight = extraReader.readHeightFromExtra();
        for (int x=0;x<2000;x++) {
            sender.miniJog(axis);
            long newHeight = extraReader.readHeightFromExtra();

            if(newHeight!=startHeight) {
         //   if(newHeight-startHeight>10) {
                changeVal = sender.queryMachineCoord(axis);
                break;
            }
        }
        return changeVal;
    }
}
