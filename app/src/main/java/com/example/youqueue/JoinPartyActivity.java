package com.example.youqueue;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class JoinPartyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_party);
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

}
