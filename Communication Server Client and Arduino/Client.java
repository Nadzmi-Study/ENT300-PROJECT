import java.io.*;
import java.net.*;

public class Client {
	public static void main(String [] arsg) {
		Socket connection = null;
		BufferedWriter writer = null;
		
		try {
			System.out.println("Connecting...");
			connection = new Socket("localhost", 9090);
			
			System.out.println("Initializing io streams...");
			writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			
			System.out.println("Sending...");
			writer.write("lock");
			writer.flush();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
				connection.close();
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
}
