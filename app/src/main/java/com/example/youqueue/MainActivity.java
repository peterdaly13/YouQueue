package com.example.youqueue;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "d19dfd48fcd54626a0f8ff696ada3b9e";
    private static final String REDIRECT_URI = "com.youqueue://callback";
    private SpotifyAppRemote mSpotifyAppRemote = null;

    public void goToSettings(View view) {
        Log.i("Info", "Settings Button pressed");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void goToJoinParty(View view) {
        Log.i("Info", "Join Party Button pressed");
        Intent intent = new Intent(this, JoinPartyActivity.class);
        startActivity(intent);
    }

    public void goToStartParty(View view) {
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

    }
    //Need to write code to pull a songQueue from Firebase
    private SongQueue pullData(){
        //only here to temporarily remove return error
        SongQueue s = new SongQueue(1);

        return s;
    }

}
