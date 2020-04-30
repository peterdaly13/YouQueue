package com.example.youqueue;

import java.util.List;

public class LocationList {

    private List<PartyLocation> listOfLocations;

    public LocationList(){

    }
    public LocationList(List<PartyLocation> listloc){
        this.listOfLocations= listloc;
    }

    public List<PartyLocation> getPl() {
        return listOfLocations;
    }

    public void setPl(List<PartyLocation> pl) {
        this.listOfLocations = pl;
    }
}
