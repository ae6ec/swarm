/*package com.example.ver.myfirstapp;

public class WiFiDirectUIActivity {
}*/
package com.example.ver.myfirstapp;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Message;
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
import java.util.Collection;
import java.util.List;

public class WiFiDirectUIActivity extends Fragment {

    private static final String TAG = "WiFiDirectActivity";
    private static int LIST_VIEW=1;
    private static int TEXT_VIEW=0;


    //private boolean bluPresent = true;
    //private BluetoothAdapter mBluetoothAdapter;
    private View WiFiDirectrootview;
    private List<WifiP2pDevice> WiFiPeers;
    //private WifiP2pDeviceList WiFiPeers;
    private RecyclerView re1,re2;
    RecyclerView.Adapter re1a,re2a;
   // private bluConnectChat bluChat;
    private WiFiDirect WiFiDManager;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiManager wifi;
    private SpeedDialView speedDialView;
    private WifiP2pManager.PeerListListener myPeerList;
    private WifiP2pDevice device;
    private WifiP2pDeviceList p2plistcollection;

    public static WiFiDirectUIActivity newInstance() {
       // Log.e(TAG,"new instance start");

        Bundle args = new Bundle();
        WiFiDirectUIActivity fragment = new WiFiDirectUIActivity();
        fragment.setArguments(args);
       //w Log.e(TAG,"ne    w instance ended");
        //activity =fragment;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
       // Log.e(TAG,"On create ");

        super.onCreate(savedInstanceState);
        WiFiPeers = new ArrayList<WifiP2pDevice>();
        p2plistcollection=new WifiP2pDeviceList();
       // mReceiverListPaired = new ArrayList<device_class>();


        //chatcontrol = new chatController( getView().getContext(), handler);
        //bluChat = new bluConnectChat();

        // handler = bluChat.getChathandler();
        //  chatcontrol = new chatController(getContext(),handler);
       // Log.e(TAG,"On create ended");

    }

