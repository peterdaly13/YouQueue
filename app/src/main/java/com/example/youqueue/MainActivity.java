package com.example.youqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.ListItems;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.protocol.types.Repeat;


import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "d19dfd48fcd54626a0f8ff696ada3b9e";
    private static final String REDIRECT_URI = "com.youqueue://callback";
    private SpotifyAppRemote mSpotifyAppRemote = null;
    // Access a Cloud Firestore instance from your Activity
    private DatabaseReference mDatabase;
    HashMap map;
    SongQueue sq = new SongQueue();


    public void goToSettings(View view) {
        Log.i("Info", "Settings Button pressed");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void goToJoinParty(View view) throws InterruptedException {
        Log.i("Info1", "before Pull Data");
        pullData(111333 , "updateVotes", "QWERTY", null);

        Log.i("Info2", "Join Party Button pressed");
        Intent intent = new Intent(this, JoinPartyActivity.class);
        startActivity(intent);
    }

    public void goToStartParty(View view) {
        try {
            Song s = new Song("1dfsccdf",100, 12, "TestSong");
            Song s2 = new Song("jsdhfiweur",1000, 120, "TestSong2");
            SongQueue songQueue= new SongQueue(111);
            songQueue.addSong(s);
            songQueue.addSong(s2);
            pushData(songQueue);
        }catch (Exception e){
            Log.w("Potato", "Error adding document", e);
        }

        Log.i("Info", "Start Party Button pressed");
        Intent intent = new Intent(this, StartPartyActivity.class);
        startActivity(intent);

    }

    public void goToURL(View view) {
        //Dialog opens if user is already logged in, else opens spotify login webpage
        //TODO: Fix checking connector to see if user is already logged in
        if (mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected()) {
            openDialog();
        } else {
            Log.i("Info", "Spotify Button pressed");
            String url = "https://accounts.spotify.com/en/login/";
            Uri uriURL = Uri.parse(url);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriURL);
            startActivity(launchBrowser);

            //Set listener to tell when user has logged in and redirect back to MainActivity
        }
    }

    public void openDialog() {
        AlreadyLoggedInDialog dialog = new AlreadyLoggedInDialog();
        dialog.show(getSupportFragmentManager(), "logged in dialog");
    }

    public SpotifyAppRemote getmSpotifyAppRemote() {
        return this.mSpotifyAppRemote;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Set the connection parameters
        /*
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
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
            */
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() {
        //queueSong("spotify:track:5nrmGFJ87crVoJF5xdRqwn");
        //resumePlayback();
        //pausePlayback();
        //playSong("spotify:track:1Eolhana7nKHYpcYpdVcT5");
        //search("Jude");
        /*
        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });

         */
    }

    private void playSong(String uri){
        mSpotifyAppRemote.getPlayerApi().play(uri);
    }

    private void queueSong(String uri){
        mSpotifyAppRemote.getPlayerApi().queue(uri);
    }

    private void pausePlayback(){
        mSpotifyAppRemote.getPlayerApi().pause();
    }

    private void resumePlayback(){ mSpotifyAppRemote.getPlayerApi().resume(); }

    private void search(String track){ }

    //Need to write code to push a songQueue to Firebase
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
    private void pullData(int partyLeaderID, final String action, final String uri, final Song song) throws InterruptedException {

        Log.i("In Pull Data","asdfad");
        DatabaseReference qReference = mDatabase.child("queues").child(Integer.toString(partyLeaderID));
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                SongQueue st = dataSnapshot.getValue(SongQueue.class);

                Log.i("onDataChange", action + "    " + st.toString());
                if (action.equals("displayQueue")) {
                    displayQueue(st);
                } else if (action.equals("updateVotes")){
                    updateVotes(st,uri);
                } else if (action.equals("addASong")) {
                    addASong(st, song);
                } else if (action.equals("playNextSong")) {
                    playNextSong(st);
                }

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
    private void playNextSong(SongQueue songQueue) {
        Song s = songQueue.nextSong();
        playSong(s.getURI());
        songQueue.removeSong(s.getURI());
        pushData(songQueue);
    }

    /*
    This adds a song to the queue and then
    returns the queue to firebase
     */
    private void addASong(SongQueue songQueue, Song song) {
        songQueue.addSong(song);
        pushData(songQueue);
    }

    /*
    This updates the specific song with one more vote and
    pushes the resulting queue back to firebase
     */
    private void updateVotes(SongQueue songQueue, String uri) {
        Log.i("updateVotes", songQueue.toString());
        songQueue.getSong(uri).incrementVotes();

        pushData(songQueue);
        Log.i("updateVotes2", songQueue.toString());
    }

    /*
    This displays the queue... Need some help from front end folks
     */
    private void displayQueue(SongQueue st) {

    }

    private void updateQueue(SongQueue s) {
        sq=s;
        Log.i("Info2", s.toString());
    }

}
