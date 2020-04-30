package com.example.youqueue;


import com.google.android.gms.maps.model.LatLng;

public class PartyLocation {

    private LatLong location;
    private int partyId;
    private String username;

    public PartyLocation(){
    }

    public PartyLocation(LatLong loc, int partyId, String username){
        this.location = loc;
        this.partyId = partyId;
        this.username = username;
    }

    public LatLong getLocation() {
        return location;
    }

    public int getPartyId() {
        return partyId;
    }

    public String getUsername() {
        return username;
    }
}
