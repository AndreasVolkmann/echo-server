package echoclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

public class EchoClient
{
  Socket socket;
  private int port;
  private InetAddress serverAddress;
  private Scanner input;
  private PrintWriter output;
  private Receiver receiver;
  
  public EchoClient(int port, String address) {
      this.port = port;
      try {
          this.serverAddress = InetAddress.getByName(address);
      } catch (UnknownHostException ex) {
          Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
      }
      
  }
  
  public void connect(Observer observer) throws IOException
  {
      System.out.println(serverAddress);
      System.out.println(port);
    socket = new Socket(serverAddress, port);
    input = new Scanner(socket.getInputStream());
    output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
    receiver = new Receiver(input, observer);
    receiver.run();
      System.out.println("Connected");
  }
  
  public void send(String msg)
  {
    output.println(msg);
  }
  
  public void stop() throws IOException{
    output.println(ProtocolStrings.STOP);
  }
  
  public String receive()
  {
    String msg = input.nextLine();
    if(msg.equals(ProtocolStrings.STOP)){
      try {
        socket.close();
      } catch (IOException ex) {
        Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return msg;
  }
  
  public static void main(String[] args)
  {   
    int port = 9090;
    String ip = "localhost";
    if(args.length == 2){
      port = Integer.parseInt(args[0]);
      ip = args[1];
    }
  }
  
  public Receiver getReceiver() {
      return receiver;
  }
  
  protected void finalize() {
      try {
          socket.close();
      } catch (IOException ex) {
          Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
}
