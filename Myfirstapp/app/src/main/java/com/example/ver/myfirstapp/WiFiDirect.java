package com.example.ver.myfirstapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class WiFiDirect extends BroadcastReceiver implements WifiP2pManager.PeerListListener{//},WifiP2pManager.ConnectionInfoListener {
    private static final String TAG = "WiFiDirectManger";
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Context context;
    private android.os.Handler handler;
    private WifiP2pManager.PeerListListener myPeerListListener;
    private List<WifiP2pDevice> Peers;
    private WifiP2pDeviceList p2pcollection;
    private boolean isconnected=false;
    private WifiManager WManager;


    public static final int onPeersReceive = 2;
    public static final int onPeersReceiveSucess = 21;
    public static final int onPeersReceiveFailed = 20;


    public static final int P2P = 1;
    public static final int P2PAvailable = 11;
    public static final int P2PNotAvailable = 10;

    public static final int P2PListUpdate = 100;
    public static final String P2PList_Object = "WiFiP2PList";



    // public int WiFiPresent=-1;

    public WiFiDirect(//WifiP2pManager manager, WifiP2pManager.Channel channel,
                      Context context, Handler handler) {
        super();

        Log.e(TAG, "On create view started");
        this.handler = handler;
        this.context = context;
        mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(context, context.getMainLooper(), null);
        Peers = new ArrayList<WifiP2pDevice>();
        p2pcollection =new WifiP2pDeviceList ();
    }

    public WifiP2pManager getWiFiManager() {
        return mManager;
    }

    public WifiP2pManager.Channel getWiFiChannel() {
        return mChannel;
    }

    public synchronized void wiFiDDiscover() throws IllegalStateException {
        if (mChannel != null) {

          try{
              mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
              @Override
              public void onSuccess() {
                  Log.e(TAG, "Discovering Process Starts ........... ");
                  //...
              }

              @Override
              public void onFailure(int reasonCode) {
                  Log.e(TAG, "Discover peers failed Reason being : " + reasonCode);
                  //callOnError(new RuntimeException("failed to start discovery, reason=" + reason));
                  //...
              }
                     });
          }
               catch(IllegalStateException e)
              {
                  Log.e(TAG, "IllegalStateException "+e);

                  throw new IllegalStateException("not registered");

              }
              catch (Exception a)
              {
                  Log.e(TAG, "Exception "+a);
                  //throw new Exception ( "not registered");


              }

        } else {
            Log.e(TAG, "NO channel ");


        }
    }
    //  public int isWiFiDPresent(){return WiFiPresent;}

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();

        Log.e(TAG, "Inside the broadcast receive " + action);
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                handler.obtainMessage(P2P, P2PAvailable, -1).sendToTarget();
                Log.e(TAG, "Wifi P2P is enabled ");

            } else {
                handler.obtainMessage(P2P, P2PNotAvailable, -1).sendToTarget();
                Log.e(TAG, "Wifi P2P is disabled ");
                isconnected=false;
                // Wi-Fi P2P is not enabled
            }

            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            Log.e(TAG, "Requesting WIFI LIST ");
            //  ((MainActivity) context).dispToast("Wifi P2P request peers");


            if (mManager != null) {
                Log.e(TAG, "RequestPeers() starting");

                mManager.requestPeers(mChannel, (WifiP2pManager.PeerListListener) this);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            //Toast.makeText(context, "Inside connection_changed",Toast.LENGTH_SHORT).show();

            Log.e(TAG," Inside connection_changed");
            if (mManager == null) {
                Log.e(TAG," NO MANAGER");
                isconnected=false;


                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                Log.e(TAG,"Success on networkInfo.isConnected() reaching");

                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {

                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                      //  Toast.makeText(context,"THEME DA!!!!"+info.groupOwnerAddress.getHostAddress(), Toast.LENGTH_LONG).show();
                        String HostAdd="<null>";
                        // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
                        Log.e(TAG,"wifi p2p info available :"+info.groupOwnerAddress.getHostAddress());
                        // InetAddress from WifiP2pInfo struct.
                        //InetAddress.getByName(wifip2pinfo.groupOwnerAddress.getHostAddress());
                             HostAdd = info.groupOwnerAddress.getHostAddress();

                        // After the group negotiation, we can determine the group owner
                        // (server).
                        if (info.groupFormed && info.isGroupOwner) {
                           // Toast.makeText(context,"OWNER!!", Toast.LENGTH_LONG).show();

                            Log.e(TAG,"I am Owner for some reason");
                            Intent intent = new Intent(context, yggdrasil.class);
                            intent.putExtra(yggdrasil.WiFiSocketInfo,"<null>");
                            context.startActivity(intent);


                            // Do whatever tasks are specific to the group owner.
                            // One common case is creating a group owner thread and accepting
                            // incoming connections.
                        } else if (info.groupFormed) {
                           // Toast.makeText(context,"CLIENT!!", Toast.LENGTH_LONG).show();

                            Log.e(TAG,"Not the owner for some good reason");
                            Intent intent = new Intent(context, yggdrasil.class);
                            intent.putExtra(yggdrasil.WiFiSocketInfo,HostAdd);
                            context.startActivity(intent);
                            // The other device acts as the peer (client). In this case,
                            // you'll want to create a peer thread that connects
                            // to the group owner.
                        }

                    }

                });
            }


                // Respond to new connection or disconnections
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
            } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);

                if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED)
                    Log.e(TAG, "Wifi P2P Discovery started");
                else if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) {
                    Log.e(TAG, "Wifi P2P Discovery Stoppped");
                    mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.e(TAG, "Discover peers Stopped ");
                            //...
                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            Log.e(TAG, "Discover peers failed Reason being : " + reasonCode);
                            //callOnError(new RuntimeException("failed to start discovery, reason=" + reason));
                            //...
                        }

                    });
               // }  else if (WifiP2pManager..equals(action)) {
                   // boolean connected = (boolean) intent
                   //         .getParcelableExtra(WifiP2pManager.Wifi);
                    // Respond to this device's wifi state changing
                } else
                    Log.e(TAG, "Wifi P2P Discovery (Something) :" + action + "in State " + state);


                // Respond to this device's wifi state changing
            }
        }



    public static final String WifiPeerList = "WiFiDevicePeerList";
    public List<WifiP2pDevice> getP2PList(){return Peers;}
    public List<WifiP2pDevice> getP2PList(String S){ return Peers;}
    public boolean isConnectedToAnyDevice(){return isconnected;}

    public WifiP2pDeviceList getP2PCollection(){return p2pcollection;}//(Collection<WifiP2pDevice>)


    @Override
    // PeerListListener.onPeersAvailable()

    public void onPeersAvailable(WifiP2pDeviceList wifip2plist) {
       // npeers++;
        //String action = intent.getAction();
        //  Log.e(TAG,wifip2plist.toString());
       // (MainActivity)context.getActivity().dispToast();
       // Toast.makeText(context, "Peers are here",Toast.LENGTH_SHORT).show();

        //Log.e(TAG,Peers+"are here");
        //p2pcollection();
        p2pcollection=wifip2plist;
        Peers.clear();
        Peers.addAll(wifip2plist.getDeviceList());//)= .toList();
         Log.e(TAG,"Peer list is "+Peers.size());
         if(Peers.size()>0) {
             // Send the name of the connected device back to the UI Activity
             Message msg = handler.obtainMessage(P2PListUpdate);
             Bundle bundle = new Bundle();
             bundle.putParcelable(P2PList_Object, wifip2plist);
             msg.setData(bundle);
             handler.sendMessage(msg);
         }

        if(Peers.size()>0){
           // Log.e(TAG,"Success trying again");

           // handler.obtainMessage(onPeersReceive,onPeersReceiveSucess, -1).sendToTarget();
            handler.obtainMessage(onPeersReceive, onPeersReceiveSucess, -1).sendToTarget();


           // Log.e(TAG,"Success trying again 2");

          // handler.obtainMessage(P2P,P2PAvailable, -1).sendToTarget();
        }
        else {
        //    Log.e(TAG,"Failure");

            handler.obtainMessage(onPeersReceive,onPeersReceiveFailed, -1).sendToTarget();}

        //if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
    }

    //@Override
    public void connect(final WifiP2pDevice Device) {
        WManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        WManager.disconnect();


        Log.e(TAG,"Connect Android inside"+Device.deviceName);
        //Toast.makeText(context, "Connect Android inside",Toast.LENGTH_SHORT).show();
        mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "Discover peers Stopped [because connect ]");
                //...
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.e(TAG, "Discover peers failed [ before connect ] Reason being : " + reasonCode);
                //callOnError(new RuntimeException("failed to start discovery, reason=" + reason));
                //...
            }

        });

        if(Device!=null) {
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = Device.deviceAddress;
            config.wps.setup = WpsInfo.PBC;

           // Toast.makeText(context, "!device inside",Toast.LENGTH_SHORT).show();
//android.net.wifi.p2p.
            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
                    Log.e(TAG,"Success on connect initiation ");

                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(context, "Connect failed. Retry.",Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"Failure on connect initiation with"+Device.deviceName+" for reason "+reason);
                }
            });

            // config.deviceAddress = device.deviceAddress;
            //connect()

        }
        else{
            Log.e(TAG,"No device found when clicked");
        }

    }

   /* @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {

        // InetAddress from WifiP2pInfo struct.
        InetAddress groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

        // After the group negotiation, we can determine the group owner
        // (server).
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
        } else if (info.groupFormed) {
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
        }
    }*/



}




