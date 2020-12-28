//Subhra Mondal
import java.util.Random;

public class ClientTest {

    public static void main(String[] args) {
        BlocksClientBridge bCB = new BlocksClientBridge();
        Random rand = new Random();
        int x = rand.nextInt(50);
        //System.out.println("From ClientTest X Value: " + x);
        String sessionId = String.valueOf(rand.nextInt(50));
        System.out.println("Test SessionID " + sessionId);
        bCB.connect("127.0.0.1", 7890, sessionId);

        bCB.placeBlock((int) x,2,3);
        //bCB.moveBlock(45, rand.nextInt(50), 56,12, rand.nextInt(50), rand.nextInt(50));
        //bCB.removeBlock(rand.nextInt(50), rand.nextInt(50), rand.nextInt(50));
        //bCB.disconnect();
    }
}
