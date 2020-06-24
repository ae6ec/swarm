
package com.example.ver.myfirstapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>  {
    private List<device_class> BlueDevice;
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

    public void add(int position, String deviceName , String deviceMac) {
        BlueDevice.add(new device_class(deviceName , deviceMac));
        notifyItemInserted(position);//notifyItemInserted(position);
    }

    public void remove(int position) {
        BlueDevice.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<device_class> myDataset) {
        BlueDevice = new ArrayList<device_class>();
        BlueDevice  = (List<device_class>) myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        test = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v =
                inflater.inflate(R.layout.recyclerview_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this posit ion
        // - replace the contents of the view with that element
        final String name = (String) BlueDevice.get(position).getBluDeviceName();
        final String MacAdd = (String) BlueDevice.get(position).getBluDeviceMAC();

        holder.txtHeader.setText(name);
        holder.txtHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(test, bluConnectChat.class);
                intent.putExtra("bluetoothDeviceAddress",BlueDevice.get(position).getBluDeviceMAC());
                test.startActivity(intent);

                //remove(position);
            }
        });
        holder.txtFooter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(test, bluConnectChat.class);
                intent.putExtra("bluetoothDeviceAddress",BlueDevice.get(position).getBluDeviceMAC());
                test.startActivity(intent);

                //remove(position);
            }
        });
        holder.txticon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(test, bluConnectChat.class);
                intent.putExtra("bluetoothDeviceAddress",BlueDevice.get(position).getBluDeviceMAC());
                test.startActivity(intent);

                //remove(position);
            }
        });

        holder.txtFooter.setText(MacAdd);
    }
    @Override
    public int getItemCount() {        return BlueDevice.size();
    }

}
