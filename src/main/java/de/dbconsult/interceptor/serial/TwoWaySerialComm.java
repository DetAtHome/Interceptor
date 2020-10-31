package de.dbconsult.interceptor.serial;


import java.io.*;

import de.dbconsult.interceptor.Orchestrator;
import de.dbconsult.interceptor.SerialDescriptor;
import gnu.io.*;

public class TwoWaySerialComm {

    private OutputStream out;
    private InputStream in;
    static long writingInProgress = System.currentTimeMillis();
    private String lastOutString = "";

    static SerialPort port = null;

    public void connect(SerialDescriptor me) throws Exception {
        String portName = me.getPortName();
        System.out.println("Opening port: " + portName);
        CommPortIdentifier portIdentifier = CommPortIdentifier
                .getPortIdentifier(portName);

        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Aborting Init: Port is currently in use");
        } else {
            int timeout = 2000;
            CommPort commPort = portIdentifier.open(TwoWaySerialComm.class.getName(), timeout);

            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(115200,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();

//                me.readerThread = ( new Thread( new SerialReader( in, me ) ) );


                port = serialPort;
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }

    }

    public class SerialReader implements Runnable {

        InputStream in;

        SerialDescriptor me;


        public SerialReader( InputStream in, SerialDescriptor from ) {
            this.in = in;
            me = from;

        }

        public void run() {
            byte[] buffer = new byte[ 1024 ];
            int len = -1;

            try {
               // StringBuffer responseBuffer = new StringBuffer();
//                while( ( len = this.in.read( buffer,0,1024 ) ) > -1 ) {
//                    if(len>0) {
//                        Orchestrator.getInstance().enqueueToWorkflow(me, new String(buffer, 0, len), buffer, len);
//                    }
//                }
                while( ( len = this.in.read( buffer,0,1024 ) ) > -1 ) {
                    Orchestrator.getInstance().enqueueToWorkflow(1,me, buffer, len);
                }

            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }



    public synchronized void  writeStringToSerial(String outString) {
        try {
            if (writingInProgress + 1000 > System.currentTimeMillis()) {
                System.out.println("writing in progress, ignored");
            } else {
                if (outString.equals(lastOutString)) {
                    System.out.println("ignored " + outString);
                } else {
                    System.out.println("writing: " + outString);
                    writingInProgress = System.currentTimeMillis();
                    out.write((outString + "\n").getBytes());
                    out.flush();
                    System.out.println("Wrote " + outString);
                }
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }

    }

    public synchronized void  writeToSerial(byte[] outData, int len) {
        try {
            out.write(outData);
            out.flush();
        } catch( Exception e ) {
            e.printStackTrace();
        }

    }
}
