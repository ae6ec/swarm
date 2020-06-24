package com.example.ver.myfirstapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.ViewHolder> {
    private List<String> chatData;

    private Context cacontext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtchat;
        public TextView txttimestamp;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtchat = (TextView) v.findViewById(R.id.chatline);
            txttimestamp = (TextView) v.findViewById(R.id.timestamp);
        }
    }

    public void add(int position, String msg ) {
        chatData.add(msg);
        notifyItemInserted(position);//notifyItemInserted(position);
    }

    public void remove(int position) {
        chatData.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public chatAdapter(List<String> myDataset) {
        chatData = new ArrayList<String>();
        chatData  = (List<String>) myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public chatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        cacontext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(cacontext);
        View v =
                inflater.inflate(R.layout.chatrecyclerviewlayout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this posit ion
        // - replace the contents of the view with that element
       // final String chatmatter = (String) chatData.get(position);

        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        holder.txttimestamp.setText(mydate);
        holder.txtchat.setText(chatData.get(position));


        holder.txtchat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               // remove (position);
                Toast.makeText(cacontext, "Not Deleting ", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public int getItemCount() {        return chatData.size();
    }

}
