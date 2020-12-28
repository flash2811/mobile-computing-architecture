//Subhra Mondal
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private static ServerSocket server;
    private static final int PORT = 7890;

    private ArrayList<String> positionHistory = new ArrayList<>();
    private Vector<HandleClient> clients = new Vector<>();

    public Server() {
        try {
            server = new ServerSocket(PORT, 10);
            System.out.println("Server Socket Connected!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.exit(1);
        }
    }

    public void process() throws IOException {
        while (true) {
            Socket client = server.accept();
            System.out.println("Listener Accepted!");
            HandleClient c = new HandleClient(client);
            clients.add(c);
            int x = 0;
            while (x <= positionHistory.size() - 1){
                //broadcast(positionHistory.get(x));
                c.sendMessage(positionHistory.get(x));
                x++;
            }
        }
    }

    private void broadcast(String position) {
        System.out.println("Client Size: " + clients.size());
        Vector<HandleClient> deadClients = new Vector<>();
        for (HandleClient c : clients) {
            if (c.isAlive()) {
                //System.out.println("Client Alive " + c.getSessionID());
//                if (c.getSessionID().equals(sessionID)) {
                    //System.out.println("Broadcast: " + c.getSessionID() + ", SessionID: " + sessionID + ", Position: " + position);
                    c.sendMessage(position);
//                }
            } else {
                //System.out.println("Added To Dead Client List " + c.getSessionID());
                deadClients.add(c);
            }
        }
        System.out.println("Removing " + deadClients.size() + " dead clients");
        // remove dead clients
        for (HandleClient c : deadClients) {
            clients.remove(c);
        }
        //deadClients.clear();
        //System.out.println("Broadcast complete");
    }

    class HandleClient extends Thread {
        private BufferedReader input;
        private PrintWriter output;
        private String sessionID = "";


        public HandleClient(Socket client) throws IOException {
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(), true);

            sessionID = input.readLine();
            start();

        }

        public String getSessionID() {
            return sessionID;
        }

        public void sendMessage(String position) {
            //System.out.println("Sending Message: " + position);
            output.println(position);
        }


        public void run() {
            String line;
            try {
                while (true) {
                    try {
                        line = input.readLine();
                        if (line.equals("end")) {
                            clients.remove(this);
                        }
                        positionHistory.add(line);
                        broadcast(line);
                    } catch (NullPointerException nullPointerException) {

                        input.close();
                        output.close();
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

//    public static void main(String[] argv) {
//        Server server = new Server();
//        try {
//            server.process();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
}