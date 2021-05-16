package de.dbconsult.interceptor;

import com.fazecast.jSerialComm.SerialPort;
import de.dbconsult.interceptor.exactposition.ExtraReader;
import de.dbconsult.interceptor.internal.ExternalLogger;
import de.dbconsult.interceptor.serial.SerialCommunication;
import de.dbconsult.interceptor.wifi.WifiCommunication;
import de.dbconsult.interceptor.workflow.internalqueue.InternalQueue;

public class Interceptor {


    private static long time = System.currentTimeMillis();
    private static long counter;
    private static final long DELAY = 200;
    private static boolean isTest = false;
    private static boolean deviationActive = false;

    static Orchestrator orchestrator = null;
    static DeviationController deviationController = new DeviationController();

    private static SerialsRepository serialsRepository = new SerialsRepository();
    private static WorkflowDataStore workflowDataStore = WorkflowDataStore.getInstance();
    private static WorkflowRepository workflowRepository = new WorkflowRepository(workflowDataStore);
    private static InternalQueue internalQueueMill = new InternalQueue();
    private static InternalQueue internalQueuePC = new InternalQueue();


    public static void main(String[] args) {


        isTest = args[0].startsWith("test");
        SerialPort comPort=null;

        try {
            comPort = setupSerial(args[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }

        WifiCommunication communication = WifiCommunication.getInstance();
        communication.setComPort(comPort);

        System.out.println("Configured workflow chain: ");

        try {
            for (Workflow workflow : workflowRepository.getConfiguredWorkflows()) {
                System.out.println(workflow.getClass().getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        workflowDataStore.update("WorkflowRepository", workflowRepository);
        workflowDataStore.update("deviationActive", false);
        workflowDataStore.update("doGracefulHold", false);
        workflowDataStore.update("doGracefulResume", false);
        workflowDataStore.update("lastResponse","nothin");
        workflowDataStore.update("pcQSize", 0L);
        workflowDataStore.update("millQSize", 0L);
        workflowDataStore.update("EXTRAREADER", new ExtraReader(workflowDataStore));
        ExternalLogger logger = new ExternalLogger(workflowDataStore);
        Thread loggingThread = new Thread(logger);
        loggingThread.start();

        orchestrator = new Orchestrator(serialsRepository, workflowRepository, workflowDataStore, internalQueueMill);
        counter = 0;
        // from now on all listener threads are active
        communication.startCommunication(orchestrator);
        while (true) {
            // Wait and parse forever, and ever and ever...
            try {
                // But let the threads breathe
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }



    private static SerialPort setupSerial(String portName) throws Exception {
        SerialPort port;
        int portIndex = determineComPortIndex(portName);
        if (portIndex < 0) {
            throw new RuntimeException("Cannot determine port " + portName);
        }

        port = SerialPort.getCommPorts()[portIndex];
        port.setBaudRate(115200);

        if (!port.openPort(1, 42, 1024)) {
            throw new RuntimeException("Could not open " + portName);
        }
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 10, 0);
        return port;

    }

    private static int determineComPortIndex(String portName) {
        for(int index=0; index < SerialPort.getCommPorts().length; index++) {
            if (SerialPort.getCommPorts()[index].getDescriptivePortName().toLowerCase().contains(portName.toLowerCase())) return index;
        }
        return -1;

    }

    private static WorkflowResult readFromUI() {
        WorkflowResult result = new WorkflowResult(0,TargetDevices.CANDLE,TargetDevices.CNC, "".getBytes(),0);
        if ((boolean) workflowDataStore.read("doGracefulHold")) {
            System.out.println("Setting _gracefulHoldON");
            result.setOutput("_gracefulHoldON\n".getBytes());
            result.setLen(result.getOutput().length);
            workflowDataStore.update("doGracefulHold", false);
        }
        if ((boolean) workflowDataStore.read("doGracefulResume")) {
            System.out.println("Setting _gracefulHoldOFF");
            result.setOutput("_gracefulHoldOFF\n".getBytes());
            result.setLen(result.getOutput().length);
            workflowDataStore.update("doGracefulResume", false);
        }
        return result;
    }

}