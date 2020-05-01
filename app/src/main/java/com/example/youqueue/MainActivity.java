package com.example.youqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.LatLng;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HttpsURLConnection;
import android.os.AsyncTask;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "d19dfd48fcd54626a0f8ff696ada3b9e";
    private static final String REDIRECT_URI = "com.youqueue://callback";
    private SpotifyAppRemote mSpotifyAppRemote = null;
    // Access a Cloud Firestore instance from your Activity
    private DatabaseReference mDatabase;
    public static String yourUserID;
    HashMap map;
    SongQueue sq = new SongQueue();
    String url_auth =
            "https://accounts.spotify.com/authorize?"
                    + "client_id="+CLIENT_ID+"&"
                    + "response_type=code&"
                    + "redirect_uri="+REDIRECT_URI+"&"
                    + "scope=user-read-private%20user-read-email&";

    public void goToSettings(View view) {
        Log.i("Info", "Settings Button pressed");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void goToJoinParty(View view) throws InterruptedException {
        Log.i("Info1", "before Pull Data");
        //Song s = new Song("Taco3", 11123, 12345, "Peter's Song Please work again");
        //pullData(111 , "addASong", null, s);
        /*
        LatLong latlng = new LatLong(0.5, 8.1);
        LatLong latlng2 = new LatLong(0.0, 0.0);
        PartyLocation location = new PartyLocation(latlng, 123, "Ethan");
        PartyLocation location2 = new PartyLocation(latlng2, 56, "Peter");
        List<PartyLocation> list = new ArrayList<PartyLocation>();
        list.add(location);
        list.add(location2);
        LocationList loclist = new LocationList(list);
        pushLocation(loclist);
        */

        LatLong latlng = new LatLong(1.5, 8.7);
        PartyLocation location = new PartyLocation(latlng, 123, "Ethan");


        pullLocation("addLocation",0 ,location );

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
    // Generate the 6 Digit User ID
    public static String generateUserID() {
        // Generate random number from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        // Convert any number sequence into 6 digits (Example: 0 becomes 000000)
        return String.format("%06d", number);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Pop-up which prompts for username
        // Saves the username in a preference field
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String userName = prefs.getString("user_name", null);
        // Check if the preference field is set. If not, prompt the user to input their username
        if (userName == null || yourUserID == null) {
            // Generate userID using the random number generating function above
            yourUserID = generateUserID();
            EditText input = new EditText(this);
            input.setId(Integer.parseInt(yourUserID));
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(input).setTitle("Enter your username!")
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    EditText theInput = (EditText) ((AlertDialog) dialog)
                                            .findViewById(Integer.parseInt(yourUserID));
                                    String enteredText = theInput.getText()
                                            .toString();
                                    if (!enteredText.equals("")) {
                                        SharedPreferences.Editor editor = prefs
                                                .edit();
                                        editor.putString("user_name",
                                                enteredText);
                                        editor.commit();
                                    }
                                }
                            }).create();
            dialog.show();
        }
        // Text to check if the username is being set correctly (Can also be kept and styled if we want to display their username)
        TextView xmlUserNameCheck = (TextView) findViewById(R.id.userNameCheck);
        //xmlUserNameCheck.setText("HELLO, " + userName.toUpperCase());
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

    protected void pausePlayback(){
        mSpotifyAppRemote.getPlayerApi().pause();
    }

    protected void resumePlayback(){ mSpotifyAppRemote.getPlayerApi().resume(); }

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
        final String[] actionRef = {action};
        Log.i("InPullData","asdfad");
        DatabaseReference qReference = mDatabase.child("queues").child(Integer.toString(partyLeaderID));
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                SongQueue st = dataSnapshot.getValue(SongQueue.class);
                Log.i("onDataChange", action + "    " + st.toString());
                if (actionRef[0].equals("displayQueue")) {
                    displayQueue(st);
                } else if (actionRef[0].equals("updateVotes")){
                    updateVotes(st,uri);
                } else if (actionRef[0].equals("addASong")) {
                    addASong(st, song);
                } else if (actionRef[0].equals("playNextSong")) {
                    playNextSong(st);
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

    /*
    This displays the queue... Need some help from front end folks
     */
        private void displayQueue (SongQueue st){

        }

        private void updateQueue (SongQueue s){
            sq = s;
            Log.i("Info3", s.toString());
        }

    /*
    Function to calculate the distance between two lat/long location coordinates in kilometers
    (Used when guest wants to join party by location, will find locations within given range)
     */
        private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            return (dist);
        }

        private double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
        }

        private double rad2deg(double rad) {
            return (rad * 180.0 / Math.PI);
        }

    }