import java.io.*;
import java.net.*;

public class SafeWalkServer implements Runnable {
	public final String[] LOCS = {"CL50", "EE", "LWSN", 
                                  "PMU", "PUSH", "*"};
    private int port;
    private ServerSocket socket;
    public static final int DEFAULT_PORT = 4242;

    public SafeWalkServer(int port) throws SocketException, IOException {
    	if (isValidPort(port)) {
    		socket = new ServerSocket(port);
    	} else {
    		System.out.printf("Invalid port.");
    	}
    }

    public SafeWalkServer() throws SocketException, IOException{
    	socket = new ServerSocket(DEFAULT_PORT);
    }

    public int getLocalPort() {
    	return port;
    }

    public void run() {
    	while (true) {
    		//Socket client = accept();
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
    	}
    }		
}
