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
    }

    public int getLocalPort() {
        return port;
    }

    public synchronized void pairClients(Socket s) {
	int indexToNotCheck = 0;
	for (int i = 0; i < clients.size(); i++) {
	    if (s == clients.get(i)) {
			indexToNotCheck = i;
			break;
	    }
	}

	for (int i = 0; i < clientInformation.size(); i++) {
	    if (i != indexToNotCheck) {
		if (validPair(clientInformation.get(i), clientInformation.get(indexToNotCheck))){
		    try { 
			OutputStream os = clients.get(i).getOutputStream();
			OutputStream os2 = clients.get(indexToNotCheck).getOutputStream();

			PrintWriter client1 = new PrintWriter(os,true);
			PrintWriter client2 = new PrintWriter(os2,true);

			client1.println("RESPONSE: " + clientInformation.get(indexToNotCheck));
			client1.flush();
			
			client2.println("RESPONSE: " + clientInformation.get(i));
			client2.flush();

			System.out.println("Got to Close");
			client1.close();
			client2.close();
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
	    }
	}
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

                String s = "";
                String input = "";
                while ((s = br.readLine()) != null) {
                    input = s;
                }

                if (inputIsCommand(input)) {
                	if (input.equals(":LIST_PENDING_REQUESTS")) {
        				pw.println(clientInformation.toString());
        			} else if (input.equals(":RESET")) {
        				for (int i = 0; i < clients.size(); i++) {
        					clients.get(i).close();
        				}
        			} else if (input.equals(":SHUTDOWN")) {
        				for (int i = 0; i < clients.size(); i++) {
        					clients.get(i).close();
        				}

        				br.close();
        				pw.close();
        				socket.close();
        				return;
        			}
        			pw.flush();
                } else {
		    		clientInformation.add(input);
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
    			int port = Integer.parseInt(args[0]);
    			SafeWalkServer s = new SafeWalkServer(port);
    			s.run();
    		} catch (NumberFormatException e) {
    			e.printStackTrace();
    		}
    	}
    }		
}
