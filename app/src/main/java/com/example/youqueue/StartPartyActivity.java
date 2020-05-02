package com.example.youqueue;

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
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class StartPartyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    Song[] songList;
    String songNames[];

    private static final String CLIENT_ID = "d19dfd48fcd54626a0f8ff696ada3b9e";
    private static final String REDIRECT_URI = "com.youqueue://callback";
    private SpotifyAppRemote mSpotifyAppRemote = null;
    int songLengthCounter =0;
    String currentURI= "";

    public String yourPartyID;
    public DatabaseReference mDatabase;
    SongQueue sq = new SongQueue();
    PartyLocation currentLocation;
    //MainActivity ma = new MainActivity();

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

        /*
        SongList sl = new SongList();
        songList = sl.getSongs();
        songNames = sl.getSongNames();
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new songListAdapter(songNames);
        recyclerView.setAdapter(mAdapter);
        
         */

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
                        Log.d("MainActivity", "Connected! Yay!");
                        //connected();
                    }
                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }


    //ACTIONS OF BUTTONS

    public void goHome(View view) {
        Log.i("Info", "Back Button pressed");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void resume(View view) throws InterruptedException {
        //ma.resumePlayback();
        startCounting();
    }

    public void pause(View view) {
        stopCounting();
        //ma.pausePlayback();
    }
    //Saving the URI of the previous song doesn't seem feasible, instead re-starts current song
    public void prevSong(View view) throws InterruptedException {
        playSong(currentURI);
    }
    public void nextSong(View view) {
        pullData(Integer.parseInt(yourPartyID), "playNextSong", null, null);
    }
    public void queueSong(Song s){
        pullData(Integer.parseInt(yourPartyID),"addASong", null, s);
    }
    public void updateVotes(Song s){
        pullData(Integer.parseInt(yourPartyID),"addASong", s.getURI(), null);
    }
    public void endParty(){
        pullData(Integer.parseInt(yourPartyID),"endParty",null, null);
        deleteLocation();
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
                if (actionRef[0].equals("displayQueue")) {
                    displayQueue(st);
                } else if (actionRef[0].equals("updateVotes")){
                    updateVotes(st,uri);
                } else if (actionRef[0].equals("addASong")) {
                    addASong(st, song);
                } else if (actionRef[0].equals("playNextSong")) {
                    try {
                        playNextSong(st);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
                if (actionRef[0].equals("addLocation")) {
                    addLocation(partyLocations, userLocation);
                } else if (actionRef[0].equals("compareLocations")) {
                    compareLocations(partyLocations, userLocation);
                } else if (actionRef[0].equals("deleteLocation")) {
                    deleteLocation(partyLocations, partyId);
                }
                actionRef[0] ="";
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
        List<PartyLocation> partyLocations = locationList.getPl();
        partyLocations.add(userLocation);
        locationList.setPl(partyLocations);
        pushLocation(locationList);
    }

    /*
    This grabs the top voted song from the queue, plays it,
    deletes the song from the queue and pushes the queue
    back to firebase
     */
    private void playNextSong (SongQueue songQueue) throws InterruptedException {
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

    }

    private void updateQueue (SongQueue s){
        sq = s;
        Log.i("Info3", s.toString());
    }


    //SPOTIFY METHODS (MIGHT NEED MORE)
    private void playSong(String uri) throws InterruptedException {
        currentURI=uri;
        mSpotifyAppRemote.getPlayerApi().play(uri);
        songLengthCounter=0;
        startCounting();

    }
}

