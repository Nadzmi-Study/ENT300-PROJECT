import java.io.*;
import java.net.*;

public class TestClient {
	public static void main(String [] args) {
		try {
			System.out.print("Connecting to server... ");
			Socket server = new Socket("127.168.1.7", 9090);
			System.out.println("Connected");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
			System.out.println(reader.readLine());
			
			reader.close();
			server.close();
		} catch(Exception e) { e.printStackTrace(); }
	}
}
