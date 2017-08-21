import java.io.*;
import java.net.*;

public class TestClient {
	public static void main(String [] args) {
		try {
			System.out.print("Connecting to server... ");
			Socket server = new Socket("127.0.0.1", 9090);
			System.out.println("Connected");
			
			if(server.isConnected()) {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
				writer.write("lock");
				writer.flush();
				writer.close();
			} else
				System.out.println("Server cannot be connected");
			
			server.close();
		} catch(Exception e) { e.printStackTrace(); }
	}
}
