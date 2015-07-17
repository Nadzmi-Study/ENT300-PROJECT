package com.example.seladang.androidtoarduino;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fazecast.jSerialComm.SerialPort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;


public class MainActivity extends ActionBarActivity {
    SerialPort arduino;

    Button action;
    TextView output;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arduino = SerialPort.getCommPorts()[0];
        arduino.openPort();

        action = (Button) findViewById(R.id.action);
        output = (TextView) findViewById(R.id.output);
    }

    public void action(View view) {
        new Thread(new Runnable() {
            public void run() {
                String command = action.getText().toString();
                try {
                    if(arduino.isOpen()) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                output.setText("Arduino is connected");
                            }
                        });

                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(arduino.getOutputStream()));

                        if(command.equals("LOCK")) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    action.setText("UNLOCK");
                                }
                            });
                            writer.write("lock");
                            writer.close();
                        } else if(command.equals("UNLOCK")) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    action.setText("LOCK");
                                }
                            });
                            writer.write("unlock");
                            writer.close();
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                output.setText("Arduino is not connected");
                            }
                        });
                    }
                } catch(final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            output.setText(e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }
}
