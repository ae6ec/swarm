package com.example.ver.myfirstapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class chatController {
    private static final String TAG = "chatController";
    private static final String APP_NAME = "BluetoothChatApp";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ReadWriteThread connectedThread;
    private int state;

    static final int STATE_NONE = 0;
    static final int STATE_LISTEN = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;


    public chatController(Context context, Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;

        this.handler = handler;
    }

    // Set the current state of the chat connection
    private synchronized void setState(int state) {
        this.state = state;
        Log.e(TAG,"Changing state from "+this.state+"to :"+state);

        handler.obtainMessage(bluConnectChat.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    // get current connection state
    public synchronized int getState() {
        return state;
    }

    // start service
    public synchronized void start() {
        // Cancel any thread
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any running thresd
        if (connectedThread != null) {  // n
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_LISTEN);
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    // initiate connection to remote device
    public synchronized void connect(BluetoothDevice device) {
        // Cancel any thread
        //Context context = c;
        if (state == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        // Cancel running thread
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        Log.e(TAG,"connection make tried");
        //Toast.makeText(c, "connection make tried!", Toast.LENGTH_SHORT).show();

        // Start the thread to connect with the given device
        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);
        Log.e(TAG,"Connecting___");

       // Toast.makeText(context, "Connecting___!", Toast.LENGTH_SHORT).show();

    }

    // manage Bluetooth connection
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        // Cancel the thread
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel running thread
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ReadWriteThread(socket);
        connectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = handler.obtainMessage(bluConnectChat.MESSAGE_DEVICE_OBJECT);
        Bundle bundle = new Bundle();
        bundle.putParcelable(bluConnectChat.DEVICE_OBJECT, device);
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    // stop all threads
    public synchronized void stop() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        setState(STATE_NONE);
    }

    public void write(byte[] out) {
        ReadWriteThread r;
        synchronized (this) {
            if (state != STATE_CONNECTED)
                return;
            r = connectedThread;
        }
        r.write(out);
    }

    private void connectionFailed() {
        Message msg = handler.obtainMessage(bluConnectChat.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Unable to connect device");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Start the service over to restart listening mode
        chatController.this.start();
    }

    private void connectionLost() {
        Message msg = handler.obtainMessage(bluConnectChat.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Device connection was lost");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Start the service over to restart listening mode
        chatController.this.start();
    }

    // runs while listening for incoming connections
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, DEFAULT_UUID);
                Log.e(TAG,"At least till Accept thread Constructor");


            } catch (IOException ex) {
                Log.e(TAG,"At least till Accept thread Constructor error");

                ex.printStackTrace();
            }
            Log.e(TAG,"Server Socket made");

            serverSocket = tmp;
        }

        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket;
            Log.e(TAG,"will try to find client");

            while (state != STATE_CONNECTED) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG,"server socket .accept failed");

                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (chatController.this) {
                        switch (state) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // start the connected thread.
                                Log.e(TAG,"State Connecting");

                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate
                                // new socket.
                                Log.e(TAG,"Already connected or not ready");

                                try {
                                    socket.close();
                                } catch (IOException e) {
                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
    }

    // runs while attempting to make an outgoing connection [client]
    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;
        //private Context c;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
          //  this.c = c;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(DEFAULT_UUID);
                Log.e(TAG,"Connection create secure  one!");
                Log.d(TAG, " made rfcomm " + device.getUuids()[0].getUuid());
                Log.e(TAG,DEFAULT_UUID.toString());


            } catch (IOException e) {
                Log.e(TAG,"Connection create secure one Failed!");

                e.printStackTrace();
            }//CHANGE THIS HERE*/


/*            try {
                // Use the UUID of the device that discovered // TODO Maybe need extra device object
                if (device != null)
                {
                    Log.i(TAG, "Device Name: " + device.getName());
                    Log.i(TAG, "Device UUID: " + device.getUuids()[0].getUuid());
                    Log.i(TAG, "Device UUID: 1" + device.getUuids()[1].getUuid());

                    Log.i(TAG, "Device UUID: 2" + device.getUuids()[2].getUuid());

                    tmp = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
                    Log.d(TAG, " made rfcomm " + device.getName());

                }
                else Log.d(TAG, "Device is null.");
            }
            catch (NullPointerException e)
            {
                Log.d(TAG, " UUID from device is null, Using Default UUID, Device name: " + device.getName());
                try {
                    tmp = device.createRfcommSocketToServiceRecord(DEFAULT_UUID);
                } catch (IOException e1) {
                    Log.d(TAG, "IOException e1 occured");
                    e1.printStackTrace();
                }
            }
            catch (IOException e) {                  Log.d(TAG, "IOException occured");
            }*/
            socket = tmp;
        }

        public void run() {
            setName("ConnectThread");
            Log.e(TAG,"connection make tried[ run[connectthread] ]");

            //Toast.makeText(c, "connection make tried!", Toast.LENGTH_SHORT).show();


            // Always cancel discovery because it will slow down a connection
            bluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                socket.connect();
            } catch (IOException e) {
                try {             Log.e(TAG,"Connection failed once");

                    socket.close();
                } catch (IOException e2) {
                    Log.e(TAG,"Connection closed failed ");
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (chatController.this) {
                connectThread = null;
            }

            // Start the connected thread
            connected(socket, device);
            Log.e(TAG,"connection made");

            //Toast.makeText(c, "connection made!", Toast.LENGTH_SHORT).show();

        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    // runs during a connection with a remote device
    private class ReadWriteThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ReadWriteThread(BluetoothSocket socket) {
            this.bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = inputStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    handler.obtainMessage(bluConnectChat.MESSAGE_READ, bytes, -1,
                            buffer).sendToTarget();
                } catch (IOException e) {
                    connectionLost();
                    // Start the service over to restart listening mode
                    chatController.this.start();
                    break;
                }
            }
        }

        // write to OutputStream
        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
                handler.obtainMessage(bluConnectChat.MESSAGE_WRITE, -1, -1,
                        buffer).sendToTarget();
            } catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}