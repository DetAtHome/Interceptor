package de.dbconsult.interceptor.wifi;

import com.fazecast.jSerialComm.SerialPort;
import de.dbconsult.interceptor.Communication;
import de.dbconsult.interceptor.Orchestrator;
import de.dbconsult.interceptor.TargetDevices;
import de.dbconsult.interceptor.WorkflowResult;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WifiCommunication implements Communication {
    private ServerSocket listener = null;
    private Socket socket = null;
    private BufferedReader inFromSocket = null;
    private BufferedWriter outToSocket = null;
    private static WifiCommunication instance = null;
    String toMillBuffer = "";
    String toCandleBuffer = "";
    String toExtraBuffer = "";
    private Orchestrator orchestrator;

    static SerialPort comPort;

    public static void main(String[] args) throws Exception {
        String portName = "com6";
        int portIndex = determineComPortIndex(portName);
        if (portIndex < 0) {
            throw new RuntimeException("Cannot determine port " + portName);
        }

        comPort = SerialPort.getCommPorts()[portIndex];
        comPort.setBaudRate(115200);

        if (!comPort.openPort(1, 42, 1024)) {
            throw new RuntimeException("Could not open " + portName);
        }
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 10, 0);
        System.out.println("have port");
        WifiCommunication communication = WifiCommunication.getInstance();
        communication.startCommunication(null);
        System.out.println("waiting forever");
        while (true) {
            if(!communication.isConnected()) {
                communication.setupSocket();
            }

        }
    }

    public void setComPort(SerialPort port) {
        comPort = port;
    }

    private static int determineComPortIndex(String portName) {
        for(int index=0; index < SerialPort.getCommPorts().length; index++) {
            if (SerialPort.getCommPorts()[index].getDescriptivePortName().toLowerCase().contains(portName.toLowerCase())) return index;
        }
        return -1;

    }
    public static WifiCommunication getInstance() {
        if (instance==null) {

            instance = new WifiCommunication();
        }
        return instance;
    }

    private WifiCommunication() {
        try {
            listener = new ServerSocket(9023);
            setupSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startCommunication(Orchestrator orchestrator) {
        Thread listenToSocket = new Thread(new Runnable() {
            @Override
            public void run() {
                readFromMillOrExtra();
            }
        } );
        listenToSocket.start();
        /* comment this in if running from main, not needed in interceptor environment */

        Thread listenToComm = new Thread(new Runnable() {
            @Override
            public void run() {
                readFromCandle();
            }
        } );
        listenToComm.start();
        Thread dispatchRequestFromCandle = new Thread(new Runnable() {
            @Override
            public void run() {
                readAndDispatchIncomingCandle(orchestrator);
            }
        });
        dispatchRequestFromCandle.start();

        Thread dispatchAnythingFromCNC = new Thread(new Runnable() {
            @Override
            public void run() {
                readAndDispatchIncomingCNC(orchestrator);
            }
        });
        dispatchAnythingFromCNC.start();

    }

    public void readAndDispatchIncomingExtra() {
        //tbd
    }
    public void readAndDispatchIncomingCNC(Orchestrator orchestrator) {
        while(true) {
            System.out.println("Reading fully from Mill to Candle");
            WorkflowResult toCandle = readFully("tocandle");
            toCandle.setFormSource(TargetDevices.CNC);
            toCandle.setToDestination(TargetDevices.CANDLE);
            System.out.println("Got something for candle");
            WorkflowResult afterWorkflows = orchestrator.enqueueToWorkflow(toCandle);
            if(afterWorkflows.getToDestination()!=TargetDevices.ABORT) {
                writeToSerial(afterWorkflows);
            }
        }
    }

    private void writeToSerial(WorkflowResult toCandle) {
        try {
            comPort.writeBytes(toCandle.getOutput(), toCandle.getLen());
            comPort.writeBytes(new byte[] {10,13},2);
            System.out.println("sent to candle:" + new String(toCandle.getOutput()));
            System.out.println("Len was: " + toCandle.getLen());
            //          Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void readAndDispatchIncomingCandle(Orchestrator orchestrator) {
        while(true) {
            System.out.println("Read fully from Candle to Mill");
            WorkflowResult toMill = readFully("tomill");
            toMill.setFormSource(TargetDevices.CANDLE);
            toMill.setToDestination(TargetDevices.CNC);
            WorkflowResult afterWorkflows = orchestrator.enqueueToWorkflow(toMill);
            System.out.println("Got something to send to mill");
            if (afterWorkflows.getToDestination()!=TargetDevices.ABORT) {
                writeToSocket(afterWorkflows);
            }
        }

    }

    private void writeToSocket(WorkflowResult toMill) {
        char[] outData = new char[toMill.getLen()];
        for (int i = 0; i < toMill.getLen(); i++) {
            outData[i] = (char) toMill.getOutput()[i];
        }
        try {

            outToSocket.write(outData);
            outToSocket.flush();
            if(outData[0]==24) {
                System.out.println("Sent CtrlX to mill");
            } else {
                System.out.println("Sent to mill:" + new String(outData));
                System.out.println("length was btw: " + outData.length);
            }
            //            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void write(String grbl) {
        try {

        //    out.write(grbl + "\r\n");
        //    out.write(grbl + "\r");
            outToSocket.write(grbl);
            outToSocket.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public WorkflowResult readFully(String channel) {
        WorkflowResult data = new WorkflowResult(0, null, null, "".getBytes(), 0);
        String payload="";
        char cr=13;
        int index = -1;
        // find 1st complete command or answer
        while((index=indexOfCRorPushCommand(payload))<0) {
   //         System.out.println("Payload: " + payload + " length: " + payload.length() + " channel " + channel);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if ("toextra".equalsIgnoreCase(channel)) {
                payload = toExtraBuffer;
            } else if ("tocandle".equalsIgnoreCase(channel)) {
                payload = toCandleBuffer;
            } else if ("tomill".equalsIgnoreCase(channel)) {
                payload = toMillBuffer;
            }
        }

        if ("toextra".equalsIgnoreCase(channel)) {
            payload = toExtraBuffer.substring(0,index+1);
            toExtraBuffer = toExtraBuffer.substring(index+1);
        } else if ("tocandle".equalsIgnoreCase(channel)) {
            payload = toCandleBuffer.substring(0,index+1);
            toCandleBuffer = toCandleBuffer.substring(index+1);
        } else if ("tomill".equalsIgnoreCase(channel)) {
            payload = toMillBuffer.substring(0,index+1);
            toMillBuffer = toMillBuffer.substring(index+1);
        }


            System.out.println("payload is " + payload + " length: " + payload.length());

            data.setOutput(payload.getBytes());
            data.setLen(payload.length());


        return data;
    }

    private int indexOfCRorPushCommand(String data) {
        int indexCR = -1;
        int indexQst = -1;
        int indexBang = -1;
        int indexTild = -1;
        int indexCtrlX = -1;

        int minFoundIndex = 99999;
        char cr = 13;
        char ctrlx = 24;
        char qst = '?';
        char bang = '!';
        char tlde = '~';

        indexCR = data.indexOf(cr);
        indexQst = data.indexOf(qst);
        indexBang = data.indexOf(bang);
        indexTild = data.indexOf(tlde);
        indexCtrlX = data.indexOf(ctrlx);


        if (indexCR>-1) minFoundIndex = indexCR;
        if (indexQst>-1 && indexQst<minFoundIndex) minFoundIndex = indexQst;
        if (indexBang>-1 && indexBang<minFoundIndex) minFoundIndex = indexBang;
        if (indexTild>-1 && indexTild<minFoundIndex) minFoundIndex = indexTild;
        if (indexCtrlX>-1 && indexCtrlX<minFoundIndex) minFoundIndex = indexCtrlX;

        if(minFoundIndex==99999) minFoundIndex=-1;
        return minFoundIndex;


    }


    @Override
    public void write(WorkflowResult data) {

    }

    public void writeToCandle(WorkflowResult data) {

        if (data.getLen()>-1) {
            comPort.writeBytes(data.getOutput(),data.getLen());
        }

    }
    private void setupSocket() {
        try {
            socket = listener.accept();
            socket.setKeepAlive(true);
            inFromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outToSocket = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if(!socket.isConnected()) System.out.println("Reconnection needed");
        return socket.isConnected();
    }

    public void readFromMillOrExtra() {
        char[] dataFromMill = new char[1];
        try {
            boolean extraDetected=false;

            while (true) {
                String inputLine="void";
                if(!isConnected()) {
                    setupSocket();
                }
               if(inFromSocket.read(dataFromMill,0,1)>0) {
                    if(dataFromMill[0]=='#') {
                        extraDetected=true;
                    }
                    if(extraDetected) {
                        toExtraBuffer = toExtraBuffer + dataFromMill[0];
                    } else {
                        toCandleBuffer = toCandleBuffer + dataFromMill[0];
                    }
                    // Convention for now and always> Extra starts with # and ends with CR (no LF involved)
                    if(extraDetected && dataFromMill[0]==13) {
                        extraDetected=false;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readFromCandle() {
        try {
            while (true) {
                byte[] outAsByte = new byte[1];
                if(comPort.readBytes(outAsByte,1)>0) {
                    toMillBuffer = toMillBuffer + (char)outAsByte[0];
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

