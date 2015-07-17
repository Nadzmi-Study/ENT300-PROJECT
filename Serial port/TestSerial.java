import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import com.fazecast.jSerialComm.*;

public class TestSerial {
	public static void main(String [] args) {
		Scanner in = new Scanner(System.in);
		
		SerialPort port = SerialPort.getCommPorts()[0];
		/*
		 * SerialPort portName = SerialPort.getCommPort("specifyPortName");
		 * this will connect to the specified port
		 */
		port.openPort();
		System.out.println("Successfully opened the port"); // print successful message if the port are opened
		
		while(port.isOpen()) { // check wheteher the port are opened or not
			// make an io stream between the arduino and java
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(port.getOutputStream()));
			BufferedReader input = new BufferedReader(new InputStreamReader(port.getInputStream()));
			
			System.out.println("Input command: ");
			String command = in.next();
			try {
				output.write(command);
				output.flush();
				output.close();
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
}
