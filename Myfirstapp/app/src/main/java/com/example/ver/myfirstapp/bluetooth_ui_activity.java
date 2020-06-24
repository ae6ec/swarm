package com.example.ver.myfirstapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class bluetooth_ui_activity extends Fragment {
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_DISCOVERABLE_BT = 0;

    private static final String TAG = "BluMainactivity";
    private static int LIST_VIEW=1;
    private static int TEXT_VIEW=0;

    private BluetoothAdapter mBluetoothAdapter;
    private View Blerootview;
    List<device_class> mReceiverList;
    List<device_class> mReceiverListPaired;
    RecyclerView re1,re2;
    RecyclerView.Adapter re1a,re2a;
    private Handler handler;



    public static bluetooth_ui_activity newInstance() {
        Bundle args = new Bundle();
        bluetooth_ui_activity fragment = new bluetooth_ui_activity();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiverList = new ArrayList<device_class>();
        mReceiverListPaired = new ArrayList<device_class>();

    }

    public void txtAndList(int k){
        TextView txtView = Blerootview.findViewById(R.id.blutextView);
        TextView txtView2 =  Blerootview.findViewById(R.id.bluPairedList);
        TextView txtView3 =  Blerootview.findViewById(R.id.bluNPList);

        if(k==1) {
            txtView.setVisibility(View.GONE);
            txtView2.setVisibility(View.VISIBLE);
            re2.setVisibility(View.VISIBLE);
            txtView3.setVisibility(View.VISIBLE);
            re1.setVisibility(View.VISIBLE);

        }
        else{
            txtView.setVisibility(View.VISIBLE);
            re1.setVisibility(View.GONE);
            re2.setVisibility(View.GONE);
            txtView2.setVisibility(View.GONE);
            txtView3.setVisibility(View.GONE);
        }
    }

    public void fillData(){
        re1a.notifyDataSetChanged();

        /*View view = Blerootview;

        ((MainActivity)getActivity()).dispToast("filled data !");

        //paired
        RecyclerView recyclerView = (RecyclerView) Blerootview.findViewById(R.id.bluview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter mAdapter = new MyAdapter(mReceiverList);
        recyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        re1=recyclerView;
        re1a=mAdapter;
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView recyclerView2 = (RecyclerView) view.findViewById(R.id.bluViewPaired);
        recyclerView2.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        recyclerView2.setLayoutManager(layoutManager2);


        RecyclerView.Adapter mAdapter2 = new MyAdapter(mReceiverListPaired);
        recyclerView2.setAdapter(mAdapter2);
        RecyclerView.ItemDecoration itemDecoration2 =
                new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        recyclerView2.addItemDecoration(itemDecoration2);


        recyclerView2.setNestedScrollingEnabled(false);

        re2=recyclerView2;
        re2a=mAdapter2;
        txtAndList();*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bluetooth_ui_layout, container, false);
        Blerootview= view;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            ((MainActivity)getActivity()).dispToast("Bluetooth Not Present");
            SpeedDialView speedDialView = Blerootview.findViewById(R.id.fabBluMain);
            speedDialView.setVisibility(View.GONE);
            TextView tv = getView().findViewById(R.id.blutextView);
            tv.setText(R.string.BluNotPresent);
            }

        else {            createblufabmenu();
            if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                ((MainActivity)getActivity()).dispToast("Location Permission is must for bluetooth to work");

        }
     //   View view = Blerootview;

       // ((MainActivity)getActivity()).dispToast("filled data !");

        //paired
        RecyclerView recyclerView = (RecyclerView) Blerootview.findViewById(R.id.bluview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter mAdapter = new MyAdapter(mReceiverList);
        recyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration =new DividerItemDecoration(Blerootview.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        re1=recyclerView;
        re1a=mAdapter;
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView recyclerView2 = (RecyclerView) view.findViewById(R.id.bluViewPaired);
        recyclerView2.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        recyclerView2.setLayoutManager(layoutManager2);


        RecyclerView.Adapter mAdapter2 = new MyAdapter(mReceiverListPaired);
        recyclerView2.setAdapter(mAdapter2);
        RecyclerView.ItemDecoration itemDecoration2 =
                new DividerItemDecoration(Blerootview.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView2.addItemDecoration(itemDecoration2);


        recyclerView2.setNestedScrollingEnabled(false);

        re2=recyclerView2;
        re2a=mAdapter2;
       // txtAndList();



        //  fillData();
        return view;
    }



    public void createblufabmenu() {


        SpeedDialView speedDialView = Blerootview.findViewById(R.id.fabBluMain);
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.bluOnOff, android.R.drawable.stat_sys_data_bluetooth)
                        .setLabel(getString(R.string.bluOnOff))
                        .setLabelColor(Color.BLUE)
                        .setLabelClickable(true)
                        .create()
        );

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.bluDisc, android.R.drawable.stat_sys_data_bluetooth)
                        .setLabel(getString(R.string.Disc))
                        .setLabelColor(Color.BLACK)
                        .setLabelClickable(true)
                        .create()
        );
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.blueOff1, android.R.drawable.stat_sys_data_bluetooth)
                        .setLabel(getString(R.string.Ooshit))
                        .setLabelColor(Color.GRAY)
                        .setLabelClickable(true)
                        .create()
        );
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.bluAcceptThread, android.R.drawable.stat_sys_data_bluetooth)
                        .setLabel(getString(R.string.bluStartAcceptThread))
                        .setLabelColor(Color.GRAY)
                        .setLabelClickable(true)
                        .create()
        );
       /* speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.testoff, android.R.drawable.stat_sys_data_bluetooth)
                        .setLabel(getString(R.string.bluDiscoverability))
                        .setLabelColor(Color.GRAY)
                        .setLabelClickable(true)
                        .create()
        );*/



        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                switch (speedDialActionItem.getId()) {
                    case R.id.bluAcceptThread:
                                if (!mBluetoothAdapter.isEnabled()) {
                                    ((MainActivity)getActivity()).dispToast("Enable Bluetooth First");
                                     return true;
                                    }


                                      Intent intent = new Intent(getContext(), bluConnectChat.class);
                                         intent.putExtra("bluetoothDeviceAddress","<null>");
                                      getContext().startActivity(intent);
                        return false;
                    /*case R.id.testoff:
                        if(mBluetoothAdapter.isDiscovering()) {
                            ((MainActivity)getActivity()).dispToast("Not Discovering Changing that");
                            Intent enableBtIntent2 = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                            enableBtIntent2.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
                            startActivityForResult(enableBtIntent2, REQUEST_DISCOVERABLE_BT);
                        }
                        else {
                            ((MainActivity)getActivity()).dispToast("Discovering Changing that");


                            Intent enableBtIntent2 = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                            startActivityForResult(enableBtIntent2, REQUEST_DISCOVERABLE_BT);
                        }
                        return false;*/
                    case R.id.blueOff1:     for (int i=0;i<100;i++)
                        mReceiverList.add(new device_class("test device"+i,"real shit "+i));
                                             for (int i=0;i<50;i++)
                                                  mReceiverListPaired.add(new device_class("test paired device"+i,"real paired shit "+i));

                                             re1a.notifyDataSetChanged();
                                              re2a.notifyDataSetChanged();
                                              return false;
                         case R.id.bluDisc:
                                            if (!mBluetoothAdapter.isEnabled()) {
                                                  ((MainActivity)getActivity()).dispToast("Enable Bluetooth First");
                                                txtAndList(TEXT_VIEW);
                                                   return true;
                                                       }
                             mReceiverList.clear();
                             txtAndList(LIST_VIEW);
                             BluDiscovery();
                                             return false;
                        case R.id.bluOnOff:
                                          if (!mBluetoothAdapter.isEnabled()) {

                                              Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                                              enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                                              startActivityForResult(enableBtIntent, REQUEST_DISCOVERABLE_BT);
                                          }
                                          else { mBluetoothAdapter.disable();
                                                    mBluetoothAdapter.cancelDiscovery();
                                            }

                                            return false; // true to keep the Speed Dial open
                    default:
                        return false;
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == MainActivity.RESULT_OK) {
                    //chatController = new ChatController(this, handler);
                } else {
                    ((MainActivity)getActivity()).dispToast("Bluetooth still disabled, Please try again!");

                }
        }
    }


    public void BluDiscovery() {
       // mReceiverListPaired.clear();
      //  txtAndList();

        ((MainActivity)getActivity()).dispToast("Bluetooth  Discovery on!");
        if(mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                mReceiverListPaired.add(new device_class(deviceName,deviceHardwareAddress));

            }
        }
        re1.removeAllViewsInLayout();
        mBluetoothAdapter.startDiscovery();



    }

    // Create a BroadcastReceiver for ACTION_FOUND.
