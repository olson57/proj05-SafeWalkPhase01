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
    private volatile ArrayList<String[]> clientInformation;
    
    public SafeWalkServer(int port) throws SocketException, IOException {
        if (isValidPort(port)) {
            socket = new ServerSocket(port);
            clients = new ArrayList<Socket>();
            clientInformation = new ArrayList<String[]>();
        } else {
            System.out.printf("Invalid port.");
        }
    }

    public SafeWalkServer() throws SocketException, IOException {
        socket = new ServerSocket(DEFAULT_PORT); 
        clients = new ArrayList<Socket>();
    }

    public int getLocalPort() {
        return port;
    }

    public synchronized void pairClients(Socket s) {
        
    }

    private boolean sameStartingLocation(Socket client1, Socket client2) {
        return false;
    }

    public void run() {
        while (true) {
            try {
                Socket client = socket.accept();

                OutputStream os = client.getOutputStream();
                 BufferedReader br = 
                 new BufferedReader(new InputStreamReader(client.getInputStream()));
                
                PrintWriter pw = new PrintWriter(client.getOutputStream());

                String s = "";
                String input = "";
                while ((s = br.readLine()) != null) {
                    input = br.readLine();
                }

                if (inputIsCommand(input)) {

                } else {
                    clients.add(client);
                    pairClients(client); 
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
    			int port = Integer.parseInt(args[1]);
    			SafeWalkServer s = new SafeWalkServer(port);
    			s.run();
    		} catch (NumberFormatException e) {
    			e.printStackTrace();
    		}
    	}
    }		
}
