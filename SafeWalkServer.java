import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class SafeWalkServer implements Runnable {
	public final String[] LOCS = {"CL50", "EE", "LWSN", 
                                  "PMU", "PUSH", "*"};
    private int port;
    private ServerSocket socket;
    public static final int DEFAULT_PORT = 4242;
    private ArrayList<Socket> clients;

    public SafeWalkServer(int port) throws SocketException, IOException {
    	if (isValidPort(port)) {
    		socket = new ServerSocket(port);
    		clients = new ArrayList<Socket>();
    	} else {
    		System.out.printf("Invalid port.");
    	}
    }

    public SafeWalkServer() throws SocketException, IOException{
    	socket = new ServerSocket(DEFAULT_PORT); 
    	clients = new ArrayList<Socket>();
    }

    public int getLocalPort() {
    	return port;
    }

    public synchronized void pairClients(Socket s) {
    	for (int i = 0; i < clients.size(); i++) {
    		if (sameStartingLocation(clients.get(i), s)) {
    			//check destination.
    		}
    	}
    }

    private boolean sameStartingLocation(Socket client1, Socket client2) {
    	return false;
    }

    public void run() {
    	while (true) {
    		try {
    			Socket client = socket.accept();
    			clients.add(client);
    			pairClients(client);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }

    private boolean isValidInput(String input) {
    	String temp = input;
    	int commaIndex = 0;
    	int previousIndex = 0;
    	int partsIndex = 0;
    	String[] parts = new String[4];
    	boolean validFrom = false;
    	boolean validTo = false;

    	for (int i = 0; i < (temp.length() - 1); i++) {
    		commaIndex = i;
    		if (temp.charAt(i) == ',') {
    			parts[partsIndex] = temp.substring(previousIndex, commaIndex);
    			previousIndex = ++commaIndex;
    			partsIndex++;
    		}
    	}

    	for (int i = 0; i < (LOCS.length - 1); i++) {
    		if (parts[1] == LOCS[i]) {
    			validFrom = true;
    		}
    	}

    	for (int i = 0; i < LOCS.length; i++) {
    		if (parts[2] == LOCS[i] && parts[2] != parts[1]) {
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
    			int port = Intger.parseInt(args[1]);
    			SafeWalkServer s = new SafeWalkServer(port);
    			s.run();
    		} catch (NumberFormatExcpetion e) {
    			e.printStackTrace();
    		}
    	}
    }		
}
