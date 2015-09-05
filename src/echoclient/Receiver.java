/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoclient;

import java.io.IOException;
import java.util.Observer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

/**
 *
 * @author av
 */
public class Receiver extends java.util.Observable{

    private Scanner input;

    public Receiver(Scanner input, Observer observer) {
        this.input = input;
        addObserver(observer);
    }

    public void run() {
        new Thread() {
            public void run() {
                while(true) {
                    System.out.println("Reciever running...");
                 String message = input.nextLine();
                System.out.println("Line received");
                    if(message.equals(ProtocolStrings.STOP)){
                        System.out.println("Stopped the receiver");
                        return;
                    };
                    setChanged();
                    notifyObservers(message);
                    System.out.println("Notified Observer!");
                }
            }

        }.start();
    }
}
