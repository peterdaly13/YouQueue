package com.example.youqueue;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class StartPartyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private songListAdapterStartParty mAdapter;
    private RecyclerView dqRecycleView;
    private displayQueueAdapter dqAdapter;

    Song[] songList;
    String songNames[];

    private static final String CLIENT_ID = "d19dfd48fcd54626a0f8ff696ada3b9e";
    private static final String REDIRECT_URI = "com.youqueue://callback";
    public static SpotifyAppRemote mSpotifyAppRemote = null;
    int songLengthCounter =0;
    Song currentSong;
    ArrayList<Song> songsYouVotedFor= new ArrayList<Song>();
    public String yourPartyID;
    public DatabaseReference mDatabase;
    PartyLocation currentLocation;


    // Generate the 6 Digit Party ID
    public static String generatePartyID() {
//        // Generate random number from 0 to 999999
//        Random rnd = new Random();
//        int number = rnd.nextInt(999999);
//
//        // Convert any number sequence into 6 digits (Example: 0 becomes 000000)
//        return String.format("%06d", number);
        return MainActivity.yourUserID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_party);


        // Generate the party ID using the random number generating function above
        yourPartyID = generatePartyID();
        // Set the party ID to display on the activity
        TextView xmlPartyID = (TextView) findViewById(R.id.xmlPartyID);
        xmlPartyID.setText(yourPartyID);
        //Push empty queue to firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        SongQueue sq= new SongQueue(Integer.parseInt(yourPartyID));
        pushData(sq);
        //pullData(Integer.parseInt(yourPartyID), "displayQueue", null,null);


        PartyLocation myLocation = MainActivity.userLocationGlobal;
        myLocation.setPartyId(Integer.parseInt(yourPartyID));
        myLocation.setUsername(MainActivity.userName);
        //pullLocation("addLocation", Integer.parseInt(yourPartyID), myLocation);

        SongList sl = new SongList();
        songList = sl.getSongs();
        songNames = sl.getSongNames();
        ArrayList<String> songNameList = new ArrayList<>(Arrays.asList(songNames));
        recyclerView = (RecyclerView) findViewById(R.id.songList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new songListAdapterStartParty(this, songNameList);
        //mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);

        mSpotifyAppRemote = MainActivity.mSpotifyAppRemote;

        //login to spotify
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("StartPartyActivity", "Connected! Yay!");
                        //connected();
                    }
                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("StartPartyActivity", throwable.getMessage(), throwable);
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });



    }



    //@Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + mAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }


    //ACTIONS OF BUTTONS

    public void goHome(View view) {
        Log.i("Info", "Back Button pressed");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        Animatoo.animateSlideLeft(this);
    }

    protected void pausePlayback(){
        mSpotifyAppRemote.getPlayerApi().pause();
    }

    protected void resumePlayback(){ mSpotifyAppRemote.getPlayerApi().resume(); }

    public void resume(View view) throws InterruptedException {
        resumePlayback();
        //startCounting();
    }

    public void pause(View view) {
        pausePlayback();
        stopCounting();
        //ma.pausePlayback();
    }
    //Saving the URI of the previous song doesn't seem feasible, instead re-starts current song
    public void prevSong(View view) throws InterruptedException {
        playSong(currentSong.getURI());
    }
    public void nextSong(View view) {
        pullData(Integer.parseInt(yourPartyID), "playNextSong", null, null);
    }
    public void queueSong(Song s){
        yourPartyID= MainActivity.yourUserID;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        pullData(Integer.parseInt(yourPartyID),"addASong", null, s);
    }
    public void addAVote(Song s){
        yourPartyID= MainActivity.yourUserID;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        boolean addVote=true;
        for(int i =0; i< songsYouVotedFor.size(); i++){
            if(songsYouVotedFor.get(i).getURI().equals(s.getURI())){
                addVote=false;
            }
        }
        if(addVote) {
            pullData(Integer.parseInt(yourPartyID), "updateVotes", s.getURI(), null);
            songsYouVotedFor.add(s);
            Log.i("Add vote",songsYouVotedFor.toString());
        }
    }
    public void endParty(View v){
        pullData(Integer.parseInt(yourPartyID),"endParty",null, null);
        deleteLocation();
        songsYouVotedFor.clear();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void addLocation(){
        pullLocation("addLocation",Integer.parseInt(yourPartyID),currentLocation );
    }
    public void deleteLocation(){
        pullLocation("deleteLocation", Integer.parseInt(yourPartyID), currentLocation);
    }

    //Counts how long a song has been playing for
    boolean counting =false;
    public void startCounting() throws InterruptedException {
        counting = true;
        while(counting){
            Thread.sleep(1000);
            songLengthCounter++;
        }
    }
    public void stopCounting(){
        counting =false;
    }





    //COPIED FIREBASE METHODS FOR USE IN THIS ACTIVITY
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
    //Need to write code to pull a songQueue from Firebase
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
                        try {
                            addASong(st, song);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (actionRef[0].equals("playNextSong")) {
                        try {
                            playNextSong(st);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (actionRef[0].equals("endParty")) {
                        endParty(st);
                    }
                    actionRef[0] = "";
                }
                Log.i("InPullData","asdfaddd");
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


    private void pushLocation(LocationList ll) {
        HashMap<String, Object> map = new HashMap<>();
        String folder = "/locations";
        map.put(folder, ll);
        mDatabase.updateChildren(map)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Potato", "Error adding document", e);
                    }
                });
    }
    private void pullLocation(final String action, final int partyId, final PartyLocation userLocation){
        final String[] actionRef = {action};
        DatabaseReference qReference = mDatabase.child("locations");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.i("datasnapshot", dataSnapshot.getValue().toString());
                LocationList partyLocations = dataSnapshot.getValue(LocationList.class);
                if (partyLocations != null) {
                    if (actionRef[0].equals("addLocation")) {
                        addLocation(partyLocations, userLocation);
                    } else if (actionRef[0].equals("compareLocations")) {
                        compareLocations(partyLocations, userLocation);
                    } else if (actionRef[0].equals("deleteLocation")) {
                        deleteLocation(partyLocations, partyId);
                    }
                    actionRef[0] = "";
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

    private void deleteLocation(LocationList locationList, int partyId) {
        List<PartyLocation> partyLocations = locationList.getPl();
        for (int i = partyLocations.size()-1; i>=0; i--){
            if (partyLocations.get(i).getPartyId() == partyId){
                partyLocations.remove(partyLocations.get(i));
            }
        }
        locationList.setPl(partyLocations);
        pushLocation(locationList);
    }
    /*
        Bilal could you fill this out?
     */
    private void compareLocations(LocationList locationList, PartyLocation userLocation) {

    }

    /*
    This adds a location to the list of locations
     */
    private void addLocation(LocationList locationList, PartyLocation userLocation) {
        if (userLocation != null) {
            List<PartyLocation> partyLocations = locationList.getPl();
            partyLocations.add(userLocation);
            locationList.setPl(partyLocations);
            pushLocation(locationList);
        }
    }

    /*
    This grabs the top voted song from the queue, plays it,
    deletes the song from the queue and pushes the queue
    back to firebase
     */
    private void playNextSong (SongQueue songQueue) throws InterruptedException {
        Song s = songQueue.nextSong();
        if (s != null) {
            currentSong = s;
            mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
                mSpotifyAppRemote.getPlayerApi().play(s.getURI());
            })
                    .setErrorCallback(throwable -> {
                        Log.i("Error in playSong", mSpotifyAppRemote.toString());
                    });

            mSpotifyAppRemote.getPlayerApi().play(s.getURI());
            //playSong(s.getURI());
            //mSpotifyAppRemote.getPlayerApi().play(s.getURI());
            //songQueue.removeSong(s.getURI());
            //pushData(songQueue);
        }
    }

    /*
    This adds a song to the queue and then
    returns the queue to firebase
     */
    private void addASong (SongQueue songQueue, Song song) throws InterruptedException {
        if (song != null) {
            songQueue.addSong(song);
            pushData(songQueue);
        }
        //pullData(Integer.parseInt(yourPartyID), "displayQueue", null,null);


        if (songQueue.getQueueSize() == 2){
            Log.i("Song queue size", Integer.toString(songQueue.getQueueSize()));
            playNextSong(songQueue);
            //Log.i("Call Result: ", result.toString());
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
        }
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
    public void displayQueue (SongQueue st){
        st.sortSongs();
        int size = st.getQueueSize();

        ArrayList<String> songsInQ = new ArrayList<>();
        Song[] songsArray = new Song[50];

        for (int i = 0; i < size; i++) {
            Song mSong = st.getSongAtIndex(i);
            String name = mSong.getName();
            songsInQ.add(name);
            songsArray[i] = mSong;
        }
        dqRecycleView = (RecyclerView) findViewById(R.id.queueList);
        dqRecycleView.setLayoutManager(new LinearLayoutManager(this));
        dqAdapter = new displayQueueAdapter(this, songsInQ, songsArray);
        dqRecycleView.setAdapter(dqAdapter);
    }


    //SPOTIFY METHODS (MIGHT NEED MORE)
    private void playSong(String uri) throws InterruptedException {
        Log.i("URI: ", uri);
        mSpotifyAppRemote = MainActivity.mSpotifyAppRemote;
        mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
            mSpotifyAppRemote.getPlayerApi().play(uri);
        })
        .setErrorCallback(throwable -> {
            Log.i("Error in playSong", mSpotifyAppRemote.toString());
        });

                mSpotifyAppRemote.getPlayerApi().play(uri);
        songLengthCounter=0;
        //startCounting();

    }
}

