package de.dbconsult.interceptor.serial;

import com.fazecast.jSerialComm.SerialPort;

public class SerialCommunication {

    int baud;
    String portName, name;
    int timeout;
    SerialPort comPort;
    boolean portOpen = false;
    String lineFragment = new String();

    public SerialCommunication(String name, String portName, int baud, int timeout) {
        this.baud = baud;
        this.portName = portName;
        this.name = name;
        this.timeout = timeout;

        if(!name.startsWith("test")) {
            int portIndex = determineComPortIndex(portName);
            if (portIndex < 0) {
                throw new RuntimeException("Cannot determine port " + portName);
            }

            comPort = SerialPort.getCommPorts()[portIndex];
            comPort.setBaudRate(baud);

            portOpen = comPort.openPort(1, 42, 1024);
            if (!portOpen) {
                throw new RuntimeException("Could not open " + portName);
            }
            comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, timeout, 0);
        }
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
        byte readBuffer[] = new byte[1024];
        byte read[] = new byte[1];
        int index =0;
        int numRead = comPort.readBytes(read, 1);
        SerialData data = new SerialData();
        if (numRead>0) {
            if (read[0] == 24 ||read[0] == '?' || read[0] == '!' || read[0] == '~') {
                // shortcut for push messages
                data.setData(read);
                data.setLen(numRead);
                data.setAsString(new String(read, 0, numRead));
                return data;
            }
        }
       while (numRead>0) {
            readBuffer[index] = read[0];
            index++;
            // continue reading
            if (read[0] == 13) {
                break;
            }
            else {
                numRead = comPort.readBytes(read, 1);
            }
        }
        data.setData(readBuffer);
        data.setLen(index);
        data.setAsString(new String(readBuffer, 0, index));
        return data;
    }


    public void write(SerialData data) {
        if (data.getLen()>-1)
            comPort.writeBytes(data.getData(),data.getLen());
    }
}
