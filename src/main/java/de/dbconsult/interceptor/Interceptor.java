package de.dbconsult.interceptor;

import de.dbconsult.interceptor.serial.SerialCommunication;
import de.dbconsult.interceptor.serial.SerialData;

public class Interceptor {


    private static long time = System.currentTimeMillis();
    private static long counter;
    private static final long DELAY=200;
    private static boolean tooggle = false;
    private static boolean dtrChange = false;

    public static void main(String[] args) {


        SerialDescriptor[] serials = parseArguments(args);
        try {
            setupSerials(serials);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Configured workflow chain: ");
        for(Workflow workflow:WorkflowRepository.getInstance().getConfiguredWorkflows()) {
            System.out.println(workflow.getClass().getName());
        }
        counter = 0;
        while(true) {
            counter++;
/*            SerialData request = SerialsRepository.getInstance().getPc().getComm().readFully();
            StringTokenizer commandTokenizer = new StringTokenizer(request.getAsString(),"\r\n");
            while (commandTokenizer.hasMoreTokens()) {
                if(request.getLen()>0) {
                    byte[] data = (commandTokenizer.nextToken() + "\r").getBytes();
                    Orchestrator.getInstance().enqueueToWorkflow(serials[0], data, data.length);
                    SerialData response = SerialsRepository.getInstance().getMill().getComm().readFully();
                    if(response.getLen()>0)
                        Orchestrator.getInstance().enqueueToWorkflow(serials[1], response.getData(), response.getLen());
                }
            }
*/            // get from 1
            SerialData request = new SerialData();
            SerialData response = new SerialData();
            if(args[0].startsWith("test")) {
                 time = System.currentTimeMillis();
                 request.setAsString(counter + " - " + System.currentTimeMillis());
                 request.setData(request.getAsString().getBytes());
                 request.setLen(request.getData().length);
                 while(System.currentTimeMillis() - time <DELAY) {}
            } else {
                request = SerialsRepository.getInstance().getPc().getComm().readFully();
            }

            // enqueue
            if (request != null && request.getLen() > 0)
                Orchestrator.getInstance().enqueueToWorkflow(counter, serials[0], request.getData(), request.getLen());
            // get from 2
            if(args[2].startsWith("test")) {
                time = System.currentTimeMillis();
                response.setAsString(counter + " - " + System.currentTimeMillis());
                response.setData(response.getAsString().getBytes());
                response.setLen(response.getData().length);
                while(System.currentTimeMillis() - time <DELAY) {}

            } else {
                response = SerialsRepository.getInstance().getMill().getComm().readFully();
            }
            // enqueue
            if (response != null && response.getLen() > 0)
                Orchestrator.getInstance().enqueueToWorkflow(counter, serials[1], response.getData(), response.getLen());

        }
    }

    private static SerialDescriptor[] parseArguments(String[] args) {
        if (args==null || args.length<6 || args.length%2!=0) {
            System.out.println("USAGE: ... serialPCName serial1Port serialMillName serial2Port serialExtraName serial3Port");
            System.exit(-1);
        }
        SerialDescriptor[] result = new SerialDescriptor[args.length/2];
        for(int index=0;index<args.length;index+=2) {
            SerialDescriptor serial = new SerialDescriptor(index/2+1,args[index], args[index+1]);
            result[index/2] = serial;
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
        SerialsRepository.getInstance().setPc(serials[0]);
        SerialsRepository.getInstance().setMill(serials[1]);
        SerialsRepository.getInstance().setExtra(serials[2]);

    }

}
