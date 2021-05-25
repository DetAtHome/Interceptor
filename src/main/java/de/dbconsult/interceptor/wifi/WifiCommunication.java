package de.dbconsult.interceptor.wifi;

import com.fazecast.jSerialComm.SerialPort;
import de.dbconsult.interceptor.Communication;
import de.dbconsult.interceptor.Orchestrator;
import de.dbconsult.interceptor.TargetDevices;
import de.dbconsult.interceptor.WorkflowResult;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WifiCommunication {
    private ServerSocket listener = null;
    private ServerSocket extraListener = null;

    private Socket socket = null;
    private Socket extraSocket = null;
    private BufferedReader inFromSocket = null;
    private BufferedReader inFromExtraSocket = null;
    private BufferedWriter outToSocket = null;
    private BufferedWriter outToExtraSocket = null;
    private static WifiCommunication instance = null;
    String toMillBuffer = "";
    String toCandleBuffer = "";
    String toExtraBuffer = "";
    private Orchestrator orchestrator;
    boolean answerFromExtraRead = false;
    String lastExtraAnswer="";

    static SerialPort comPort;

    boolean stopThreads = true;
    boolean isWaitingToAccept = true;
    long lastHeartbeatSent = System.currentTimeMillis();
    long lastHeartbeatReceived = System.currentTimeMillis();

    Thread readAndSignalAnyExtra = null;
    Thread dispatchAnythingFromCNC = null;
    Thread dispatchRequestFromCandle = null;
    Thread listenToSocket = null;
    Thread listenToExtraSocket = null;
    Thread listenToComm = null;
    Thread heartbeat = null;

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
        }
    }

    public void setComPort(SerialPort port) {
        comPort = port;
    }
    public void setOrchestrator(Orchestrator o) {
        orchestrator = o;
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
            extraListener = new ServerSocket(9024);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCommunication(Orchestrator orchestrator) {

        heartbeat = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!stopThreads) {
                    sendHeartBeat();
                    checkLastHeartbeatReceived();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
     //   heartbeat.start();

        listenToSocket = new Thread(new Runnable() {
            @Override
            public void run() {
                readFromMill();
            }
        } );

        listenToSocket.start();
        /* comment this in if running from main, not needed in interceptor environment */
        listenToExtraSocket = new Thread(new Runnable() {
            @Override
            public void run() {
                readFromExtra();
            }
        } );
        listenToExtraSocket.start();
        /* comment this in if running from main, not needed in interceptor environment */

        listenToComm = new Thread(new Runnable() {
            @Override
            public void run() {
                readFromCandle();
            }
        } );
        listenToComm.start();
        dispatchRequestFromCandle = new Thread(new Runnable() {
            @Override
            public void run() {
                readAndDispatchIncomingCandle(orchestrator);
            }
        });
        dispatchRequestFromCandle.start();

        dispatchAnythingFromCNC = new Thread(new Runnable() {
            @Override
            public void run() {
                readAndDispatchIncomingCNC(orchestrator);
            }
        });
        dispatchAnythingFromCNC.start();

        readAndSignalAnyExtra = new Thread(new Runnable() {
            @Override
            public void run() {
                readAndDispatchIncomingExtra(orchestrator);
            }
        });
        readAndSignalAnyExtra.start();

    }

    public void readAndDispatchIncomingExtra(Orchestrator orchestrator) {
        while(!stopThreads) {
   //         System.out.println("Reading fully from ExtraAnswer");
            WorkflowResult toExtra = readFully("toextra");
            if (orchestrator==null) {
                // direct pingpong
                toExtra.setFormSource(TargetDevices.EXTRA);
                toExtra.setToDestination(TargetDevices.EXTRA);
                answerFromExtraRead = true;
                lastExtraAnswer = lastExtraAnswer + new String(toExtra.getOutput());
            } else {
                toExtra.setFormSource(TargetDevices.EXTRA);
                toExtra.setToDestination(TargetDevices.SKIP);
                WorkflowResult afterWorkflows = orchestrator.enqueueToWorkflow(toExtra);
            }
        }
    }

    public boolean hasExtraAnswer() {
        return answerFromExtraRead;
    }
    public void markExtraAnswerConsumed() {
        answerFromExtraRead = false;
        lastExtraAnswer="";
    }
    public String getLastExtraAnswer() {
        return lastExtraAnswer;
    }

    public void readAndDispatchIncomingCNC(Orchestrator orchestrator) {
        while(!stopThreads) {
//            System.out.println("Reading fully from Mill to Candle");
            WorkflowResult toCandle = readFully("tocandle");
            toCandle.setFormSource(TargetDevices.CNC);
            toCandle.setToDestination(TargetDevices.CANDLE);
//            System.out.println("Got something for candle: " + new String(toCandle.getOutput()));
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
//            System.out.println("sent to candle:" + new String(toCandle.getOutput()));
//            System.out.println("Len was: " + toCandle.getLen());
            //          Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void readAndDispatchIncomingCandle(Orchestrator orchestrator) {
        while(!stopThreads) {
 //          System.out.println("Read fully from Candle to Mill");
            WorkflowResult toMill = readFully("tomill");
            toMill.setFormSource(TargetDevices.CANDLE);
            toMill.setToDestination(TargetDevices.CNC);
            WorkflowResult afterWorkflows = orchestrator.enqueueToWorkflow(toMill);
 //           System.out.println("Got something to send to mill");
            if (afterWorkflows.getToDestination()!=TargetDevices.ABORT) {
                writeToSocket(afterWorkflows);
            }
        }

    }

    public void writeToSocket(WorkflowResult toCNCOrExtra) {
        char[] outData = new char[toCNCOrExtra.getLen()];
        for (int i = 0; i < toCNCOrExtra.getLen(); i++) {
            outData[i] = (char) toCNCOrExtra.getOutput()[i];
        }
        try {
            if (toCNCOrExtra.getToDestination() == TargetDevices.CNC) {
                outToSocket.write(outData);
                outToSocket.flush();
            } else if(toCNCOrExtra.getToDestination() == TargetDevices.EXTRA) {
                outToExtraSocket.write(outData);
                outToExtraSocket.flush();

            }
 /*           if(outData[0]==24) {
                System.out.println("Sent CtrlX to mill");
            } else {
                System.out.println("Sent to mill:" + new String(outData));
                System.out.println("length was btw: " + outData.length);
            }
            //            Thread.sleep(10);*/
        } catch(IOException ioe) {
            // connection reset by peer?
            System.out.println("Connection reset?");
            setupSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // convenience method for sending stuff without a WorkflowResult object only to CNC
    public void directWriteToCNC(String grbl) {
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
       //     System.out.println("Payload: " + payload + " length: " + payload.length() + " channel " + channel);
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


   //         System.out.println("payload is " + payload + " length: " + payload.length());

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

    public synchronized void setupSocket() {
        try {
            stopThreads = true;
            Thread.sleep(10); // give threads time to terminate
            System.out.println("Waiting for incoming socket");
            isWaitingToAccept = true;
            socket = listener.accept();
            System.out.println("Accepted socket ");

            System.out.println("Waiting for incoming extra socket");
            extraSocket = extraListener.accept();
            isWaitingToAccept = false;
            System.out.println("Accepted socket ");
            socket.setKeepAlive(true);
            extraSocket.setKeepAlive(true);
            inFromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outToSocket = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            inFromExtraSocket = new BufferedReader(new InputStreamReader(extraSocket.getInputStream()));
            outToExtraSocket = new BufferedWriter(new OutputStreamWriter(extraSocket.getOutputStream()));
            stopThreads = false;
            startCommunication(orchestrator);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void readFromExtra() {
        char[] dataFromExtra = new char[1];
        try {
            while (!stopThreads) {
                if(inFromExtraSocket.read(dataFromExtra,0,1)>0) {
                    toExtraBuffer = toExtraBuffer + dataFromExtra[0];
                    // Convention for now and always> Extra starts with # and ends with CR (no LF involved)
//                    if (dataFromExtra[0]==13){
//                        Date dte = new Date(System.currentTimeMillis());
//                        SimpleDateFormat fmt = new SimpleDateFormat("hh:mm:ss");
//                        System.out.println(fmt.format(dte) + "Answer from Extra:" + toExtraBuffer);
//                    }
//                } else {
//                    // socket lost
//                    System.out.println("cannot read from extra socket, renew");
//                    setupSocket();
                }

            }
        } catch (SocketException e) {
            System.out.println("No extra socket, renewing");
            setupSocket();
        } catch (SocketTimeoutException te) {
            System.out.println("Extra Socket timeout, renewing");
            setupSocket();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void readFromMill() {
        char[] dataFromMill = new char[1];
        try {
            while (!stopThreads) {
               if(inFromSocket.read(dataFromMill,0,1)>0) {
  //                  if(dataFromMill[0]==94) {
  //                      lastHeartbeatReceived = System.currentTimeMillis();
  //                  } else {
                        toCandleBuffer = toCandleBuffer + dataFromMill[0];

//                    if(dataFromMill[0]==13) {
//                        Date dte = new Date(System.currentTimeMillis());
//                        SimpleDateFormat fmt = new SimpleDateFormat("hh:mm:ss");
//                        System.out.println(fmt.format(dte) + "Answer from CNC:" + toCandleBuffer);
//                    }
   //                 }
//                } else {
//                   // socket lost
//                   System.out.println("cannot read from socket, renew");
//                   setupSocket();
               }

            }
        } catch (SocketException e) {
            System.out.println("No socket, renewing");
            setupSocket();
        } catch (SocketTimeoutException te) {
            System.out.println("Socket timeout, renewing");
            setupSocket();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void readFromCandle() {
        try {
            while (!stopThreads) {
                byte[] outAsByte = new byte[1];
                if(comPort.readBytes(outAsByte,1)>0) {
                    toMillBuffer = toMillBuffer + (char)outAsByte[0];
                    if(outAsByte[0]==13) {
                        Date dte = new Date(System.currentTimeMillis());
                        SimpleDateFormat fmt = new SimpleDateFormat("hh:mm:ss");
  //                      System.out.println(fmt.format(dte) + "Read from ComPort:" + toMillBuffer);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendHeartBeat() {
        try {
            if (isWaitingToAccept) return;
            if(System.currentTimeMillis()-lastHeartbeatSent>1000) {
                outToSocket.write(94);
                outToSocket.flush();
                lastHeartbeatSent = System.currentTimeMillis();
            }
        } catch (Exception e) {
            setupSocket();
        }

    }

    private void checkLastHeartbeatReceived() {
        if(System.currentTimeMillis()-lastHeartbeatReceived>2000 &&!isWaitingToAccept) {
            System.out.println("heartbeat failed, System time: " + System.currentTimeMillis() + " last: " + lastHeartbeatReceived + " difference: " + (System.currentTimeMillis()-lastHeartbeatReceived));
     //      setupSocket();
            lastHeartbeatReceived = System.currentTimeMillis();
        }
    }
}