    public void txtAndList(int k){
        TextView txtView = WiFiDirectrootview.findViewById(R.id.WiFiTxtView);
        TextView wifiknownlist =  WiFiDirectrootview.findViewById(R.id.WiFiKnownName);
        TextView wifiDlist =  WiFiDirectrootview.findViewById(R.id.WiFiDName);

        if(k==1) {
            txtView.setVisibility(View.GONE);
            //wifiknownlist.setVisibility(View.VISIBLE);
          //  re2.setVisibility(View.VISIBLE);
            wifiDlist.setVisibility(View.VISIBLE);
            re1.setVisibility(View.VISIBLE);

        }
        else{
            txtView.setVisibility(View.VISIBLE);
            re1.setVisibility(View.GONE);
         //   re2.setVisibility(View.GONE);
            //wifiknownlist.setVisibility(View.GONE);
            wifiDlist.setVisibility(View.GONE);
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
        Log.e(TAG,"On create view started");

        View view = inflater.inflate(R.layout.activity_wifidirect_ui_activity, container, false);
        WiFiDirectrootview= view;

        /*boolean k=WiFiDManager.isWiFiDPresent();
        if(k)
        Log.e(TAG,"Wifi is enabled ");
        else
            Log.e(TAG,"Wifi is disabled ");
*/
        wifi = (WifiManager) WiFiDirectrootview.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WiFiDManager = new WiFiDirect(getContext() , handler);
           // WiFiPeers.putWifiManager(WiFiDManager);
            mManager=WiFiDManager.getWiFiManager();
            mChannel=WiFiDManager.getWiFiChannel();
            speedDialView = WiFiDirectrootview.findViewById(R.id.WiFifabMain);

          /*  if (WiFiDManager.isWiFiDPresent() == 0) {
                Log.e(TAG, "wifi present value 2" + WiFiDManager.isWiFiDPresent());

                SpeedDialView speedDialView = WiFiDirectrootview.findViewById(R.id.WiFifabMain);
                speedDialView.setVisibility(View.GONE);
                ((MainActivity) getActivity()).dispToast("WiFi P2P Not Enabled");
                TextView tv = WiFiDirectrootview.findViewById(R.id.WiFiTxtView);
                tv.setText(R.string.WiFiDNP);
            } else if (WiFiDManager.isWiFiDPresent() == 1) {
                createWifiFabMenu();
                Log.e(TAG, "wifi present value " + WiFiDManager.isWiFiDPresent());

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
                    ((MainActivity) getActivity()).dispToast("Location Permission is must for WiFi to work");
            }
            else {
                ((MainActivity) getActivity()).dispToast("WiFi NOTHING");

            }*/

        //   View view = Blerootview;

        // ((MainActivity)getActivity()).dispToast("filled data !");
        Log.e(TAG,"On create view [creating recycler view]");

        //paired
        RecyclerView recyclerView = (RecyclerView) WiFiDirectrootview.findViewById(R.id.WiFiDList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter mAdapter = new myAdapterWiFi(WiFiPeers,WiFiDManager);
        recyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(WiFiDirectrootview.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        re1=recyclerView;
        re1a=mAdapter;
        recyclerView.setNestedScrollingEnabled(false);
        /*RecyclerView recyclerView2 = (RecyclerView) view.findViewById(R.id.WiFiViewKnownList);
        recyclerView2.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        recyclerView2.setLayoutManager(layoutManager2);
        //speedDialView.setVisibility(View.GONE);


        RecyclerView.Adapter mAdapter2 = new myAdapterWiFi(WiFiPeers);
        recyclerView2.setAdapter(mAdapter2);
        RecyclerView.ItemDecoration itemDecoration2 =
                new DividerItemDecoration(WiFiDirectrootview.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView2.addItemDecoration(itemDecoration2);


        recyclerView2.setNestedScrollingEnabled(false);

        re2=recyclerView2;
        re2a=mAdapter2;*/
        // txtAndList();


        Log.e(TAG,"On create view ended");
        createWifiFabMenu();
        //  fillData();
        return view;
    }







    public void createWifiFabMenu(){
        SpeedDialView speedDialView = WiFiDirectrootview.findViewById(R.id.WiFifabMain);

      //  Log.e(TAG,"creation of fab menu started");
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.WiFiOnOff, android.R.drawable.stat_sys_data_bluetooth)
                        .setLabel(getString(R.string.WifiOnOff))
                        .setLabelColor(Color.BLUE)
                        .setLabelClickable(true)
                        .create()
        );

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.WiFiDisc, android.R.drawable.stat_sys_data_bluetooth)
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
                new SpeedDialActionItem.Builder(R.id.WiFiRecive, android.R.drawable.stat_sys_data_bluetooth)
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

        //Log.e(TAG,"creation of fab menu [listners adding]");


        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                switch (speedDialActionItem.getId()) {
                    case R.id.WiFiRecive:
                            ((MainActivity)getActivity()).dispToast("Enable WiFi First");
                            return true;
                    case R.id.blueOff1:

                       // WiFiPeers=WiFiDManager.getP2PList();

                        //((MainActivity)getActivity()).dispToast("Starting getting list"+WiFiDManager.getP2PList("A").toString());

                        //Log.d(TAG, "OnPeer Success p2p one"+WiFiDManager.getP2PList().toString());


                        re1a.notifyDataSetChanged();
                        re2a.notifyDataSetChanged();
                        return false;
                    case R.id.WiFiDisc:
                        if(wifi.isWifiEnabled()){
                            Log.e(TAG,"WiFiDDiscover() starting");
                            WiFiDManager.wiFiDDiscover();

                        }
                        else{
                            Log.e(TAG,"WiFiDDiscover() could not start");
                            ((MainActivity)getActivity()).dispToast("Enable Wifi");
                        }
                        //if (!mBluetoothAdapter.isEnabled()) {
                           // txtAndList(TEXT_VIEW);
                            return false;
                        //}
                       /* mReceiverList.clear();
                        txtAndList(LIST_VIEW);
                        txtAndList(LIST_VIEW);
                        BluDiscovery();*/
                      //  return false;
                    case R.id.WiFiOnOff:
                      if(wifi.isWifiEnabled()){
                            Log.e(TAG,"Wifi is enabled");
                          Log.e(TAG,"Disabled");
                            txtAndList(TEXT_VIEW);

                          //Perform Operation
                          wifi.setWifiEnabled(false);
                          }
                        else{
                          Log.e(TAG,"Wifi is Disabled");
                          Log.e(TAG,"enabling");
                          txtAndList(TEXT_VIEW);

                          //Other Operation[
                          wifi.setWifiEnabled(true);
                              }

                        return false; // true to keep the Speed Dial open
                    default:
                        return false;
                }
            }
        });
      //  Log.e(TAG,"creation of fab menu ended");

    }
