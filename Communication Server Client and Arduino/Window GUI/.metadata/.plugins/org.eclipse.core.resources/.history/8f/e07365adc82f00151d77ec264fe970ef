/*
 * 7/21/2015.
 * @author Seladang Hijau
 * @version 0.5
 */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Application {
	private static ServerSocket server;
	private static Socket client;
	
    @Override
    public void init() throws Exception {
        System.out.println("Initializing server...");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // primary stage
        primaryStage.setTitle("Server");
        primaryStage.setResizable(false);
        
        // other stage
        Stage outputStage = new Stage();
        outputStage.setTitle("Output console");
        outputStage.setResizable(false);

        // create root node
        FlowPane rootNode = new FlowPane(4,1);
        rootNode.setOrientation(Orientation.VERTICAL);
        rootNode.setAlignment(Pos.CENTER);
        FlowPane outputNode = new FlowPane();
        outputNode.setAlignment(Pos.CENTER);

        // create a scene
        Scene primaryScene = new Scene(rootNode,300,100);
        Scene outputScene = new Scene(outputNode,400,500);

        // set scene on stage
        primaryStage.setScene(primaryScene);
        outputStage.setScene(outputScene);
        
        // create gui component
        TextArea output = new TextArea("OUTPUT");
        output.setEditable(false);
        output.setPrefHeight(outputNode.getHeight());
        output.setPrefWidth(outputNode.getWidth());
        output.setFont(new Font("Courier New", 12));
        TextField inputPort = new TextField("Port number");
        inputPort.setAlignment(Pos.CENTER);
        inputPort.setEditable(true);
        Button start = new Button("START");
        start.setAlignment(Pos.CENTER);
                
        // event-handler
        start.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent e) {
        		if(start.getText().equalsIgnoreCase("start")) {
        			int port = Integer.parseInt(inputPort.getText().toString());
        			start.setText("STOP");
        			outputStage.show();
        			startServer(port, output);
        		} else if(start.getText().equalsIgnoreCase("stop")) {
        			stopServer(output);
        			outputStage.close();
        			inputPort.setEditable(true);
        			start.setText("START");
        		}
        	}
        });
        
        // attach gui component into node
        rootNode.getChildren().addAll(inputPort, start);
        outputNode.getChildren().add(output);

        // show gui
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
    	System.out.println("Session stopped");
    	
    	System.exit(0);
    }
    
    public void stopServer(TextArea output) {
    	output.appendText("Stopping server...\n");
    	try {
    		if(client != null) {
    			client.close();
    		}
    		server.close();
		} catch(Exception e) { e.printStackTrace(); }
    	output.appendText("Server stopped\n");
    }

    public void startServer(int port, TextArea output) {
    	try {
    		output.setText("Starting server...\n");
			server = new ServerSocket(port);
			output.appendText("Server started\n");
			output.appendText("Waiting on port "+port+"...\n");
		} catch (Exception io) { io.printStackTrace(); }
		
		new Thread(new Runnable() {
			public void run() {
				try {
					client = server.accept();
					
					if(client.isConnected()) {
						output.appendText("Connected\n");
						
						BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
						
						// io operation here
						
						writer.close();
						reader.close();
					}
				} catch(Exception e) { e.printStackTrace(); }
			}
		}).start();
    }
    
    public static void main(String [] args) {
        launch();
    }
}