/*
    private final BroadcastReceiver mReceiverDiscover = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //  ((MainActivity)getActivity()).dispToast("Insider Reg Rec");
            String action = intent.getAction();
            if (mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                // BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //String deviceName = device.getName();
                // String deviceHardwareAddress = device.getAddress(); // MAC address
                // ((MainActivity)getActivity()).dispToast(deviceHardwareAddress);
                //mReceiverList.add(new device_class(deviceName,deviceHardwareAddress));
                //fillData();
                ((MainActivity)getActivity()).dispToast("Discoverable 300");

            }
        }


    };*/



    private final BroadcastReceiver mReceiverDiscover = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            //if (action.equals(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                ((MainActivity)getActivity()).dispToast("Discoverable 300");
                Log.d(TAG, "mBroadcastRec eiver2: Discoverability Enabled.");

                /*switch (mode) {
                    //Device is in Discoverable Mode
                    case mBluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case mBluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case mBluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case mBluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case mBluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }*/

            }
        }
};



    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          //  ((MainActivity)getActivity()).dispToast("Insider Reg Rec");

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
               // ((MainActivity)getActivity()).dispToast(deviceHardwareAddress);
                mReceiverList.add(new device_class(deviceName,deviceHardwareAddress));
                fillData();
            }
           else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                ((MainActivity)getActivity()).dispToast("Bluetooth Complete action done");
                // if (mReceiverListPaired.getCount() == 0) {
                   //     mReceiverListPaired.add(new (R.string.blu));
                    //}
                }

        }


    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        ((MainActivity) getActivity()).registerReceiver(mReceiver, filter);

        //  filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
//mReceiverDiscover
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        ((MainActivity) getActivity()).registerReceiver(mReceiverDiscover, filter2);

      /*  if (chatController != null) {
         if (chatController.getState() == ChatController.STATE_NONE) {
             chatController.start();
           }
      }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       //unregisterReceiver(mReceiverDiscover);

            ((MainActivity) getActivity()).unregisterReceiver(mReceiver);
        //}catch (Exception e){e.printStackTrace();}

        ((MainActivity) getActivity()).unregisterReceiver(mReceiverDiscover);


       /* if (chatController != null)
            chatController.stop();*/
    }



//}


    @Override
    public void onPause() {
        super.onPause();

        ((MainActivity)getActivity()).unregisterReceiver(mReceiver);
        ((MainActivity)getActivity()).unregisterReceiver(mReceiverDiscover);
    }

    @Override
    public void onStart() {
        super .onStart();
    }






}

