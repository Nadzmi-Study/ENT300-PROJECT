package com.example.clientsocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	Thread clientThread;
	Socket clientSocket;
	Button action, connect;
	TextView output;
	EditText inputIP, inputPort;

	String ip;
	int port;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		action = (Button) findViewById(R.id.action);
		connect = (Button) findViewById(R.id.connect);
		output = (TextView) findViewById(R.id.output);
		inputIP = (EditText) findViewById(R.id.ip);
		inputPort = (EditText) findViewById(R.id.port);
	}

	public void connect(View view) {
		if(connect.getText().toString().equals("CONNECT")) {
			ip = inputIP.getText().toString();
			port = Integer.parseInt(inputPort.getText().toString());

			inputIP.setEnabled(false);
			inputPort.setEnabled(false);
			connect.setText("DISCONNECT");
		} else if(connect.getText().toString().equals("DISCONNECT")) {
			inputIP.setEnabled(true);
			inputPort.setEnabled(true);
			connect.setText("CONNECT");
		}
	}

	public void Start(View view) {
		clientThread = new Thread(new Runnable() {
			public void run() {
				BufferedWriter writer = null;

				try {

					runOnUiThread(new Runnable() {
						public void run() {
							output.setText("\n\n--------------------->>>->>->");
							output.append("\nConnecting to " + ip + ":"+port+"...");
						}
					});
					clientSocket = new Socket(ip,port);
					runOnUiThread(new Runnable() {
						public void run() {
							output.append("\nConnected.");
						}
					});

					runOnUiThread(new Runnable() {
						public void run() {
							output.append("\nInitializing IO Streams...");
						}
					});
					writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
					runOnUiThread(new Runnable() {
						public void run() {
							output.append("\nIO Streams initialized.");
							output.append("\nExecuting command...");
						}
					});
					if(action.getText().toString().equals("LOCK")) {
						runOnUiThread(new Runnable() {
							public void run() {
								output.append("\nLocking door...");
							}
						});

						writer.write("lock");
						writer.flush();
						action.setText("UNLOCK");

						runOnUiThread(new Runnable() {
							public void run() {
								output.append("\nDoor locked.");
							}
						});
					} else if(action.getText().toString().equals("UNLOCK")) {
						runOnUiThread(new Runnable() {
							public void run() {
								output.append("\nUnlocking door...");
							}
						});


						writer.write("unlock");
						writer.flush();
						action.setText("LOCK");

						runOnUiThread(new Runnable() {
							public void run() {
								output.append("\nDoor unlocked.");
							}
						});

					}
					runOnUiThread(new Runnable() {
						public void run() {
							output.append("\nCommand executed.");
							output.append("\n--------------------->>>->>->");
						}
					});
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					try {
						writer.close();
					} catch(Exception e) { e.printStackTrace(); }
				}
			}
		});

		clientThread.start();
	}
}
