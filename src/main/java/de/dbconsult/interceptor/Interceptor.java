package de.dbconsult.interceptor;

import de.dbconsult.interceptor.serial.SerialCommunication;
import de.dbconsult.interceptor.serial.SerialData;
import de.dbconsult.interceptor.serial.TwoWaySerialComm;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinDirection;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.event.PinEventType;

import java.util.StringTokenizer;

public class Interceptor {


    private static long time = System.currentTimeMillis();
    private static final long DELAY=200;
    private static boolean tooggle = false;
    private static boolean dtrChange = false;

    public static void main(String[] args) {

        System.out.println("Configured workflow chain: ");
        for(Workflow workflow:WorkflowRepository.getInstance().getConfiguredWorkflows()) {
            System.out.println(workflow.getClass().getName());
        }

        SerialDescriptor[] serials = parseArguments(args);
        setupSerials(serials);

        while(true) {
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
            SerialData request = SerialsRepository.getInstance().getPc().getComm().readFully();
            // enqueue
            if(request!=null && request.getLen()>0)
                Orchestrator.getInstance().enqueueToWorkflow(serials[0], request.getData(), request.getLen());
            // get from 2
            SerialData response = SerialsRepository.getInstance().getMill().getComm().readFully();
            // enqueue
            if(response!=null && response.getLen()>0)
                Orchestrator.getInstance().enqueueToWorkflow(serials[1], response.getData(), response.getLen());

        }
    }

    private static SerialDescriptor[] parseArguments(String[] args) {
        if (args==null || args.length<4 || args.length%2!=0) {
            System.out.println("USAGE: ... serial1Name serial1Port serial2Name serial2Port");
            System.exit(-1);
        }
        SerialDescriptor[] result = new SerialDescriptor[args.length/2];
        for(int index=0;index<args.length;index+=2) {
            SerialDescriptor serial = new SerialDescriptor(index/2+1,args[index], args[index+1]);
            result[index/2] = serial;
        }
        return result;
    }

    private static void setupSerials(SerialDescriptor[] serials) {
        SerialCommunication com1 = new SerialCommunication(serials[0].getName(), serials[0].getPortName(), 115200, 10);
        SerialCommunication com2 = new SerialCommunication(serials[1].getName(), serials[1].getPortName(), 115200, 10);
        serials[0].setComm(com1);
        serials[1].setComm(com2);
        SerialsRepository.getInstance().setPc(serials[0]);
        SerialsRepository.getInstance().setMill(serials[1]);

    }

}
