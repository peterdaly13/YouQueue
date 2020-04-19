package com.example.youqueue;

import java.util.PriorityQueue;

public class SongQueue {

    private int partyLeaderID;
    private PriorityQueue<Song> q;

    public SongQueue(int partyLeaderID){
        this.partyLeaderID=partyLeaderID;
        q= new PriorityQueue<Song>();
    }

    Song nextSong(){
        return q.poll();
    }
    void addSong(Song s){
        q.add(s);
    }
    int getPartyLeaderID(){
        return partyLeaderID;
    }
}
