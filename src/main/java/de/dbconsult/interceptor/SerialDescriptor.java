package de.dbconsult.interceptor;

public class SerialDescriptor {

    public static final int SWALLOW = 0;
    public static final int serialConnection = 1;         // PC communication
    public static final int usbConnection = 2;            // Mill communication

    public final int serialId;
    public final String portName;
    public final String name;
    private Communication comm;

    public SerialDescriptor(int id, String name, String portName) {
        serialId = id;
        this.name = name;
        this.portName = portName;
    }

    public void setComm(Communication comm) {
        this.comm = comm;
    }

    public Communication getComm() {
        return comm;
    }

    public String getPortName() {
        return portName;
    }

    public String getName() {
        return name;
    }
}
