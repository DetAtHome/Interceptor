package de.dbconsult.interceptor.workflow;

import com.pi4j.io.gpio.*;
import de.dbconsult.interceptor.TargetDevices;
import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;

import java.util.StringTokenizer;

public class ControlSpindle implements Workflow {
    // create gpio controller instance
    final static GpioController gpio = GpioFactory.getInstance();
    private static GpioPinDigitalOutput myLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07,
            "My LED",           // PIN FRIENDLY NAME (optional)
            PinState.LOW);      // PIN STARTUP STATE (optional)
    private static GpioPinDigitalInput dtr = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01,
            "DTR",                   // PIN FRIENDLY NAME (optional)
            PinPullResistance.PULL_DOWN); // PIN RESISTANCE (optional)
    double desiredSpindleSpeed=0;

    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {

    }

    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {
        if (data.getFormSource()!=TargetDevices.CANDLE) {
            return data;
        }

        String command = new String(data.getOutput(),0,data.getLen());
        // filter out comments
        if(command.contains("(")) return data;

        if(command.contains("S")) {
            System.out.println("Original> " + command + " len> " + data.getLen());

            int sIndex = command.indexOf("S");
            command = command.concat(" ");
            String param = command.substring(sIndex + 1).trim();
            StringTokenizer tokenizer = new StringTokenizer(param, "\n\r ");
            String val = tokenizer.nextToken();
            try {
                desiredSpindleSpeed = Double.parseDouble(val);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("Val was: '" + val + '"');
            }
            System.out.println("Desired Speed: " + desiredSpindleSpeed);
        }

        if(command.contains("M3") || command.contains("M03")) {
           myLed.setState(true);
        }

        if(command.contains("M5") || command.contains("M05")) {
            myLed.setState(false);
        }


        return data;

    }

}
