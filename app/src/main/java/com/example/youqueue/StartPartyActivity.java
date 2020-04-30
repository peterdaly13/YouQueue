package com.example.youqueue;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class StartPartyActivity extends AppCompatActivity {

    public String yourPartyID;
    //MainActivity ma = new MainActivity();

    // Generate the 6 Digit Party ID
    public static String generatePartyID() {
        // Generate random number from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // Convert any number sequence into 6 digits (Example: 0 becomes 000000)
        return String.format("%06d", number);
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
