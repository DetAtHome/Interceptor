package de.dbconsult.interceptor;

import de.dbconsult.interceptor.serial.SerialCommunication;
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
        SerialDescriptor[] serials = parseArguments(args);

        try {
            setupSerials(serials);
        } catch (Exception e) {
            e.printStackTrace();
        }
        workflowDataStore.update("SerialsRepository", serialsRepository);
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
        deviationController.initialize(workflowDataStore);

        orchestrator = new Orchestrator(serialsRepository, workflowRepository, workflowDataStore, internalQueueMill);
        counter = 0;
        while (true) {
            if (workflowDataStore.read("ZMaskInProgress")!=null) continue;
            counter++;
            deviationActive = ((boolean)workflowDataStore.read("deviationActive"));

            // get from 1
            WorkflowResult request = null;
            request = readFromUI();
            request = deviationController.process(request);
            if (internalQueuePC.size() > 0 && !deviationActive) {
                request = readFromQueue(true);
                System.out.print("sending from internal pc q: " + new String(request.getOutput()) + " new size: " + internalQueuePC.size());
            }
            if (request==null || request.getLen() == 0) {
                request = readFrom(true, isTest);
            }
            // enqueue
            if (request.getLen() > 0) {
                if ((boolean)workflowDataStore.read("deviationActive")) {
                    internalQueuePC.enqueue(request);
                    workflowDataStore.update("pcQSize", internalQueuePC.size());
                    System.out.println("stored to internal pc q: " + new String(request.getOutput()) + " size is now" + internalQueuePC.size());
                } else {
                    request = orchestrator.enqueueToWorkflow(request);
                    writeRequest(request, isTest);
                }
            }
            // get from 2
            WorkflowResult response;

            if(internalQueueMill.size()>0 && !deviationActive) {
                response = readFromQueue(false);
                System.out.print("sending from internal mill q: " + new String(response.getOutput()) + " new size: " + internalQueueMill.size());

            } else {
                response = readFrom(false, isTest);
            }
            // enqueue
            if (response.getLen() > 0) {
                if (deviationActive) {
                    internalQueueMill.enqueue(response);
                    workflowDataStore.update("millQSize", internalQueueMill.size());
                    System.out.println("stored to internal mill q: " + new String(response.getOutput()) + " size is now" + internalQueueMill.size());

                } else {
                    response = orchestrator.enqueueToWorkflow(response);
                    writeRequest(response, isTest);
                }
            }

        }
    }

    private static SerialDescriptor[] parseArguments(String[] args) {
        if (args == null || args.length < 6 || args.length % 2 != 0) {
            System.out.println("USAGE: ... serialPCName serial1Port serialMillName serial2Port serialExtraName serial3Port");
            System.exit(-1);
        }
        SerialDescriptor[] result = new SerialDescriptor[args.length / 2];
        for (int index = 0; index < args.length; index += 2) {
            SerialDescriptor serial = new SerialDescriptor(index / 2 + 1, args[index], args[index + 1]);
            result[index / 2] = serial;
        }
        return result;
    }

    private static void setupSerials(SerialDescriptor[] serials) throws Exception {
        SerialCommunication com1 = new SerialCommunication(serials[0].getName(), serials[0].getPortName(), 115200, 10);
        SerialCommunication com2 = new SerialCommunication(serials[1].getName(), serials[1].getPortName(), 115200, 10);
        SerialCommunication com3 = new SerialCommunication(serials[2].getName(), serials[2].getPortName(), 115200, 10);
        serials[0].setComm(com1);
        serials[1].setComm(com2);
        serials[2].setComm(com3);
        serialsRepository.setPc(serials[0]);
        serialsRepository.setMill(serials[1]);
        serialsRepository.setExtra(serials[2]);

    }

    private static WorkflowResult readFrom(boolean isPc, boolean isTest) {
        WorkflowResult request = null;
        if (isTest) {
            time = System.currentTimeMillis();
            request = new WorkflowResult(0, null, null, "".getBytes(),0);
            request.setOutput((counter + " - " + System.currentTimeMillis()).getBytes());
            request.setLen(request.getOutput().length);
            while (System.currentTimeMillis() - time < DELAY) {
            }
        } else {
            if (isPc)
                request = serialsRepository.getPc().getComm().readFully();
            else
                request = serialsRepository.getMill().getComm().readFully();
        }
        if (isPc) {
            request.setFormSource(serialsRepository.getPc());
            request.setToDestination(serialsRepository.getMill());
        } else {
            request.setFormSource(serialsRepository.getMill());
            request.setToDestination(serialsRepository.getPc());
        }
        return request;
    }

    private static WorkflowResult readFromQueue(boolean isPc) {
        WorkflowResult result;


        if (isPc) {
            result = internalQueuePC.dequeue();
            workflowDataStore.update("pcQSize", internalQueuePC.size());
            result.setFormSource(serialsRepository.getPc());
            result.setToDestination(serialsRepository.getMill());
        } else {

            result = internalQueueMill.dequeue();
            workflowDataStore.update("millQSize", internalQueueMill.size());
            result.setFormSource(serialsRepository.getMill());
            result.setToDestination(serialsRepository.getPc());
        }
        return result;
    }

    private static WorkflowResult readFromUI() {
        WorkflowResult result = new WorkflowResult(0,serialsRepository.getPc(),serialsRepository.getMill(), "".getBytes(),0);
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

    private static void writeRequest(WorkflowResult result, boolean isTest) {

        if(!isTest) result.getToDestination().getComm().write(result);

    }
}