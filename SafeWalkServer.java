import java.io.*;
import java.net.*;

public class SafeWalkServer {
	public final String[] LOCS = {"CL50", "EE", "LWSN", 
                                  "PMU", "PUSH", "*"};
    private int port;
    public static final int DEFAULT_PORT = 4242;

    public SafeWalkServer(int port) throws SocketException, IOException {

    }

    public SafeWalkServer() throws SocketException, IOException{

    }

    public int getLocalPort() {
    	return port;
    }

    public void run() {
    	while (true) {

    	}
    }

    public boolean isValidInput(String input) {

    	return false;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
    	if (args.length == 0) {
    		System.out.printf("Port not specified. Using free port %d", DEFAULT_PORT);
    	}
    }		
}
