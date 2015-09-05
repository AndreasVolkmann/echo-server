/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

/**
 *
 * @author av
 */
public class ClientHandler extends Thread {

    private Scanner input;
    private PrintWriter writer;
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            input = new Scanner(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        start();
    }

    public void run() {
        while (true) {
            try {
                handleClient();
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchElementException e) {
                close();
            } finally {
                close();
            }
        }
    }

    private void handleClient() throws IOException {
        String message = input.nextLine(); //IMPORTANT blocking call
        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));
        while (!message.equals(ProtocolStrings.STOP)) {
            EchoServer.addMessage(message);
            Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message.toUpperCase()));
            message = input.nextLine(); //IMPORTANT blocking call
        }
        writer.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
        socket.close();
        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Closed a Connection");
    }

    public void send(String message) {
        writer.println(message.toUpperCase());
    }

    private void close() {
        EchoServer.removeHandler(this);
    }

    protected void finalize() {
        input.close();
        writer.close();
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
