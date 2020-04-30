package com.example.youqueue;

import com.google.type.LatLng;

public class PartyLocation {

    private LatLng location;
    private int partyId;
    private String username;

    public PartyLocation(){
    }

    public PartyLocation(LatLng loc, int partyId, String username){
        this.location = loc;
        this.partyId = partyId;
        this.username = username;
    }

    public LatLng getLocation() {
        return location;
    }

    public int getPartyId() {
        return partyId;
    }

    public String getUsername() {
        return username;
    }
}
