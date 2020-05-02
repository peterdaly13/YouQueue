package com.example.youqueue;

public class PartyLocation {

    // Store a location in our version of LatLng because Firebase can't handle google's LatLng
    private LatLong location;

    // Unique identifier
    private int partyId;

    // Username of who is hosting the party
    private String username;

    // This constructor is needed for Firebase stuff to work
    public PartyLocation(){}

    // Basic Constructor
    public PartyLocation(LatLong loc, int partyId, String username){
        this.location = loc;
        this.partyId = partyId;
        this.username = username;
    }

    // Getter methods for every field
    public LatLong getLocation() {
        return location;
    }
    public int getPartyId() {
        return partyId;
    }
    public String getUsername() {
        return username;
    }

    public void setLocation(LatLong loc) {
        location = loc;
    }
}
