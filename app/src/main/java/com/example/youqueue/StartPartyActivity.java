package com.example.youqueue;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.Random;

public class StartPartyActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "d19dfd48fcd54626a0f8ff696ada3b9e";
    private static final String REDIRECT_URI = "com.youqueue://callback";
    private SpotifyAppRemote mSpotifyAppRemote = null;

    public String yourPartyID;
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

    public void goHome(View view) {
        Log.i("Info", "Back Button pressed");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void resume(View view) {
        //ma.resumePlayback();
    }

    //public void pause(View view) {
      //  ma.pausePlayback();
    //}

}
