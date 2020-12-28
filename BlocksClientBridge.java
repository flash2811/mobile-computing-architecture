//import com.unity3d.player.UnityPlayerActivity;
//import com.unity3d.player.UnityPlayer;

//import android.util.Log;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Vector;

/*
 * This class is the interface between Java code for client networking
 * and C#/Unity code for the client UI.
 */
class BlocksClientBridge {
    public static final String PLACE = "P";
    public static final String MOVE = "M";
    public static final String REMOVE = "R";

    private static final String TAG = BlocksClientBridge.class.getName();
    private static final String UNITY_GAMEOBJECT = "Control";

    private Socket socket;
    final DataOutputStream ToServer = null;
    final DataInputStream FromServer = null;
    private BufferedReader input;
    private PrintWriter output;
    private Vector<String> theMessages;

    public BlocksClientBridge() {
    }

    public void SendMessageToUnity(String line) throws NullPointerException {
        //break down the string...
        String[] res = line.split("[,]", 0);
        int x, y, z, x2, y2, z2;
        String actionType = res[0];
        x = Integer.parseInt(res[1]);
        y = Integer.parseInt(res[2]);
        z = Integer.parseInt(res[3]);

        switch (actionType) {
            case PLACE:
                receivePlaceBlock(x, y, z);
                break;
            case MOVE:
                x2 = Integer.parseInt(res[4]);
                y2 = Integer.parseInt(res[5]);
                z2 = Integer.parseInt(res[6]);
                receiveMoveBlock(x, y, z, x2, y2, z2);
                break;
            case REMOVE:
                receiveRemoveBlock(x, y, z);
                break;
            default:
                throw new IllegalStateException("Invalid Action Type! " + actionType);
        }
    }

    /*
     * Unity -> Java
     *
     * Unity will call these functions to send events to the server.
     *
     * (Implement these functions)
     *
     */

    public void connect(String serverHost, int serverPort, String sessionId) {
        //Log.v(TAG, "connect");
        // TODO: establish a connection to the server
        // TODO: start listening for messages from the server (in a thread)
        try {
            socket = new Socket(serverHost, serverPort);
            System.out.println("Client Socket Connected!");
            //Log.v(TAG, "Client Socket Connected!");
            receiveConnectedToServer("Client Socket Connected!");

            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output.println(sessionId);

            //start the thread
            MessagesThread thread = new MessagesThread();
//            try {
                thread.start();
//            } catch (NullPointerException nullPointerException) {
//                this.disconnect();
//            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
            disconnect();

        }

    }

    public void disconnect() {
        //Log.v(TAG, "disconnect");
        // TODO: gracefully disconnect from the server
        try {
            System.out.println("Client Disconnected!");
            receiveDisconnectedFromServer("Client Disconnected!");
            output.println("end");
            socket.close();
            input.close();
            output.close();
            System.exit(0);
            //Log.v(TAG, "Client Disconnected!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // send to server
    public void placeBlock(int x, int y, int z) {
        //Log.v(TAG, "placeBlock");
        //receivePlaceBlock(x, y, z); // PLACEHOLDER: REMOVE THIS LINE
        // TODO: send event to the server
        try {
            output.println(PLACE + "," + x + "," + y + "," + z);
            //System.out.println("Sent Positions To Server!");

        } catch (NoSuchElementException ne) {   //if the server closes connection
            ne.printStackTrace();
        }

    }

    // send to server
    public void moveBlock(int x, int y, int z, int x2, int y2, int z2) {
        //Log.v(TAG, "moveBlock");
        //receiveMoveBlock(x, y, z, x2, y2, z2); // PLACEHOLDER: REMOVE THIS LINE
        // TODO: send event to the server
        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println(MOVE + "," + x + "," + y + "," + z + "," + x2 + "," + y2 + "," + z2);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // send to server
    public void removeBlock(int x, int y, int z) {
        //Log.v(TAG, "removeBlock");
        //receiveRemoveBlock(x, y, z); // PLACEHOLDER: REMOVE THIS LINE
        // TODO: send event to the server
        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println(REMOVE + "," + x + "," + y + "," + z);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /*
     * Java -> Unity
     *
     * Call these functions when this client receives events from the server.
     *
     * (Call these functions)
     *
     */

    public void receiveConnectedToServer(String message) {
        //Log.v(TAG, "receiveConnectedToServer");
        // forward to unity function
        //UnityPlayer.UnitySendMessage(UNITY_GAMEOBJECT, "ReceiveConnectedToServer", message);
    }

    public void receiveDisconnectedFromServer(String message) {
        //Log.v(TAG, "receiveDisconnectedFromServer");
        // forward to unity function

        //UnityPlayer.UnitySendMessage(UNITY_GAMEOBJECT, "ReceiveDisconnectedFromServer", message);
    }

    public void receivePlaceBlock(int x, int y, int z) {
        //Log.v(TAG, "receivePlaceBlock");
        // forward to unity function
        //Log.v(TAG, "receiveMoveBlock Called!");
        //Log.v(TAG, "receivePlaceBlock Called!");
        System.out.println("receivePlaceBlock Called!");
        //UnityPlayer.UnitySendMessage(UNITY_GAMEOBJECT, "ReceivePlaceBlock", x + " " + y + " " + z);
    }

    public void receiveMoveBlock(int x, int y, int z, int x2, int y2, int z2) {
        //Log.v(TAG, "receiveMoveBlock");
        // forward to unity function
        //Log.v(TAG, "receiveRemoveBlock Called!");
        System.out.println("receiveMoveBlock Called!");
        //UnityPlayer.UnitySendMessage(UNITY_GAMEOBJECT, "ReceiveMoveBlock", x + " " + y + " " + z + " " + x2 + " " + y2 + " " + z2);
    }

    public void receiveRemoveBlock(int x, int y, int z) {
        //Log.v(TAG, "receiveRemoveBlock");
        // forward to unity function
        System.out.println("receiveRemoveBlock Called!");
        //UnityPlayer.UnitySendMessage(UNITY_GAMEOBJECT, "ReceiveRemoveBlock", x + " " + y + " " + z);
    }

    class MessagesThread extends Thread {
        public void run() throws NullPointerException{
            String line;
            boolean isValid = true;
            while (isValid) {
                try {
                    line = input.readLine();
                    //Log.v(TAG, ""Client MessagesThread: " + line"){}
                    System.out.println("Client MessagesThread: " + line);
                    //theMessages.add(line + "\n");
                    SendMessageToUnity(line);
//                } catch (NullPointerException nullPointerException) {
//                    //ex.printStackTrace();
//
                } catch (IOException ioException) {
                    //Log.e(TAG, "Error receiving message", ex);
                    isValid = false;
                    ioException.printStackTrace();
                    throw new NullPointerException();
                }
            }
        }
    }

}