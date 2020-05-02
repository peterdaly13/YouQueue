package com.example.youqueue;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.app.AlertDialog;

import java.util.List;

public class JoinPartyActivity extends AppCompatActivity {
    List<PartyLocation> nearbyParties = MainActivity.getPartiesNearby();
    String[] parties = new String[nearbyParties.size()];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_party);
        addNearbyParties();
    }

    public void goHome(View view) {
        Log.i("Info", "Back Button pressed");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToJoinedParty(View view) {
        String code = ((EditText)findViewById(R.id.editText)).getText().toString();
        if (code != "") {
            Log.i("Info", "Enter Button pressed");
            Intent intent = new Intent(this, JoinedParty.class);
            startActivity(intent);
        } else {
            EnterValidCodeDialog dialog = new EnterValidCodeDialog();
            dialog.show(getSupportFragmentManager(), "enter valid code dialog");
        }

    }

    public void addNearbyParties() {
        for (int i = 0; i < parties.length; i++) {
            parties[i] = nearbyParties.get(i).getUsername() + "'s Party";
        }
    }

    public void displayAlertDialog(View view) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Intent intent = new Intent(this, JoinedParty.class);

        builder.setTitle("Choose a Nearby Party");

        // add a list
        builder.setItems(parties, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Currently displays 5 nearest locations
                switch (which) {
                    case 0:
                        startActivity(intent);
                    case 1:
                        startActivity(intent);
                    case 2:
                        startActivity(intent);
                    case 3:
                        startActivity(intent);
                    case 4:
                        startActivity(intent);
                }
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
