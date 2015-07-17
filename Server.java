import java.net.*;
import java.io.*;

import com.fazecast.jSerialComm.SerialPort;

public class Server extends Thread {
	public Server() {
		ServerSocket server = null;
		Socket connection = null;
		
		SerialPort arduino = null;
		
		try {
			arduino = SerialPort.getCommPort("COM4"); // initialize arduino port on port COM4
			server = new ServerSocket(9090); // create a new server and open connection port on port 9090
			
			if(arduino.openPort()) { // open port for the arduino communication
				System.out.println("Arduino port opened.");
				System.out.println("Server initialised.");
				
				System.out.println("\nThe system are now ready.");
				
				while(arduino.isOpen()) { // while the arduino port is opened
					System.out.println("\n--------------------------->>>");
					System.out.println("Waiting for your command...");
					connection = server.accept(); // accept connection from client
					System.out.println("Accepted");
					
					if(connection.isConnected()) { // while client connection is still on
						System.out.println("Initializing io streams...");
						// initialize input stream from client
						BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						System.out.println("Reading command...");
						String command = reader.readLine(); // read client's command
						System.out.println("Your command is "+command);
												
						System.out.println("Interpreting your command...");
						// initalize output stream for arduino
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(arduino.getOutputStream()));
						if(command.equalsIgnoreCase("lock")) { // if the command entered is "lock"
							System.out.println("locking door...");
							// write the command to lock the door
							writer.write("lock"); 
							writer.flush();
							System.out.println("Door locked.");
						} else if(command.equalsIgnoreCase("unlock")) {
							System.out.println("unlocking door...");
							// write the command to unlock the door
							writer.write("unlock");
							writer.flush();
							System.out.println("Door unlocked.");
						}
						System.out.println("--------------------------->>>");
						
						// close io streams
						reader.close();
						writer.close();
					} else
						System.err.println("There are no server connection");
					
					connection.close(); // close client connection
				}
				
				// close all connection
				arduino.closePort();
				connection.close();
			} else
				System.err.println("Port are not connected");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String [] args) {
		new Thread(new Server());
	}
}
