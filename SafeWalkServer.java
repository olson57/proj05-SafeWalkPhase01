import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class SafeWalkServer implements Runnable {
    public final String[] LOCS = {"CL50", "EE", "LWSN", 
                                  "PMU", "PUSH", "*"};
    private int port;
    private ServerSocket socket;
    public static final int DEFAULT_PORT = 4242;
    private volatile ArrayList<Socket> clients;
    private volatile ArrayList<String> clientInformation;
    
    public SafeWalkServer(int port) throws SocketException, IOException {
        if (isValidPort(port)) {
            socket = new ServerSocket(port);
            clients = new ArrayList<Socket>();
            clientInformation = new ArrayList<String>();
        } else {
            System.out.printf("Invalid port.");
        }
    }
    
    public SafeWalkServer() throws SocketException, IOException {
        socket = new ServerSocket(DEFAULT_PORT); 
        clients = new ArrayList<Socket>();
	clientInformation = new ArrayList<String>();
    }
    
    public int getLocalPort() {
        return port;
    }
    
    public synchronized void pairClients() {
        
    }

    private boolean validPair(String client1Loc, String client2Loc) {
        String[] client1Info = client1Loc.split(",");
        String[] client2Info = client2Loc.split(",");

        if (client1Info[1].equals(client2Info[1])) {
            if (client1Info[2].equals("*") || client2Info[2].equals("*")) {
                return true;
            }
        }
        
        return false;
    }

    public void run() {
        while (true) {
            try {
                Socket client = socket.accept();

                OutputStream os = client.getOutputStream();
                BufferedReader br = 
                 new BufferedReader(new InputStreamReader(client.getInputStream()));
                
                PrintWriter pw = new PrintWriter(os, true);
		pw.println("Will it print on connection?");

                String s = "";
                while ((s = br.readLine()) != null) {
                    if (inputIsCommand(s)) {
                        if (s.equals(":LIST_PENDING_REQUESTS")) {
                            pw.println(clientInformation.toString());
                        } else if (s.equals(":RESET")) {
                            for (int i = 0; i < clients.size(); i++) {
                                clients.get(i).close();
                            }
                        } else if (s.equals(":SHUTDOWN")) {
                            for (int i = 0; i < clients.size(); i++) {
                                clients.get(i).close();
                            }
                        
                            br.close();
                            pw.flush();
                            pw.close();
                            socket.close();
                            return;
                        }
                        pw.flush();
                    } else {
                        clientInformation.add(s);
                        clients.add(client);
                        pairClients(); 
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }      
        }
    }
    
    private boolean inputIsCommand(String input) {
        if (input.contains(":")) {
            if (input.equals(":LIST_PENDING_REQUESTS")) {
                return true;
            } else if (input.equals(":RESET")) {
                return true;
            } else if (input.equals(":SHUTDOWN")) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isValidInput(String input) {
        String temp = input;
        boolean validFrom = false;
        boolean validTo = false;
        
        String information[] = input.split(",");
        
        if (information.length != 4) {
            return false;
        }
        
        for (int i = 0; i < (LOCS.length - 1); i++) {
            if (information[1] == LOCS[i]) {
                validFrom = true;
            }
        }
        
        for (int i = 0; i < LOCS.length; i++) {
            if (information[2] == LOCS[i] && information[2] != information[1]) {
                validTo = true;
            }
        }
        
        return (validFrom && validTo);
    }
    
    private boolean isValidPort(int port) {
        if (port > 1025 && port < 65535) {
            return true;
        }
        return false;
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length == 0) {
            System.out.printf("Port not specified. Using free port %d", DEFAULT_PORT);
            SafeWalkServer s = new SafeWalkServer();
            s.run();
        } else {
            try {
                int port = Integer.parseInt(args[0]);
                SafeWalkServer s = new SafeWalkServer(port);
                s.run();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }  
}
