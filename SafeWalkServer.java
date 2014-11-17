import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class SafeWalkServer implements Runnable {
    public final String[] LOCS = {"CL50", "EE", "LWSN", 
                                  "PMU", "PUSH", "*"};
    private int port;
    private ServerSocket socket;
    private ArrayList<Socket> clients;
    private ArrayList<String> clientInformation;
    
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
        socket = new ServerSocket(0); 
        clients = new ArrayList<Socket>();
	clientInformation = new ArrayList<String>();
    }
    
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    private boolean validPair(String client1Loc, String client2Loc) {
        String[] client1Info = client1Loc.split(",");
        String[] client2Info = client2Loc.split(",");

        if (client1Info[1].equals(client2Info[1])) {
            if ((client1Info[2].equals("*") || client2Info[2].equals("*")) && 
		!(client1Info[2].equals("*") && client1Info[2].equals("*"))) {
                return true;
            }

	    if (client1Info[2].equals(client2Info[2])) {
		return true;
	    }
        }
        
        return false;
    }

    public void run() {
        while (true) {
	    try {
                Socket client = socket.accept();
                Object o = new Object();
                synchronized (o) {
                    parseInput(client);
                }
	    } catch (IOException e) {
		return;
	    }
        }
    }

    private String formatPendingRequests() {
	String result = "[";
	for (int i = 0; i < clientInformation.size(); i++) {
	    result += "[";
	    String[] temp = clientInformation.get(i).split(",");
	    if (i != (clientInformation.size() - 1)) {
		for (int j = 0; j < temp.length; j++) {
		    result += i != (temp.length - 1) ? temp[i].concat(", ") : temp[i].concat("], ");
		}
	    } else {
		for (int j = 0; j < temp.length; j++) {
		    result += i != (temp.length - 1) ? temp[i].concat(", ") : temp[i].concat("]]");
		}
	    }
	}

	return result;
    }

    public void parseInput(Socket client) {
    try {
            OutputStream os = client.getOutputStream();
            BufferedReader br = 
            new BufferedReader(new InputStreamReader(client.getInputStream()));
                
            PrintWriter pw = new PrintWriter(os, true);

            String s = "";
            if ((s = br.readLine()) != null) {
                if (inputIsCommand(s)) {
                    if (s.equals(":LIST_PENDING_REQUESTS")) {
                        pw.println(formatPendingRequests());
			client.close();
                    } else if (s.equals(":RESET")) {
                        for (int i = 0; i < clients.size(); i++) {
			    OutputStream osOtherUsers = clients.get(i).getOutputStream();
			    PrintWriter pwOtherUsers = new PrintWriter(osOtherUsers, true);
			    pwOtherUsers.println("ERROR: connection reset");
			    clientInformation.remove(clientInformation.get(i));
			    clients.get(i).close();
			    clients.remove(clients.get(i));

			    osOtherUsers.close();
			    pwOtherUsers.close();
                        }

			pw.println("RESPONSE: success");
			client.close();
                    } else if (s.equals(":SHUTDOWN")) {
                        for (int i = 0; i < clients.size(); i++) {     
			    OutputStream osOtherUsers = clients.get(i).getOutputStream();
                            PrintWriter pwOtherUsers = new PrintWriter(osOtherUsers, true);
                            pwOtherUsers.println("ERROR: connection reset");
			    clientInformation.remove(clientInformation.get(i));
			    clients.get(i).close();
			    clients.remove(clients.get(i));

			    osOtherUsers.close();
			    pwOtherUsers.close();
                        }
			pw.println("RESPONSE: success");
                        br.close();
                        pw.flush();
                        pw.close();
                        socket.close();
			throw new IOException();
                    }
                    pw.flush();
                } else if (isValidInput(s)) {
                    clientInformation.add(s);
                    clients.add(client);
                    
                    if (clientInformation.size() > 1) {
                        for (int i = 0; i < clientInformation.size(); i++) {
                            if (validPair(clientInformation.get(i), s) && 
				(!clientInformation.get(i).equals(s))) {
				PrintWriter pw1 = new PrintWriter(clients.get(i).getOutputStream(), true);
       
				pw.println("RESPONSE: " + clientInformation.get(i));
				pw1.println("RESPONSE: " + s);
				clients.get(i).close();
				clientInformation.remove(clientInformation.get(i));
				clients.remove(clients.get(i));
				
				client.close();
				clientInformation.remove(s);
				clients.remove(client);
                            }
                        }
                    }
                } else {
                    pw.println("ERROR: invalid request");
                    client.close();
                }
            }
        } catch (IOException e) {}     
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
            if (information[1].equals(LOCS[i])) {
                validFrom = true;
            }
        }
        
        for (int i = 0; i < LOCS.length; i++) {
            if (information[2].equals(LOCS[i]) && !information[2].equals(information[1])) {
                validTo = true;
            }
        }
        
        return (validFrom && validTo);
    }
    
    private boolean isValidPort(int port) {
        if (port >= 1025 && port <= 65535) {
            return true;
        }
        return false;
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length == 0) {
            SafeWalkServer s = new SafeWalkServer();
            System.out.printf("Port not specified. Using free port %d.", s.getLocalPort());
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
