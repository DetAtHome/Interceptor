package de.dbconsult.interceptor.serial;

import com.fazecast.jSerialComm.SerialPort;
import de.dbconsult.interceptor.Communication;
import de.dbconsult.interceptor.WorkflowResult;

public class SerialCommunication implements Communication {

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

    public WorkflowResult readFully(String channel) {
        // channel is not needed here, cause extra runs on a different port and therefore no distinguishing is needed
        byte readBuffer[] = new byte[1024];
        byte read[] = new byte[1];
        int index =0;
        int numRead = comPort.readBytes(read, 1);
        WorkflowResult data = new WorkflowResult(0,null, null, "".getBytes(),0);
        if (numRead>0) {
            if (read[0] == 24 ||read[0] == '?' || read[0] == '!' || read[0] == '~') {
                // shortcut for push messages
                data.setOutput(read);
                data.setLen(numRead);
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
        data.setOutput(readBuffer);
        data.setLen(index);
        return data;
    }


    public void write(WorkflowResult data) {
        if (data.getLen()>-1) {
            if (comPort.writeBytes(data.getOutput(), data.getLen()) < 0) {
                System.out.println("Error while writing");
                System.out.println("Comport> " + comPort.getSystemPortName());
            }
        }
    }

    public void write(String data) {
        if (data.length()>0) {
            if (comPort.writeBytes(data.getBytes(),data.length()) < 0) {
                System.out.println("Error while writing");
                System.out.println("Comport> " + comPort.getSystemPortName());
            }
        }
    }
}
