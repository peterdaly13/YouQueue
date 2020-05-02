package com.example.youqueue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.HashMap;
import java.util.List;

public class JoinedParty extends AppCompatActivity {

    public DatabaseReference mDatabase;
    SongQueue sq = new SongQueue();
    private SpotifyAppRemote mSpotifyAppRemote = null;
    public String yourPartyID;
    LinearLayout mLinLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_party);

        mLinLay = (LinearLayout) this.findViewById(R.id.linlay);
    }

    public void goHome(View view) {
        Log.i("Info", "Back Button pressed");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void queueSong(Song s){
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
                if (actionRef[0].equals("displayQueue")) {
                    displayQueue(st);
                } else if (actionRef[0].equals("updateVotes")){
                    updateVotes(st,uri);
                } else if (actionRef[0].equals("addASong")) {
                    addASong(st, song);
                } else if (actionRef[0].equals("playNextSong")) {
                    playNextSong(st);
                }else if (actionRef[0].equals("endParty")) {
                    endParty(st);
                }
                actionRef[0] ="";
                Log.i("InPullData","asdfaddd");
                updateQueue(st);
                //Log.d("PullData", sq.toString());

                // ...
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
        playSong(s.getURI());
        songQueue.removeSong(s.getURI());
        pushData(songQueue);
    }
    /*
    This adds a song to the queue and then
    returns the queue to firebase
     */
    private void addASong (SongQueue songQueue, Song song){
        songQueue.addSong(song);
        pushData(songQueue);
    }
    /*
    This updates the specific song with one more vote and
    pushes the resulting queue back to firebase
     */
    private void updateVotes (SongQueue songQueue, String uri){
        Log.i("updateVotes", songQueue.toString());
        songQueue.getSong(uri).incrementVotes();

        pushData(songQueue);
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
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < size; i++) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(lparams);
            Song mSong = st.getSongAtIndex(i);
            tv.setText(mSong.getName());
            this.mLinLay.addView(tv);
        }
    }

    private void updateQueue (SongQueue s){
        sq = s;
        Log.i("Info3", s.toString());
    }

    private void playSong(String uri){
        mSpotifyAppRemote.getPlayerApi().play(uri);
    }


}
