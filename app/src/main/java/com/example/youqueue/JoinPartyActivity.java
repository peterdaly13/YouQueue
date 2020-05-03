package com.example.youqueue;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.app.AlertDialog;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JoinPartyActivity extends AppCompatActivity {
    List<PartyLocation> nearbyParties = MainActivity.getPartiesNearby();
    String[] parties = new String[nearbyParties.size()];
    static String yourPartyId="";


    public DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_party);
        addNearbyParties();
    }

    public void goHome(View view) {
        Log.i("Info", "Back Button pressed");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Animatoo.animateSlideRight(this);
    }

    public void goToJoinedParty(View view) {
        String code = ((EditText)findViewById(R.id.editText)).getText().toString();
        checkIfValidParty(code);
        Animatoo.animateSlideLeft(this);
    }

    public void checkIfValidParty(String code) {
        DatabaseReference qReference = mDatabase.child("queues").child(code);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the SongQueue from Firebase
                SongQueue st = dataSnapshot.getValue(SongQueue.class);

                if (st == null) {
                    Log.i("CheckIfValidParty", "not a valid party");
                    EnterValidCodeDialog dialog = new EnterValidCodeDialog();
                    dialog.show(getSupportFragmentManager(), "enter valid code dialog");
                } else {
                    Log.i("Info", "Enter Button pressed");
                    changeScreen();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting SongQueue failed, log a message
                Log.w("CheckIfValidParty", "Failed to check for valid party", databaseError.toException());
            }
        };
        qReference.addValueEventListener(postListener);
    }

    private void changeScreen() {
        yourPartyId = ((EditText)findViewById(R.id.editText)).getText().toString();
        Intent intent = new Intent(this, JoinedParty.class);
        startActivity(intent);
        Animatoo.animateSlideLeft(this);
    }

    public static String getYourPartyId(){
        return yourPartyId;
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

        private void pullLocation(final String action, final int partyId, final PartyLocation userLocation){
            final String[] actionRef = {action};
            DatabaseReference qReference = mDatabase.child("locations");
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    Log.i("datasnapshot", dataSnapshot.getValue().toString());
                    LocationList partyLocations = dataSnapshot.getValue(LocationList.class);
                    if (partyLocations != null) {
                        if (actionRef[0].equals("addLocation")) {
                            addLocation(partyLocations, userLocation);
                        } else if (actionRef[0].equals("compareLocations")) {
                            compareLocations(partyLocations, userLocation);
                        } else if (actionRef[0].equals("deleteLocation")) {
                            deleteLocation(partyLocations, partyId);
                        }
                        actionRef[0] = "";
                    }
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
                        nearbyParties.add(partyLocations.get(i));
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
        public List<PartyLocation> getPartiesNearby() {
            return nearbyParties;
        }


    }
