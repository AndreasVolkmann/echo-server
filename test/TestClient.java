import echoclient.EchoClient;
import echoserver.EchoServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Lars Mortensen
 */
public class TestClient {
  
  public TestClient() {
  }
  
  @BeforeClass
  public static void setUpClass() {
    new Thread(new Runnable(){
      @Override
      public void run() {
        EchoServer.main(null);
      }
    }).start();
  }
  
  @AfterClass
  public static void tearDownClass() {
    EchoServer.stopServer();
  }
  
  @Before
  public void setUp() {
      
  }
  
  @Test
  public void send() throws IOException{
    final List<String> list = new ArrayList<>();
    EchoClient client = new EchoClient(9090, "localhost");
    Observer o = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            System.out.println("Update received!");
            list.add((String) arg);
        
        };
    };
    client.connect(o);
    client.send("Hello");
    assertEquals("HELLO", list.get(0));
  }
  
}
