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
    		ServerSocket s = new ServerSocket(port);
    	} else {

    	}
    }

    public SafeWalkServer() throws SocketException, IOException{
    	ServerSocket s = new ServerSocket(DEFAULT_PORT);
    }

    public int getLocalPort() {
    	return port;
    }

    public void run() {
    	while (true) {

    	}
    }

    private boolean isValidInput(String input) {

    	return false;
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
