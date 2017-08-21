package com.seladanghijau.test.server;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;


public class MainActivity extends Activity {
    TextView output = null;
    Button action = (Button) findViewById(R.id.action);

    private static final int targetVendorID = 9025;  // Arduino Uno : vendor id - 9025, product id - 67
    private static final int targetProductID = 67; // x semua arduino sama vendor id atau product id, kena check id masing2 pakai "usb check.apk"

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    PendingIntent mPermissionIntent;

    UsbDevice deviceFound = null;
    UsbInterface usbInterfaceFound = null;
    UsbEndpoint endpointIn = null;
    UsbEndpoint endpointOut = null;
    UsbInterface usbInterface;
    UsbDeviceConnection usbDeviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = (TextView) findViewById(R.id.output);

        // usb host detection
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        registerReceiver(mUsbDeviceReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED));
        registerReceiver(mUsbDeviceReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));

        connectUsb();

        action();
    }

    // onClick methods :
    // action
    public void action() {
        if(action.getText().toString().equalsIgnoreCase("LOCK")) {
            byte[] bytesOut = "lock".getBytes(); //convert String to byte[]
            usbDeviceConnection.bulkTransfer(endpointOut, bytesOut, bytesOut.length, 0);
        } else if(action.getText().toString().equalsIgnoreCase("UNLOCK")) {
            byte[] bytesOut = "unlock".getBytes(); //convert String to byte[]
            usbDeviceConnection.bulkTransfer(endpointOut, bytesOut, bytesOut.length, 0);
        }
    }

    // other methods :
    // connectUsb()
    private void connectUsb() {
        Toast.makeText(MainActivity.this, "Connecting USB...", Toast.LENGTH_LONG).show();

        searchEndPoint();
        if (usbInterfaceFound != null) {
            setupUsbComm();
        }

        Toast.makeText(MainActivity.this, "USB connected", Toast.LENGTH_LONG).show();
    }
    // onDestroy()
    @Override
    protected void onDestroy() {
        releaseUsb();
        unregisterReceiver(mUsbReceiver);
        unregisterReceiver(mUsbDeviceReceiver);
        super.onDestroy();
    }
    // searchEndPoint()
    private void searchEndPoint() {
        output.append("\n\n");

        usbInterfaceFound = null;
        endpointOut = null;
        endpointIn = null;

        // Search device for targetVendorID and targetProductID
        if (deviceFound == null) {
            UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();

                if (device.getVendorId() == targetVendorID) {
                    if (device.getProductId() == targetProductID) {
                        deviceFound = device;
                    }
                }
            }
        }

        if (deviceFound == null) {
            Toast.makeText(MainActivity.this, "Device not found", Toast.LENGTH_LONG).show();
            output.setText("Device not found");
        } else {
            String s = "\n" + "DeviceID: " + deviceFound.getDeviceId()
                    + "\n" + "DeviceName: " + deviceFound.getDeviceName()
                    + "\n" + "DeviceClass: " + deviceFound.getDeviceClass()
                    + "\n" + "DeviceSubClass: " + deviceFound.getDeviceSubclass()
                    + "\n" + "VendorID: " + deviceFound.getVendorId()
                    + "\n" + "ProductID: " + deviceFound.getProductId()
                    + "\n" + "InterfaceCount: " + deviceFound.getInterfaceCount();
            output.append(s+"\n");

            // Search for UsbInterface with Endpoint of USB_ENDPOINT_XFER_BULK,
            // and direction USB_DIR_OUT and USB_DIR_IN
            for (int i = 0; i < deviceFound.getInterfaceCount(); i++) {
                UsbInterface usbif = deviceFound.getInterface(i);

                UsbEndpoint tOut = null;
                UsbEndpoint tIn = null;

                int tEndpointCnt = usbif.getEndpointCount();
                if (tEndpointCnt >= 2) {
                    for (int j = 0; j < tEndpointCnt; j++) {
                        if (usbif.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                            if (usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_OUT) {
                                tOut = usbif.getEndpoint(j);
                            } else if (usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_IN) {
                                tIn = usbif.getEndpoint(j);
                            }
                        }
                    }

                    if (tOut != null && tIn != null) {
                        // This interface have both USB_DIR_OUT
                        // and USB_DIR_IN of USB_ENDPOINT_XFER_BULK
                        usbInterfaceFound = usbif;
                        endpointOut = tOut;
                        endpointIn = tIn;
                    }
                }

            }

            if (usbInterfaceFound == null) {
                output.setText("No suitable interface found!");
            } else {
                output.append("\nUsbInterface found: " + usbInterfaceFound.toString() + "\n\n" + "Endpoint OUT: " + endpointOut.toString() + "\n\n" + "Endpoint IN: " + endpointIn.toString() + "\n");
            }
        }
    }
    // setupUsbComm()
    private boolean setupUsbComm() {
        // for more info, search SET_LINE_CODING and
        // SET_CONTROL_LINE_STATE in the document:
        // "Universal Serial Bus Class Definitions for Communication Devices"
        // at http://adf.ly/dppFt
        final int RQSID_SET_LINE_CODING = 0x20;
        final int RQSID_SET_CONTROL_LINE_STATE = 0x22;

        boolean success = false;

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        Boolean permitToRead = manager.hasPermission(deviceFound);

        if (permitToRead) {
            usbDeviceConnection = manager.openDevice(deviceFound);
            if (usbDeviceConnection != null) {
                usbDeviceConnection.claimInterface(usbInterfaceFound, true);

                int usbResult;
                usbResult = usbDeviceConnection.controlTransfer(0x21, // requestType
                        RQSID_SET_CONTROL_LINE_STATE, // SET_CONTROL_LINE_STATE
                        0, // value
                        0, // index
                        null, // buffer
                        0, // length
                        0); // timeout

                Toast.makeText(MainActivity.this, "controlTransfer(SET_CONTROL_LINE_STATE):\n" + usbResult, Toast.LENGTH_LONG).show();

                // baud rate = 9600
                // 8 data bit
                // 1 stop bit
                byte[] encodingSetting = new byte[] { (byte) 0x80, 0x25, 0x00, 0x00, 0x00, 0x00, 0x08 };
                usbResult = usbDeviceConnection.controlTransfer(0x21, // requestType
                        RQSID_SET_LINE_CODING, // SET_LINE_CODING
                        0, // value
                        0, // index
                        encodingSetting, // buffer
                        7, // length
                        0); // timeout
                Toast.makeText(MainActivity.this, "controlTransfer(RQSID_SET_LINE_CODING):\n" + usbResult, Toast.LENGTH_LONG).show();
            }
        } else {
            manager.requestPermission(deviceFound, mPermissionIntent);
            Toast.makeText(MainActivity.this, "Permission to read: " + permitToRead, Toast.LENGTH_LONG).show();
            output.append("Permission to read: " + permitToRead + "\n");
        }

        return success;
    }
    // realeaseUsb()
    private void releaseUsb() {
        Toast.makeText(MainActivity.this, "Realeasing USB...", Toast.LENGTH_LONG).show();

        if (usbDeviceConnection != null) {
            if (usbInterface != null) {
                usbDeviceConnection.releaseInterface(usbInterface);
                usbInterface = null;
            }
            usbDeviceConnection.close();
            usbDeviceConnection = null;
        }

        deviceFound = null;
        usbInterfaceFound = null;
        endpointIn = null;
        endpointOut = null;

        Toast.makeText(MainActivity.this, "USB released", Toast.LENGTH_LONG).show();
    }

    // other variables :
    // mUsbReceiver
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                Toast.makeText(MainActivity.this, "Getting USB debug permission...", Toast.LENGTH_LONG).show();
                output.append("Getting USB debug permission...\n");

                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            connectUsb();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Permission denied for device " + device, Toast.LENGTH_LONG).show();
                        output.setText("Permission denied for device " + device);
                    }
                }
            }
        }
    };
    // mUsbDeviceReceiver
    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                deviceFound = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Toast.makeText(MainActivity.this, "USB attached:\n" + deviceFound.toString(), Toast.LENGTH_LONG).show();
                output.append("USB attached:\n" + deviceFound.toString() + "\n");

                connectUsb();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                Toast.makeText(MainActivity.this, "USB detached:\n" + device.toString(), Toast.LENGTH_LONG).show();
                output.append("USB detached:\n" + device.toString() + "\n");

                if (device != null) {
                    if (device == deviceFound) {
                        releaseUsb();
                    } else {
                        Toast.makeText(MainActivity.this, "device == deviceFound, no call releaseUsb()\n" + device.toString() + "\n" + deviceFound.toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "device == null, no call releaseUsb()", Toast.LENGTH_LONG).show();
                }

                output.append("\n");
            }
        }
    };
}
