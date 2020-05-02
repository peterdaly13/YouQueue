package com.example.youqueue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class JoinedParty extends AppCompatActivity {

    public DatabaseReference mDatabase;
    private SpotifyAppRemote mSpotifyAppRemote = null;
    public String yourPartyID;

    private RecyclerView dqRecycleView;
    private displayQueueAdapter dqAdapter;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    Song[] songList;
    String songNames[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_party);

        mDatabase = FirebaseDatabase.getInstance().getReference();



        yourPartyID= JoinPartyActivity.getYourPartyId();
        TextView xmlPartyID = (TextView) findViewById(R.id.xmlPartyID);
        xmlPartyID.setText(yourPartyID);

        pullData(Integer.parseInt(yourPartyID), "displayQueue", null,null);

        SongList sl = new SongList();
        songList = sl.getSongs();
        songNames = sl.getSongNames();
        ArrayList<String> songNameList = new ArrayList<>(Arrays.asList(songNames));
        recyclerView = (RecyclerView) findViewById(R.id.songList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new songListAdapterStartParty(this, songNameList);
        //mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);




    }

    public void goHome(View view) {
        Log.i("Info", "Back Button pressed");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void queueSong(Song s){
        yourPartyID= JoinPartyActivity.getYourPartyId();
        Log.i("QueueSong", yourPartyID);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        System.out.println(mDatabase);
        pullData(Integer.parseInt(yourPartyID),"addASong", null, s);
    }
    public void updateVotes(Song s){
        pullData(Integer.parseInt(yourPartyID),"updateVotes", s.getURI(), null);
    }








    //Pasted in PullData methods

    private void pushData(SongQueue s){
        Log.d("PushData", "1234");
        HashMap<String, Object> map = new HashMap<>();
        String folder = "/queues/"+ Integer.toString(s.partyLeaderID);
        map.put(folder, s);
        mDatabase.updateChildren(map)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Potato", "Error adding document", e);
                    }
                });
    }
    private void pullData(int partyLeaderID, final String action, final String uri, final Song song) {
        final String[] actionRef = {action};
        Log.i("InPullData","asdfad");
        DatabaseReference qReference = mDatabase.child("queues").child(Integer.toString(partyLeaderID));
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                SongQueue st = dataSnapshot.getValue(SongQueue.class);
                //Log.i("onDataChange", action + "    " + st.toString());
                if (st != null) {
                    if (actionRef[0].equals("displayQueue")) {
                        displayQueue(st);
                    } else if (actionRef[0].equals("updateVotes")) {
                        updateVotes(st, uri);
                    } else if (actionRef[0].equals("addASong")) {
                        addASong(st, song);
                    } else if (actionRef[0].equals("playNextSong")) {
                        playNextSong(st);
                    } else if (actionRef[0].equals("endParty")) {
                        endParty(st);
                    }
                    actionRef[0] = "";
                    Log.i("InPullData", "asdfaddd");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("PullData", "Failed to Load SongQueue from Firebase", databaseError.toException());
                // ...
            }
        };
        qReference.addValueEventListener(postListener);
    }
    /*
    This grabs the top voted song from the queue, plays it,
    deletes the song from the queue and pushes the queue
    back to firebase
     */
    private void playNextSong (SongQueue songQueue){
        Song s = songQueue.nextSong();
        if (s != null) {
            playSong(s.getURI());
            songQueue.removeSong(s.getURI());
            pushData(songQueue);
        }
    }
    /*
    This adds a song to the queue and then
    returns the queue to firebase
     */
    private void addASong (SongQueue songQueue, Song song){
        if (song != null) {
            songQueue.addSong(song);
            pushData(songQueue);
        }
    }
    /*
    This updates the specific song with one more vote and
    pushes the resulting queue back to firebase
     */
    private void updateVotes (SongQueue songQueue, String uri){
        Log.i("updateVotes", songQueue.toString());
        if (songQueue.getSong(uri) != null) {
            songQueue.getSong(uri).incrementVotes();
            pushData(songQueue);
        }
        Log.i("updateVotes2", songQueue.toString());
    }

    private void endParty(SongQueue st) {
        if(st!=null) {
            mDatabase.child("/queues/" + st.getPartyLeaderID()).removeValue();
        }
    }

    /*
    This displays the queue... Need some help from front end folks
     */
    private void displayQueue (SongQueue st){
        st.sortSongs();
        int size = st.getQueueSize();

        ArrayList<String> songsInQ = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Song mSong = st.getSongAtIndex(i);
            String name = mSong.getName();
            songsInQ.add(name);
        }
        dqRecycleView = (RecyclerView) findViewById(R.id.queueList);
        dqRecycleView.setLayoutManager(new LinearLayoutManager(this));
        dqAdapter = new displayQueueAdapter(this, songsInQ);
        recyclerView.setAdapter(dqAdapter);


    }

    private void playSong(String uri){
        mSpotifyAppRemote.getPlayerApi().play(uri);
    }


}
