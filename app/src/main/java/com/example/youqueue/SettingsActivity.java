package com.example.youqueue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SettingsActivity extends AppCompatActivity {

    MainActivity ma = new MainActivity();

//    // Initiate Location Switch
//    Switch simpleSwitch = (Switch) findViewById(R.id.locationSwitch);
//
//    // Check whether Starting Parties by Location is True or False
//    Boolean switchState = simpleSwitch.isChecked();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void goHome(View view) {
        Log.i("Info", "Back Button pressed");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void logOut(View view) {

        SpotifyAppRemote mSpotifyAppRemote = ma.getmSpotifyAppRemote();
        //Dialog opens if user is already logged out, else opens spotify login webpage
        //TODO: Fix checking connector to see if user is already logged out
        if (mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected()) {
            Log.i("Info", "Logout Button pressed");
            String url = "https://accounts.spotify.com/en/login/";
            Uri uriURL = Uri.parse(url);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriURL);
            startActivity(launchBrowser);
        } else {
            openDialog();
        }
    }

    public void openDialog() {
        AlreadyLoggedOutDialog dialog = new AlreadyLoggedOutDialog();
        dialog.show(getSupportFragmentManager(), "logged out dialog");
    }

}
