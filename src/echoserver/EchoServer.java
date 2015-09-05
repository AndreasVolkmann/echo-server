package echoserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;
import utils.Utils;

public class EchoServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private static final Properties properties = Utils.initProperties("server.properties");
    private static ArrayList<ClientHandler> clients;
    private static LinkedBlockingDeque<String> messages;

    public EchoServer() {
        clients = new ArrayList<>();
        messages = new LinkedBlockingDeque<>();
    }

    public static void stopServer() {
        keepRunning = false;
    }

    private void runServer() {
        int port = Integer.parseInt(properties.getProperty("port"));
        String ip = properties.getProperty("serverIp");

        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Sever started. Listening on: " + port + ", bound to: " + ip);
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            Thread messageHandling = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            String message = messages.take();
                            sendToAll(message);
                            System.out.println("Message received: " + message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            messageHandling.start();
            do {
                Socket socket = serverSocket.accept(); //Important Blocking call
                Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Connected to a client");
                clients.add(new ClientHandler(socket));
            } while (keepRunning);
        } catch (IOException ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void removeHandler(ClientHandler handler) {
        clients.remove(handler);
        System.out.println("Removed client");
    }

    public static void addMessage(String message) {
        messages.add(message);
    }
    
    public void sendToAll(String message) {
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }

    public static void main(String[] args) {
        try {
            String logFile = properties.getProperty("logFile");
            Utils.setLogFile(logFile, EchoServer.class.getName());
            new EchoServer().runServer();
        } finally {
            Utils.closeLogger(EchoServer.class.getName());
        }
    }
}
