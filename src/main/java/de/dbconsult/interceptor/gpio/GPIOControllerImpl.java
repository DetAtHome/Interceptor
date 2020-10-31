package de.dbconsult.interceptor.gpio;

import com.pi4j.io.gpio.*;

public class GPIOControllerImpl implements GPIOController {
    final static GpioController gpio = GpioFactory.getInstance();
    private static GpioPinPwmOutput spindle = gpio.provisionPwmOutputPin(RaspiPin.GPIO_01,0);

    static {
        spindle.setPwmRange(1000);
    }

    public void setPin(int pin, double val) {

        spindle.setPwm((int) val);
    }
}
