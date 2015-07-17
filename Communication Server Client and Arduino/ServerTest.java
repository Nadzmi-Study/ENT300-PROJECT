import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {
	public static void main(String [] arsg) {
		new Thread(new Runnable() {
			public void run() {
				ServerSocket server = null;
				Socket client = null;
				
				try {
					System.out.print("Initializing server...");
					server = new ServerSocket(9090);
					System.out.println(" Initialized");
					
					int x = 0;
					while(true) {
						System.out.print("Waiting for connection... ");
						client = server.accept();
						System.out.println("Connected");
						
						System.out.println("Initializing output stream...");
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
						writer.write("Connected, u r no. "+x);
						writer.flush();
						writer.close();
						
						x++;
					}
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					try {
						client.close();
						server.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
