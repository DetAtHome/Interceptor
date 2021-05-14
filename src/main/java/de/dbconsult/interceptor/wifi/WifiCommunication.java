package de.dbconsult.interceptor.wifi;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WifiCommunication implements Runnable {
    ServerSocket listener = null;
    Socket socket = null;
    BufferedReader in = null;
    BufferedWriter out = null;

    public String writeGrbl(String grbl) {
//        Socket clientSocket = null;
        try {
//            clientSocket = new Socket("192.168.178.178", 6666);
//            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
//            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.write(grbl + "\r\n");
            out.flush();
//            int data;
//            String result="";
//            while((data=in.read())>-1) {
//                result = result + (char)data;
//            }
//            System.out.println("Result>" + result);
//            String inputLine;
//            if ((inputLine = in.readLine()) != null) {
//                return (inputLine);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try  { clientSocket.close();} catch (IOException ioe) {}
        }

        return "";



    }

    public void receive(String data) {
        System.out.println("Received> " + data);
    }

    public static void main(String[] args) throws Exception {

        WifiCommunication communication = new WifiCommunication();
        communication.listener = new ServerSocket(9023);
        communication.socket = communication.listener.accept();
        communication.socket.setKeepAlive(false);
        communication.in = new BufferedReader(new InputStreamReader(communication.socket.getInputStream()));
        communication.out = new BufferedWriter(new OutputStreamWriter(communication.socket.getOutputStream()));

        Thread listen = new Thread(communication);
        listen.start();
        String data;
        String response = null;
        System.out.println("gimme something to send:");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while ((data = in.readLine())!="shutdown") {
            response = communication.writeGrbl(data);
            System.out.println(response);
        }?


    }

    @Override
    public void run() {
        try {
            while (true) {
                String inputLine="void";
                if ((inputLine = in.readLine()) != null) {
                    System.out.println("Incoming request from ESP>" + inputLine);
                   // out.write("OK\n");
                   // out.flush();
                }
              //  socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
