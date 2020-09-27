package de.dbconsult.interceptor.serial;

import com.fazecast.jSerialComm.SerialPort;

public class SerialCommunication {

    int baud;
    String portName, name;
    int timeout;
    SerialPort comPort;
    boolean portOpen = false;

    public SerialCommunication(String name, String portName, int baud, int timeout) {
        this.baud = baud;
        this.portName = portName;
        this.name = name;
        this.timeout = timeout;

        int portIndex = determineComPortIndex(portName);
        if(portIndex<0) {
            throw  new RuntimeException("Cannot determine port " + portName);
        }

        comPort = SerialPort.getCommPorts()[portIndex];
        comPort.setBaudRate(baud);

        portOpen = comPort.openPort(1,42,1024);
        if (!portOpen) {
            throw new RuntimeException("Could not open " + portName);
        }
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, timeout, 0);
    }

    private int determineComPortIndex(String portName) {
        for(int index=0; index < SerialPort.getCommPorts().length; index++) {
            if (SerialPort.getCommPorts()[index].getDescriptivePortName().toLowerCase().contains(portName.toLowerCase())) return index;
        }
        return -1;

    }

    public SerialPort getPort() {
        if (portOpen) return comPort;
        else return null;
    }

    public void enumerate() {
        for(SerialPort port:SerialPort.getCommPorts()) {
            System.out.println(port.getDescriptivePortName());
        }
    }
    public SerialData readFully() {
        byte[] readBuffer = new byte[1024];
        int numRead = comPort.readBytes(readBuffer, readBuffer.length);
        SerialData data = new SerialData();
        if (numRead > 0) {
            data.setData(readBuffer);
            data.setLen(numRead);
            data.setAsString(new String(readBuffer, 0, numRead));
        } else {
            data.setLen(-1);
            data.setAsString("Nothing");
        }
        return data;
    }

    public void write(SerialData data) {
        if (data.getLen()>-1)
            comPort.writeBytes(data.getData(),data.getLen());
    }
}
