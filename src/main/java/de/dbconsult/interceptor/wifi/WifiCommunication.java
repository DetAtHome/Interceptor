package de.dbconsult.interceptor.wifi;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WifiCommunication implements Runnable {
    private ServerSocket listener = null;
    private Socket socket = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    private String lastSocketDataReceived="";
    private static WifiCommunication instance = null;


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
            Thread listen = new Thread(this);
            listen.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public String write(String grbl) {
        try {

            out.write(grbl + "\r\n");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return "";



    }

    public String readFully() {
        return lastSocketDataReceived;
    }

    public static void main(String[] args) throws Exception {

        WifiCommunication communication = new WifiCommunication();

        String data;
        String response = null;
        System.out.println("gimme something to send:");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while ((data = in.readLine())!="shutdown") {
            if(!communication.isConnected()) {
                communication.setupSocket();
            }
            response = communication.write(data);
            System.out.println(response);
        }
    }

    private void setupSocket() {
        try {
            socket = listener.accept();
            socket.setKeepAlive(false);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String inputLine="void";
                if(!isConnected()) {
                    setupSocket();
                }

                if ((inputLine = in.readLine()) != null) {
                    System.out.println("Incoming request from ESP>" + inputLine);
                   // out.write("OK\n");
                   // out.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
