package com.example.ver.myfirstapp;

import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class yggdrasil extends AppCompatActivity {
    private List<String> chatList;
    private RecyclerView.Adapter chatListAdapter;
   // private chatController chatcontrol;
    private WConnect WController;
    Context context ;
    public static final String WiFiSocketInfo = "Wifi_P2P_Information";
    private WifiP2pInfo wifip2pinfo;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "Blu_Swarm";
    private static final String TAG = "Yggdrasil";
    private String hostAdd;
    private Button button;
    private RecyclerView reView;
    private boolean isDeviceOwner;
    private WifiManager WManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yggdrasil);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chatList = new ArrayList<String>();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        context = this;
        WController = new WConnect(context,handler);
        fillChatData();

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

       // Toast.makeText(this,(String) b.get("bluetoothDeviceAddress"), Toast.LENGTH_LONG).show();
        hostAdd= (String) bundle.get(WiFiSocketInfo);

        startConnection();

    }


    private void setStatus(String s) {
        chatList.add(s);
        chatListAdapter.notifyDataSetChanged();
    }

    private void connectToDevice(String deviceAddress) {
   //     bluetoothAdapter.cancelDiscovery();
         WManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WManager.disconnect();

        //   BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        WController.connect(deviceAddress);
    }

    private void sendMessage(String message) {
        Log.e(TAG,"REached sendmessage() :"+message);

        if (WController.getState() != WController.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), "Connection was lost!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            Log.e(TAG,"sent to write() of wconnect "+send);
            WController.write(send);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        button = (Button) findViewById(R.id.sendData);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(WController.getState() == WConnect.STATE_CONNECTED)
                {                       Log.e(TAG,"Sending message");

                    TextView txtvw = (TextView) findViewById(R.id.chattextiput);
                    String msg =(String)txtvw.getText().toString();
                    txtvw.setText((String)"");
                    Log.e(TAG,"in on click listner message is "+msg);


                    if(msg.length()>0)

                        sendMessage(msg);

                }

            }
        });
        button.setEnabled(true);// FOR TESTING ENABLE BEBELOWWLOW FOR REAL

       // button.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (WController != null) {
            if (WController.getState() == WConnect.STATE_NONE) {
                WController.start();
            }
        }
        if(WController.getState()==WConnect.STATE_CONNECTED)
        {
            button.setEnabled(true);

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( WController != null)

            WController.stop();
    }


    /*public void connectToDevice(){
       // bluetoothAdapter.cancelDiscovery();
        WController.connect(mdevice);
    }*/



    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case WConnect.STATE_CONNECTED:
                            Toast.makeText(getApplicationContext(), "STATE_CONNECTED",  Toast.LENGTH_SHORT).show();

                            // setStatus("Connected to: " + mdevice.getName());
                            button.setEnabled(true);

                            // btnConnect.setEnabled(false);
                            break;
                        case WConnect.STATE_CONNECTING:
                            setStatus("Connecting...");
                            // btnConnect.setEnabled(false);
                            break;
                        case WConnect.STATE_LISTEN:
                        case WConnect.STATE_NONE:
                            button.setEnabled(false);
                            break;
                        case WConnect.STATE_LOST:
                            Log.e(TAG,"Failed TO CONNECT");
                            WController.stop();
                            startConnection();

                    }
                    break;
                case MESSAGE_WRITE:
                    Log.e(TAG,"in handler ");
                    byte[] writeBuf = (byte[]) msg.obj;

                    /String writeMessage = new String(writeBuf);
                    chatList.add("Me: " + writeMessage);


                    chatListAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "MESSAGE WRITE",  Toast.LENGTH_SHORT).show();
                    //reView.getLayoutManager().smoothScrollToPosition(reView,new RecyclerView.State(), reView.getAdapter().getItemCount());

                    //       reView.smoothScrollToPosition(chatList.size() - 1);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);
                    chatList.add("KKMODI " + ":  " + readMessage);//mdevice connectingdevice
                    chatListAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "MESSAGE READ",  Toast.LENGTH_SHORT).show();
                    //eView.getLayoutManager().smoothScrollToPosition(reView,new RecyclerView.State(), reView.getAdapter().getItemCount());
                    //reView.smoothScrollToPosition(chatList.size() - 1);
                    break;
                case MESSAGE_DEVICE_OBJECT:
                    int d =  msg.arg1;
                    Log.e(TAG,"MESSAGE DEVICE OBJECT HERE | KINDA CONNECTED");

                   // int d = msg.getData().getParcelable(DEVICE_OBJECT);//mdevice connectingdevice
                    //Toast.makeText(getApplicationContext(), "Connected to " +d,  Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"), Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    public void fillChatData(){

        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.YChatView);
        recyclerView.setHasFixedSize(true);
        reView=recyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter mAdapter = new chatAdapter(chatList);
        chatListAdapter = mAdapter;
        recyclerView.setAdapter(mAdapter);


    }
    //public boolean isOwner(){return isDeviceOwner;}


    public void startConnection(){

        if(hostAdd.equals("<null>")) {
            isDeviceOwner=true;
            Log.e(TAG,"SerVer");
            // wifip2pinfo.groupOwnerAddress.getHostAddress();
            WController.start();

        }
        else if(!hostAdd.equals("<null>")){
            Log.e(TAG,"Client");
            isDeviceOwner=true;
            setStatus("Starting Connection");
            connectToDevice(hostAdd);

        }



    }


}


/*                      TRY THESE
@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    //PERMISSION GRANTED
                else
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }



    if (ActivityCompat.checkSelfPermission(SmsOtpCheck.this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE}, CODE);
 */