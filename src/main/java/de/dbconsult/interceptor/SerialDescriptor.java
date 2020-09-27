package de.dbconsult.interceptor;

import de.dbconsult.interceptor.serial.SerialCommunication;

public class SerialDescriptor {

    public static final int SWALLOW = 0;
    public static final int serialConnection = 1;         // PC communication
    public static final int usbConnection = 2;            // Mill communication

    public final int serialId;
    public final String portName;
    public final String name;
    private SerialCommunication comm;

    public SerialDescriptor(int id, String name, String portName) {
        serialId = id;
        this.name = name;
        this.portName = portName;
    }

    public void setComm(SerialCommunication comm) {
        this.comm = comm;
    }

    public SerialCommunication getComm() {
        return comm;
    }

    public String getPortName() {
        return portName;
    }

    public String getName() {
        return name;
    }
}