//bluetooth_ui_activity onActivityResult here
//bluetooth_ui_activity BluDiscovery here
//bluetooth_ui_activity BroadcastReceiver mReceiver here


    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "IN HANDLER");
            switch (msg.what) {
                /*case MESSAGE_STATE_CHANGE:

                    break;*/
                case WiFiDirect.onPeersReceive:
                   // ((MainActivity) getActivity()).dispToast("OnPeerReceive Success");

                    Log.d(TAG, "OnPeerReceive Success");

                    switch (msg.arg1) {
                        case WiFiDirect.onPeersReceiveSucess:
                            // createWifiFabMenu();
                           // Log.e(TAG,"wifipeers before adding "+WiFiPeers.toString());

                            WiFiPeers.clear();
                           // Log.e(TAG,"wifipeers cleared "+WiFiPeers.toString());

                            //  p2plistcollection=(WifiP2pDeviceList) WiFiDManager.getP2PCollection();
                            WiFiPeers.addAll(p2plistcollection.getDeviceList());
                            //  WiFiPeers.add(p2plistcollection.getDeviceList());

                            // WiFiPeers.
                            re1a.notifyDataSetChanged();
                            // re2a.notifyDataSetChanged();
                            //Log.e(TAG,"JUST Testing");
                           // ((MainActivity) getActivity()).dispToast("DonEEEEE!!!!");
                           // Log.e(TAG,"wifi peers "+WiFiPeers.toString());


                            if(WiFiPeers.size()>0)
                                txtAndList(LIST_VIEW);
                            else
                                txtAndList(TEXT_VIEW);


                            break;
                        case WiFiDirect.onPeersReceiveFailed:
                            // Log.e(TAG, "wifi present value 2" + WiFiDManager.isWiFiDPresent());

                            //((MainActivity) getActivity()).dispToast("NO DEVICE FOUND!!!!");
                            Log.d(TAG, "No devices found");
                            txtAndList(TEXT_VIEW);


                    break;
                    }

                        //  Log.e(TAG, myPeerList.toString());
                 //   for (WifiP2pDevice device  myPeerList )


                    break;
                case WiFiDirect.P2PListUpdate:

                    p2plistcollection = msg.getData().getParcelable(WiFiDirect.P2PList_Object);
                    Log.d(TAG, "List Acquired "+p2plistcollection.toString());

                    break;

                case WiFiDirect.P2P:
                         switch (msg.arg1) {
                           case WiFiDirect.P2PAvailable:
                              // createWifiFabMenu();
                               TextView tv = WiFiDirectrootview.findViewById(R.id.WiFiTxtView);
                               tv.setText(R.string.Wifi);

                              // Log.e(TAG, "wifi present value " + WiFiDManager.isWiFiDPresent());
                               ((MainActivity) getActivity()).dispToast("WiFi P2P Enabled");

                               if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                       != PackageManager.PERMISSION_GRANTED)
                                   ((MainActivity) getActivity()).dispToast("Location Permission is must for WiFi to work");

                               break;
                            case WiFiDirect.P2PNotAvailable:
                               // Log.e(TAG, "wifi present value 2" + WiFiDManager.isWiFiDPresent());

                                ((MainActivity) getActivity()).dispToast("WiFi P2P Not Enabled");
                                TextView tvw = WiFiDirectrootview.findViewById(R.id.WiFiTxtView);
                                tvw.setText(R.string.WiFiDNP);

                                break;

                                       }
                    break;

            }
            return false;
        }
    });



    @Override
    public void onResume() {
        Log.e(TAG,"Onresume started [intent filter ]");

        super.onResume();

        IntentFilter mIntentFilter;
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);// remove this for test
        getActivity().registerReceiver(WiFiDManager, mIntentFilter);

        // IntentFilter filter = new IntentFilter();
       // filter.addAction(BluetoothDevice.ACTION_FOUND);
       // filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
       // ((MainActivity) getActivity()).registerReceiver(mReceiver, filter);

        //  filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
//mReceiverDiscover
       // IntentFilter filter2 = new IntentFilter();
       /// filter2.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
       // ((MainActivity) getActivity()).registerReceiver(mReceiverDiscover, filter2);

      /*  if (chatController != null) {
         if (chatController.getState() == ChatController.STATE_NONE) {
             chatController.start();
           }
      }*/
        Log.e(TAG,"Onresume ended");

    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"ondestory started");
        getActivity().unregisterReceiver(WiFiDManager);

        super.onDestroy();
        //unregisterReceiver(mReceiverDiscover);

       // ((MainActivity) getActivity()).unregisterReceiver(mReceiver);
        //}catch (Exception e){e.printStackTrace();}

        //((MainActivity) getActivity()).unregisterReceiver(mReceiverDiscover);


       /* if (chatController != null)
            chatController.stop();*/
       // Log.e(TAG,"ondestroy ended");

    }


    @Override
    public void onPause() {
        Log.e(TAG,"onpause started [unregistering intent    ]");

        super.onPause();
        getActivity().unregisterReceiver(WiFiDManager);

     //   ((MainActivity)getActivity()).unregisterReceiver(mReceiver);
      //  ((MainActivity)getActivity()).unregisterReceiver(mReceiverDiscover);
        Log.e(TAG,"onpause ended [unregistering intent done]");

    }

    @Override
    public void onStart() {

        super .onStart();
        IntentFilter mIntentFilter;
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);// remove this for test

        getActivity().registerReceiver(WiFiDManager, mIntentFilter);


    }






}


