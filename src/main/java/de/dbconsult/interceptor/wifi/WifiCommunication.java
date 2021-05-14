package de.dbconsult.interceptor.wifi;

import com.fazecast.jSerialComm.SerialPort;
import de.dbconsult.interceptor.Communication;
import de.dbconsult.interceptor.WorkflowResult;
import org.jdom.output.EscapeStrategy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class WifiCommunication implements Communication {
    private ServerSocket listener = null;
    private Socket socket = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    private ArrayList<String> answerBuffer = new ArrayList<>();
    private static WifiCommunication instance = null;
    byte[] outMillBuffer = new byte[1024];
    byte[] outExtraBuffer = new byte[1024];
    PipedInputStream toCandleReadStream = new PipedInputStream();
    PipedInputStream toMillReadStream = new PipedInputStream();
    PipedInputStream toExtraReadStream = new PipedInputStream();
    PipedOutputStream toCandlePipedOutStream = null;
    PipedOutputStream toMillPipedOutStream = null;
    PipedOutputStream toExtraPipedOutStream = null;
    ByteArrayOutputStream toCandleOut = new ByteArrayOutputStream(1024);
    ByteArrayOutputStream toExtraOut = new ByteArrayOutputStream(1024);
    static SerialPort comPort;
    private char[] dataFromMill = new char[1];
    private byte[] dataFromCandle = new byte[1];

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
        System.out.println("waiting forever");
        while (true) {
            if(!communication.isConnected()) {
                communication.setupSocket();
            }

            // the threads will take care...

//            WorkflowResult answer = communication.readFully();
//            System.out.println(new String(answer.getOutput()));
//            communication.writeToCandle(answer);
//            System.out.println(new String(communication.readFully(true).getOutput()));
        }
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
            toCandlePipedOutStream = new PipedOutputStream(toCandleReadStream);
            toMillPipedOutStream = new PipedOutputStream(toMillReadStream);
            toExtraPipedOutStream = new PipedOutputStream(toExtraReadStream);
            listener = new ServerSocket(9023);
            setupSocket();
            Thread listenToSocket = new Thread(new Runnable() {
                @Override
                public void run() {
                    readFromMill();
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
                    readAndDispatchIncomingCandle();
                }
            });
            dispatchRequestFromCandle.start();

            Thread dispatchAnythingFromCNC = new Thread(new Runnable() {
                @Override
                public void run() {
                    readAndDispatchIncomingCNC();
                }
            });
            dispatchAnythingFromCNC.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readAndDispatchIncomingExtra() {
        //tbd
    }
    public void readAndDispatchIncomingCNC() {
        while(true) {
            System.out.println("Reading fully from Mill to Candle");
            WorkflowResult toCandle = readFully("tocandle");
            System.out.println("Got something for candle");
            // do somthing with it if appropriate (orchestrator)
            // but for now: send it where it belongs
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
    }
    public void readAndDispatchIncomingCandle() {
        while(true) {
            System.out.println("Read fully from Candle to Mill");
            WorkflowResult toMill = readFully("tomill");
            System.out.println("Got something to send to mill");

            // do somthing with it if appropriate (orchestrator)
            // but for now: send it where it belongs
            char[] outData = new char[toMill.getLen()];
            for (int i = 0; i < toMill.getLen(); i++) {
                outData[i] = (char) toMill.getOutput()[i];
            }
            try {

                out.write(outData);
                out.flush();
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

    }

    public void write(String grbl) {
        try {

        //    out.write(grbl + "\r\n");
        //    out.write(grbl + "\r");
            out.write(grbl);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    TAKE EXTREME CARE!!!!
    readFully is blocking!!
    That means: No answers from cnc will be passed while waiting for a BrightBricks response and vice
    versa. It does not have to be this way when exposing the piped streams and simply signaling a cr
    but rest of the code is not working this way. This would be a complete rewrite. Hmmmm.
     */
    public WorkflowResult readFully(String channel) {
        WorkflowResult data = new WorkflowResult(0, null, null, "".getBytes(), 0);
        String payload="";
        char c=0;
        try {
            while (true) {
                String info = "none";
                if ("toextra".equalsIgnoreCase(channel)) {
                    c = (char) toExtraReadStream.read();
                } else if("tocandle".equalsIgnoreCase(channel)) {
                    c = (char) toCandleReadStream.read();
                    info = "toCandle: " + c;
                } else if("tomill".equalsIgnoreCase(channel)) {
                    c = (char) toMillReadStream.read();
                    // do not wait for cr in those cases a cr will not be sent
                    // CtrlX (=24), ?, ! and ~
                    info = "toMill: " + c;
                    if (c == 24 ||c == '?' || c == '!' || c == '~') {
                        payload= "" + c;
                        break;
                    }
                } else {
                    throw new RuntimeException("unknown channel: " + channel );
                }
                payload = payload + c;
      //          Thread.sleep(1);
                if(c==13) break;
            }
            System.out.println("payload is " + payload);

            data.setOutput(payload.getBytes());
            data.setLen(payload.length());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public void write(WorkflowResult data) {
        if (data.getLen()>-1) {
            write(new String(data.getOutput()));
        }
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
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if(!socket.isConnected()) System.out.println("Reconnection needed");
        return socket.isConnected();
    }

    public void readFromMill() {
        try {
            boolean extraDetected=false;

            while (true) {
                String inputLine="void";
                if(!isConnected()) {
                    setupSocket();
                }
               if(in.read(dataFromMill,0,1)>0) {
                    byte[] outAsByte = new byte[1];
                    outAsByte[0] = (byte) dataFromMill[0];
                    if(outAsByte[0]=='#') {
                        extraDetected=true;
                    }
                    if(extraDetected) {
                        toExtraPipedOutStream.write(outAsByte);
                    } else {
                        toCandlePipedOutStream.write(outAsByte);
                    }
                    if(extraDetected && outAsByte[0]==13) {
                        extraDetected=false;
                    }
//                    char c = new Character(dataFromMill[0]);
//                    System.out.print(c);
//                    toCandleOut.write(outAsByte);
    //                comPort.writeBytes(outAsByte,1);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/* This is here for testing purposes if main is needed */

    public void readFromCandle() {
        try {
            while (true) {
                byte[] outAsByte = new byte[1];
                if(comPort.readBytes(outAsByte,1)>0) {
                    toMillPipedOutStream.write(outAsByte);
                }
/*
                if(comPort.readBytes(dataFromCandle,1)>0) {
                    char[] outAsChar = new char[1];
                    outAsChar[0] = (char)dataFromCandle[0];
                    char c = new Character(outAsChar[0]);
 //                   System.out.print("'" + c);
                    out.write(dataFromCandle[0]);
                    out.flush();
                }
*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

