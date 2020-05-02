package com.example.youqueue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class songListAdapterStartParty extends RecyclerView.Adapter<songListAdapterStartParty.ViewHolder>{
    private List<String> mDataset;
    private LayoutInflater mInflater;
    private songListAdapterStartParty.ItemClickListener mClickListener;
    SongList sl = new SongList();
    Song[] songObjects = sl.getSongs();
    StartPartyActivity sp = new StartPartyActivity();


    // data is passed into the constructor
    songListAdapterStartParty(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mDataset = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public songListAdapterStartParty.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.listsongs, parent, false);
        return new songListAdapterStartParty.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(songListAdapterStartParty.ViewHolder holder, int position) {
        String song = mDataset.get(position);
        holder.myTextView.setText(song);
        holder.myTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("Pressed " + songObjects[position].getName());
                sp.queueSong(songObjects[position]);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.songName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mDataset.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(songListAdapterStartParty.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
