package com.example.youqueue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class displayQueueAdapter extends RecyclerView.Adapter<displayQueueAdapter.ViewHolder>{

    private ArrayList<String> mDataset;
    private LayoutInflater mInflater1;
    private displayQueueAdapter.ItemClickListener mClickListener;

    //Constructor
    public displayQueueAdapter(Context context, ArrayList<String> myDataset) {
        this.mInflater1 = LayoutInflater.from(context);
        this.mDataset = myDataset;
    }

    // inflates the row layout from xml when needed
    @Override
    public displayQueueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater1.inflate(R.layout.dqlistsongs, parent, false);
        return new displayQueueAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(displayQueueAdapter.ViewHolder holder, int position) {
        String song = mDataset.get(position);
        holder.myTextView.setText(song);
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
            myTextView = itemView.findViewById(R.id.dqSongName);
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
    void setClickListener(displayQueueAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
