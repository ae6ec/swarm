package com.example.ver.myfirstapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
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


import java.util.ArrayList;
import java.util.List;

public class bluConnectChat extends AppCompatActivity {
    private List<String> chatList;
    private RecyclerView.Adapter chatListAdapter;
    private chatController chatcontrol;
    Context context ;
    private BluetoothDevice mdevice;
    private BluetoothAdapter bluetoothAdapter;


    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "Blu_Swarm";
    private static final String TAG = "BluConnectChatActivity";
    private final static int HeadsetProfile=1;
    BluetoothProfile mBlueProfile;
    BluetoothHeadset mBluetoothHeadset;


    private Button button;
    private static RecyclerView reView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluchatlayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chatList = new ArrayList<String>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothHeadset=null;
        mBlueProfile=null;
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        context = this;
        chatcontrol = new chatController(context,handler);
        fillChatData();

       Toast.makeText(this,(String) b.get("bluetoothDeviceAddress"), Toast.LENGTH_LONG).show();
        String MacAdd =(String) b.get("bluetoothDeviceAddress");
        if(!MacAdd.equals("<null>")) {
        mdevice = bluetoothAdapter.getRemoteDevice(MacAdd);
            Log.e(TAG,"With MAC");
            connectToDevice((String) b.get("bluetoothDeviceAddress"));
        }
        else{
            Log.e(TAG,"Without MAC");

            setStatus("Starting Connection");
            chatcontrol.start();

        }


    }
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {

        View textbox =  findViewById(R.id.chattextiput);
        Log.d("event", "culoide is: "+textbox.getWidth()); // always 0


    }

    private void setStatus(String s) {
        chatList.add(s);
        chatListAdapter.notifyDataSetChanged();
    }

    private void connectToDevice(String deviceAddress) {
        bluetoothAdapter.cancelDiscovery();

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        chatcontrol.connect(device);
    }

    private void sendMessage(String message) {
        if (chatcontrol.getState() != chatController.STATE_CONNECTED) {
            Toast.makeText(this, "Connection was lost!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            chatcontrol.write(send);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        /*if (!bluetoothAdapter.isEnabled()) {
           // Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
           // startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            chatController = new chatcontroller(this, handler);
        }*/
         button = (Button) findViewById(R.id.sendchat);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(chatcontrol.getState() == chatController.STATE_CONNECTED)
                {                       Log.e(TAG,"Sending message");

                    TextView txtvw = (TextView) findViewById(R.id.chattextiput);
                    String msg =(String)txtvw.getText().toString();
                    txtvw.setText((String)"");

                    if(!msg.equals(""))

                    sendMessage(msg);

                }

            }
        });

    button.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (chatcontrol != null) {
            if (chatcontrol.getState() == chatController.STATE_NONE) {
                chatcontrol.start();
            }
        }
        if(chatcontrol.getState()==chatController.STATE_CONNECTED)
        {
            button.setEnabled(true);

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( chatcontrol != null)
            chatcontrol.stop();
    }


    public bluConnectChat(){

        //chatcontrol.start();

    }


    public void connectToDevice(){
        bluetoothAdapter.cancelDiscovery();
        chatcontrol.connect(mdevice);
    }


    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case chatController.STATE_CONNECTED:
                           /* Intent enableBtIntent2 = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                            enableBtIntent2.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
                            startActivityForResult(enableBtIntent2, 1);*/
                            setStatus("Connected to: " + mdevice.getName());
                            button.setEnabled(true);

                            // btnConnect.setEnabled(false);
                            break;
                        case chatController.STATE_CONNECTING:
                            setStatus("Connecting...");
                           // btnConnect.setEnabled(false);
                            break;
                        case chatController.STATE_LISTEN:
                        case chatController.STATE_NONE:
                            button.setEnabled(false);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;

                    String writeMessage = new String(writeBuf);
                    chatList.add("Me: " + writeMessage);


                    chatListAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "now scroll",  Toast.LENGTH_SHORT).show();
                    reView.getLayoutManager().smoothScrollToPosition(reView,new RecyclerView.State(), reView.getAdapter().getItemCount());

             //       reView.smoothScrollToPosition(chatList.size() - 1);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);
                    chatList.add(mdevice.getName() + ":  " + readMessage);//mdevice connectingdevice
                    chatListAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "now scroll",  Toast.LENGTH_SHORT).show();
                    reView.getLayoutManager().smoothScrollToPosition(reView,new RecyclerView.State(), reView.getAdapter().getItemCount());
                    //reView.smoothScrollToPosition(chatList.size() - 1);
                    break;
                case MESSAGE_DEVICE_OBJECT:
                    mdevice = msg.getData().getParcelable(DEVICE_OBJECT);//mdevice connectingdevice
                    Toast.makeText(getApplicationContext(), "Connected to " + mdevice.getName(),  Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"), Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    public void fillChatData(){

        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.bluChatrecyclerview);
        recyclerView.setHasFixedSize(true);
        reView=recyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter mAdapter = new chatAdapter(chatList);
        chatListAdapter = mAdapter;
        recyclerView.setAdapter(mAdapter);


    }

}
