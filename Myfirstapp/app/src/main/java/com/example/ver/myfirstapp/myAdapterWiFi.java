
package com.example.ver.myfirstapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class myAdapterWiFi extends RecyclerView.Adapter<myAdapterWiFi.ViewHolder>  {
    private List<WifiP2pDevice> peers;
    //private List<String> peersAdd;
    private WiFiDirect WiFiDManager;

    //private WifiP2pDevice device=null;

    //private Collection<WifiP2pDevice> p2plistcollection;
    private static final String TAG = "WiFiListAdapter";


    // private final int

    Context test;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder



    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtFooter;
        public View txticon;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtFooter = (TextView) v.findViewById(R.id.secondLine);
            txticon =  v.findViewById(R.id.icon);

            //txt =  v.findViewById(R.id.discDevice);

        }
    }

 /*   public void putWifiManager(WiFiDirect adapter){
        //WiFiDManager=mManager;
        WiFiDManager = adapter;
        Log.e(TAG,"Adapter acquired");
    }*/

   // @Override
    public void addAll(Collection<WifiP2pDevice> data){           //WifiP2pDeviceList data){
      //  ((MainActivity) test).dispToast("adding Data");
        Log.e(TAG,"Adding Data ");

        // WifiP2pDeviceList data=WiFiDManager.getP2PCollection();
        int position=0;
        if(data.size()==0){
            return;

        }
        //p2plistcollection=data;

        peers.clear();
       // for(WifiP2pDeviceList i:data)
            for (WifiP2pDevice device : data)   //data.getDeviceList())
            {
                peers.add(device);
                notifyItemInserted(position);
                ++position;
                // device.deviceName
            //    Log.e(TAG,"Device Added "+device);

               // ((MainActivity) test).dispToast("Device Added "+position);

            }



    }


    public void remove(int position) {
        peers.clear();
       // notifyItemRemoved(0);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    myAdapterWiFi(List<WifiP2pDevice> mydataset,WiFiDirect mManager )  {
        peers=mydataset;
        WiFiDManager=mManager;
       // addAll(myDataset);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public myAdapterWiFi.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        test = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v =inflater.inflate(R.layout.recyclerview_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    @TargetApi(18)

    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this posit ion
        // - replace the contents of the view with that element
        //Log.e(TAG, "View holder ");
       //((MainActivity) test).dispToast("View holder");
        if(peers.size()==0 ){
            return;

        }
        int pos=0;
 /*       for (WifiP2pDevice d : p2plistcollection)//data.getDeviceList())
         {                if(pos==position) {
                                device = d;
                                break;
                             }
                             else if(pos > position)
                                    break;
                            else
                                ++pos;
                                // device.deviceName
                                Log.e(TAG,"Device Added "+position);
        }*/

      //  Log.e(TAG, "got data ");//+npeers);

        final String name = peers.get(position).deviceName;//(String) device.deviceName;
        final String MacAdd = peers.get(position).deviceAddress;//(String) device.deviceAddress;

        holder.txtHeader.setText(name);
        holder.txtFooter.setText(MacAdd);

        holder.txtHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"Connect_android going to be called [header]");
                 ((MainActivity) test).dispToast("clicked header of "+peers.get(position).deviceName);
                //Log.e(TAG,"what we are sending "+);

                WiFiDManager.connect(peers.get(position));
             //   public void onListItemClick(ListView l, View v, int position, long id) {
                   // WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
                   // ((DeviceActionListener) getActivity()).showDetails(device);
               // }

                //Intent intent = new Intent(test, bluConnectChat.class);
                //intent.putExtra("bluetoothDeviceAddress",BlueDevice.get(position).getBluDeviceMAC());
               // test.startActivity(intent);

                //remove(position);
            }
        });
        holder.txtFooter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"Connect_android going to be called [footer]");
                ((MainActivity) test).dispToast("clicked header of "+peers.get(position).deviceName);

                //((MainActivity) test).dispToast("clicked footer of "+name);
                WiFiDManager.connect(peers.get(position));

                /*Intent intent = new Intent(test, bluConnectChat.class);
                intent.putExtra("bluetoothDeviceAddress",BlueDevice.get(position).getBluDeviceMAC());
                test.startActivity(intent);*/


                //remove(position);
            }
        });
        holder.txticon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"Connect_android going to be called [icon]");
                ((MainActivity) test).dispToast("clicked header of "+peers.get(position).deviceName);

                // ((MainActivity) test).dispToast("clicked icon of "+name);
                WiFiDManager.connect(peers.get(position));

               /* Intent intent = new Intent(test, bluConnectChat.class);
                intent.putExtra("bluetoothDeviceAddress",BlueDevice.get(position).getBluDeviceMAC());
                test.startActivity(intent);*/

                //remove(position);
            }
        });

    }



        @Override
    public int getItemCount() {        return peers.size(); }

}
