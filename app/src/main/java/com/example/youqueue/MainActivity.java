package com.example.youqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.TextView;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;


import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "d19dfd48fcd54626a0f8ff696ada3b9e";
    private static final String REDIRECT_URI = "com.youqueue://callback";
    private SpotifyAppRemote mSpotifyAppRemote = null;
    // Access a Cloud Firestore instance from your Activity
    public DatabaseReference mDatabase;
    public static String userName = "";
    public static String yourUserID = "000000";
    HashMap map;
    SongQueue sq = new SongQueue();
    String url_auth =
            "https://accounts.spotify.com/authorize?"
                    + "client_id="+CLIENT_ID+"&"
                    + "response_type=code&"
                    + "redirect_uri="+REDIRECT_URI+"&"
                    + "scope=user-read-private%20user-read-email&";

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;

    // Initial LatLong variable to store (0,0)
    private static LatLong initial = new LatLong(0,0);
    // Variable which stores the user's current location - Before permissions are granted, the initial location is set to (0,0)
    private static PartyLocation userLocationGlobal = new PartyLocation(initial, Integer.parseInt(yourUserID), userName);
    // List to store parties nearby to the current location
    private static List<PartyLocation> partiesNearby = new ArrayList<PartyLocation>();

    public void goToSettings(View view) {
        Log.i("Info", "Settings Button pressed");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

        Animatoo.animateSlideRight(this);
    }

    public void goToJoinParty(View view) throws InterruptedException {
        //LatLong latLong= new LatLong(0.01,-0.01);
        //PartyLocation partyLocation = new PartyLocation(latLong,12345,"Test Location" );
        //pullLocation("addLocation", 111, partyLocation);
        Log.i("Info2", "Join Party Button pressed");
        Intent intent = new Intent(this, JoinPartyActivity.class);
        startActivity(intent);

        Animatoo.animateSlideLeft(this);
    }

    public void goToStartParty(View view) {

        Log.i("Info", "Start Party Button pressed");
        Intent intent = new Intent(this, StartPartyActivity.class);
        startActivity(intent);

        Animatoo.animateSlideRight(this);

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
        if(number == 0) {
            number++;
        }
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
        userName = prefs.getString("user_name", null);
        // Check if the preference field is set. If not, prompt the user to input their username
        if (userName == "") {
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

        // Saves the userID generated in a preference field
        final SharedPreferences prefs2 = PreferenceManager
                .getDefaultSharedPreferences(this);

        // Check if the userID is set already
        if (yourUserID == "000000") {
            // Generate userID using the random number generating function above
            yourUserID = generateUserID();
            SharedPreferences.Editor editor = prefs2
                    .edit();
            editor.putString("userID",
                    yourUserID);
            editor.commit();
        }

        // Text to check if the username is being set correctly (Can also be kept and styled if we want to display their username)
        TextView xmlUserNameCheck = (TextView) findViewById(R.id.userNameCheck);
        //xmlUserNameCheck.setText("HELLO, " + userName.toUpperCase());

        // Obtain a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Set the connection parameters

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

        getDeviceLocation();
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
        playSong("spotify:track:1Eolhana7nKHYpcYpdVcT5");
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

    /*
        This method is used to push a SongQueue to firebase, it's location in firebase
        is determined by the SongQueue's partyLeaderID
     */
    private void pushData(SongQueue s){
        // A HashMap is used to upload information to firebase, the String is the location in
        // firebase and the Object is the SongQueue to be put in firebase
        HashMap<String, Object> map = new HashMap<>();
        String folder = "/queues/"+ Integer.toString(s.partyLeaderID);
        map.put(folder, s);
        mDatabase.updateChildren(map)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("pushData", "Error adding a SongQueue", e);
                    }
                });
    }

    /*
        This method is used to perform any action that reads or updates the SongQueue that is in
        Firebase. It takes in an "action" parameter which specifies which method should be called.
        Possible methods are: displayQueue, updateVotes, addASong, playNextSong, and endParty.
        updateVotes requires the song uri and addASong requires a song, otherwise, the song and
        uri parameters can be left blank
     */
    private void pullData(int partyLeaderID, final String action, final String uri, final Song song) {
        // Somehow this allows the action String to work as intented
        final String[] actionRef = {action};

        // Use the partyLeaderID to access the correct database
        DatabaseReference qReference = mDatabase.child("queues").child(Integer.toString(partyLeaderID));

        // The onDataChange will be called once to perform an action, and then once again if the
        // data was changed. The action var is set to "" so that no action repeats in one call of
        // pullData
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the SongQueue from Firebase
                SongQueue st = dataSnapshot.getValue(SongQueue.class);

                if (st != null) {
                    // Perform the desired action
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
                }
                // Set action to "" so that the action doesn't repeat
                actionRef[0] ="";
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting SongQueue failed, log a message
                Log.w("PullData", "Failed to Load SongQueue from Firebase", databaseError.toException());
            }
        };
        qReference.addValueEventListener(postListener);
    }

    /*
        This method puts the LocationList it is given into Firebase, once the Location list is
        initialized in Firebase, this should only be called from the pullLocation related methods.
        The layout of this is very similar to pushData.
     */
    private void pushLocation(LocationList ll) {
        // HashMap used to put things into Firebase, String is location in the database, object is
        // the LocationList object
        HashMap<String, Object> map = new HashMap<>();
        String folder = "/locations";
        map.put(folder, ll);
        mDatabase.updateChildren(map)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("pushLocation", "Error adding the LocationList", e);
                    }
                });
    }

    /*
        This method is used to perform any action that relates to checking locations or adding /
        removing locations from the database.
        The action variable must be either: addLocation, compareLocations, or deleteLocation
        and specifies which method will be used.
        partyId is needed for delete location, and userLocation is needed for add a location or
        compareLocations, otherwise, the partyId and userLocation will not be used.
     */
    private void pullLocation(final String action, final int partyId, final PartyLocation userLocation){
        // Used to make the action variable work as intended
        final String[] actionRef = {action};

        // Get to the locations part of Firebase
        DatabaseReference qReference = mDatabase.child("locations");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the LocationList stored in Firebase
                LocationList partyLocations = dataSnapshot.getValue(LocationList.class);
                if (partyLocations != null) {
                    // Choose which method should be called
                    if (actionRef[0].equals("addLocation")) {
                        addLocation(partyLocations, userLocation);
                    } else if (actionRef[0].equals("compareLocations")) {
                        compareLocations(partyLocations, userLocation);
                    } else if (actionRef[0].equals("deleteLocation")) {
                        deleteLocation(partyLocations, partyId);
                    }
                }

                // Set action to "" so the same action doesn't repeat in one call
                actionRef[0] ="";
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("PullLocation", "Failed to Load LocationList from Firebase", databaseError.toException());
            }
        };
        qReference.addValueEventListener(postListener);
    }

    /*
        This method takes in the locationlist and removes the location corresponding to the
        provided partyId and pushes the result to Firebase
     */
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
    This method takes the user's current location, iterates through the total list of party locations,
    and compares each index. If the distance is within 1 km, then the party at that current index
    is added to the global variable list of "Parties Nearby"
     */
    private void compareLocations(LocationList locationList, PartyLocation userLocation) {
        List<PartyLocation> partyLocations = locationList.getPl();
        double distanceBetween;
        if (userLocation != null) {
            for (int i = 0; i < partyLocations.size(); i++) {
                // Calculate the distance between the userLocation and location i in the locationList
                distanceBetween = calculateDistance(userLocation.getLocation().getLat(),
                        userLocation.getLocation().getLawng(), partyLocations.get(i).getLocation().getLat(),
                        partyLocations.get(i).getLocation().getLawng());

                if (distanceBetween < 1.0) {
                    partiesNearby.add(partyLocations.get(i));
                }
            }
        } else{
            Log.i("compareLocations", "User location is null");
        }
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
    Getter method which returns the list of parties nearby
     */
    public static List<PartyLocation> getPartiesNearby() {
        return partiesNearby;
    }

    /*
    This grabs the top voted song from the queue, plays it,
    deletes the song from the queue and pushes the queue
    back to firebase
     */
        protected void playNextSong (SongQueue songQueue){
            Song s = songQueue.nextSong();
            if (s != null) {
                playSong(s.getURI());
                songQueue.removeSong(s.getURI());
            }
            pushData(songQueue);
        }

    /*
    This adds a song to the queue and then
    returns the queue to firebase
     */
        private void addASong (SongQueue songQueue, Song song){
            if (song != null) {
                songQueue.addSong(song);
            }
            pushData(songQueue);
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
            mDatabase.child("/queues/" + st.getPartyLeaderID()).removeValue();
    }

    /*
    This displays the queue... Need some help from front end folks
     */
        private void displayQueue (SongQueue st){
            st.sortSongs();
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

        private void getDeviceLocation() {
            // Check if permission granted
            int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            // If not, ask for it
            if (permission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }

            // If permission granted, grab the lat and long of the current location
            else {
                mFusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    LatLong newLocation = new LatLong(location.getLatitude(), location.getLongitude());
                                    userLocationGlobal.setLocation(newLocation);
                                    Log.i("Current Latitude", String.valueOf(userLocationGlobal.getLocation().getLat()));
                                    Log.i("Current Longitude", String.valueOf(userLocationGlobal.getLocation().getLawng()));
                                }
                            }
                        });

            }
        }

    }