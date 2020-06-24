package com.example.ver.myfirstapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class WConnect {
    private static final String TAG = "WConnect";
    private static final String APP_NAME = "WiFi_Chat_Area";
    public static final int kkmodi=88831;


    public static final int SocketPort=8888;

    private Handler handler;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ReadWriteThread connectedThread;
    private int state;
    private Context context;

    static final int STATE_NONE = 0;
    static final int STATE_LISTEN = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_LOST=4;


    public WConnect(Context context, Handler handler) {
       // bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;
        this.context=context;
        this.handler = handler;
    }
   /* public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }*/
    // Set the current state of the chat connection
    private synchronized void setState(int state) {
        Log.e(TAG,"Changing state from "+this.state+"to :"+state);
        this.state = state;

        handler.obtainMessage(yggdrasil.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
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
    public synchronized void connect(String hostAddress) {
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
        connectThread = new ConnectThread(hostAddress);
        connectThread.start();
        setState(STATE_CONNECTING);
        Log.e(TAG,"Connecting___");

       // Toast.makeText(context, "Connecting___!", Toast.LENGTH_SHORT).show();

    }

    // manage Bluetooth connection
    public synchronized void connected(Socket socket) {
        // Cancel the thread
        Log.e(TAG,"Connected process started ");
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
        Log.e(TAG,"Connected process half done | going to start read write thread");


        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ReadWriteThread(socket); ///PROBLEM
        connectedThread.start();
        Log.e(TAG,"started read write thread and DONE");

        // RUN GET DEVICE NAME HERE ON THE DEVICE CONNECTED AND SEND THAT THROUGH yggdrasil.MESSAGE_DEVICE_OBJECT
        handler.obtainMessage(yggdrasil.MESSAGE_DEVICE_OBJECT, kkmodi, -1).sendToTarget();
        Log.e(TAG,"MESSAGE_DEVICE_OBJECT sent | state CONNECTED");

        // Send the name of the connected device back to the UI Activity
     /*   Message msg = handler.obtainMessage(yggdrasil.MESSAGE_DEVICE_OBJECT);
        Bundle bundle = new Bundle();
        bundle.putParcelable(yggdrasil.DEVICE_OBJECT,"THEME 1");//  , device); {MAYBE SEND SOCKET HERE}
        msg.setData(bundle);
        handler.sendMessage(msg);*/

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
        Log.e(TAG,"Inside Wconnect write() :"+out);

        ReadWriteThread r;
        synchronized (this) {
            if (state != STATE_CONNECTED)
                return;
            r = connectedThread;
        }
        Log.e(TAG,"before write() of thread :"+out);

        r.write(out);
        Log.e(TAG,"after write() of thread :"+out);

    }

    private void connectionFailed() {
        Message msg = handler.obtainMessage(yggdrasil.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Unable to connect device");
        msg.setData(bundle);
        handler.sendMessage(msg);
        setState(STATE_LOST);

        // Start the service over to restart listening mode
       // WConnect.this.start();
    }

    private void connectionLost() {
        Message msg = handler.obtainMessage(yggdrasil.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Device connection was lost");
        msg.setData(bundle);
        handler.sendMessage(msg);
        setState(STATE_LOST);

        // Start the service over to restart listening mode
       // WConnect.this.start();
    }

    // runs while listening for incoming connections   [SERVER] {LISTENING}
    private class AcceptThread extends Thread {
        private final  ServerSocket serverSocket;


        public AcceptThread() {
            ServerSocket tmp = null;
            try {
                tmp = new ServerSocket(8888);


            } catch (IOException ex) {
                Log.e(TAG,"At least till Accept thread Constructor error");

                ex.printStackTrace();
            }
            Log.e(TAG,"Server Socket made");

            serverSocket = tmp;
        }

        public void run() {
            setName("AcceptThread");
            Log.e(TAG,"will try to find CLIENT");
            Socket socket;
            while (state != STATE_CONNECTED) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG,"server socket accept() failed");
                    e.printStackTrace();
                       //socket.close();

                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (WConnect.this) {
                        switch (state) {
                            case STATE_LOST:
                                Log.e(TAG,"FAILED TO CONNECT ACK FROM WCONNECT");
                                break;
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // start the connected thread.
                                Log.e(TAG,"State Connecting");

                                connected(socket);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate
                                // new socket.
                                Log.e(TAG,"Already connected or not ready");

                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG,"FAILED TO CLOSE SOCKET[accept thread run]");
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
                e.printStackTrace();

            }
        }
    }

    // runs while attempting to make an outgoing connection [client] {Connects} (NEEDS HOST AND PORT)
    private class ConnectThread extends Thread {
       // Context context = this.getApplicationContext();
        String host;
      //  int port=8888;
        int len;
        private Socket socket;


        //private Context c;

        public ConnectThread(String HostAdd) {
            host=HostAdd;
          //  this.c = c;
          //  BluetoothSocket tmp = null;
                socket=null;
           // try {
                 socket = new Socket();

                //tmp = device.createRfcommSocketToServiceRecord(DEFAULT_UUID);
             //   Log.e(TAG,"Connection create secure  one!");



           /* } catch (IOException e) {
                Log.e(TAG,"Connection create secure one Failed!");

                e.printStackTrace();
            }*/


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
        //    socket = tmp;
                    }

    public void run() {
        setName("ConnectThread");
        Log.e(TAG, "connection make tried[ run[connect thread] ]");

        //Toast.makeText(c, "connection make tried!", Toast.LENGTH_SHORT).show();


        // Always cancel discovery because it will slow down a connection
        // bluetoothAdapter.cancelDiscovery();

        // Make a connection to the BluetoothSocket
        try {
            //if (socket.)
                socket.bind(null);
            socket.connect((new InetSocketAddress(host, SocketPort)), 500);

        } catch (IOException e) {
            Log.e(TAG, "Connection failed once [connect failed]");
            Log.e(TAG,"Closing socket MORE INFO below");
            e.printStackTrace();

            try{socket.close();}catch (Exception ee){Log.e(TAG,"Closing socket failed");ee.printStackTrace();}
                connectionFailed();
                return;

            // Reset the ConnectThread because we're done


        }


        //Log.e(TAG,"CONNECTED");

// Reset the ConnectThread because we're done
    synchronized (WConnect.this) {
        connectThread = null;
        }
        Log.e(TAG," going to start connected[]");

        // Start the connected thread
        connected(socket);
        Log.e(TAG,"connection made FOR REAL");

        //Toast.makeText(c, "connection made!", Toast.LENGTH_SHORT).show();

        }

        public void cancel() {
        try {
        socket.close();
        } catch (IOException e) {Log.e(TAG,"FAILED TO CLOSE SOCKET {Cancel}");}
        }
 }

// runs during a connection with a remote device
private class ReadWriteThread extends Thread {
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public ReadWriteThread(Socket socket) {
        this.socket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            Log.e(TAG,"getInputStream starting");
            tmpIn = socket.getInputStream();
            Log.e(TAG,"InputStream done "+tmpIn.toString());
            Log.e(TAG," getOutputStream starting");

            tmpOut = socket.getOutputStream();
            Log.e(TAG," sending a message");

            tmpOut.write("kk".getBytes());
            Log.e(TAG,"OutputStream done"+tmpOut.toString());

        } catch (IOException e) {
            Log.e(TAG,"getting input output stream failed more info :");
            e.printStackTrace();
        }

        inputStream = tmpIn;
        outputStream = tmpOut;
        Log.e(TAG,"input/output  stream here");

    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        Log.e(TAG,"readWriteThread run() started");

        // Keep listening to the InputStream
        while (true) {
            try {
                    // Read from the InputStream
                    bytes = inputStream.read(buffer);
                if(!buffer.equals(null)){

                    Log.e(TAG, "read :" + bytes);
                    // Send the obtained bytes to the UI Activity
                    handler.obtainMessage(yggdrasil.MESSAGE_READ, bytes, -1,
                            buffer).sendToTarget();}

            } catch (IOException e) {
                Log.e(TAG,"Some issue during MESSAGE_READ |More Info below |reStarting ");
                e.printStackTrace();
                connectionLost();
                // Start the service over to restart listening mode
             //   WConnect.this.start();
                break;
            }
        }
    }

    // write to OutputStream
    public void write(final byte[] buffer) {

        try {
            outputStream.flush();
           // socket.getOutputStream().write(buffer);

            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        // Your implementation goes here
                        outputStream.write(buffer) ;

                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();

            handler.obtainMessage(yggdrasil.MESSAGE_WRITE, -1, -1,
                    buffer).sendToTarget();

        } catch (IOException e) {
            Log.e(TAG,"Some issue during MESSAGE_WRITE |More Info below ");
            e.printStackTrace();


        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
}

