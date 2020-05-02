package com.example.youqueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongQueue {

    // Stores a list of the songs and the Id of the partyLeader (aka the partyId)
    public int partyLeaderID;
    public List<Song> queue;

    public int queueSize;

    // This is needed for Firebase stuff to work
    public SongQueue(){}

    public void setQueueSize() {
        this.queueSize = queue.size();
    }

    // A Basic constructor which creates an empty ArrayList for the queue field
    public SongQueue(int partyLeaderID) {
        queueSize = 0;
        this.partyLeaderID = partyLeaderID;
        queue = new ArrayList<Song>();
        queue.add(new Song("spotify:track:6brdWUL8mJTw8LP6MRFwUu", 0, -10, "", 35, "Silence"));
    }

    // Used to get the song with the highest number of votes
    Song nextSong() {
        Song result = null;
        int maxVotes = -1;
        for (int i = 0; i < queue.size(); i++){
            if (queue.get(i).getVotes() > maxVotes) {
                result = queue.get(i);
            }
        }
        return result;
    }

    // Used to remove a song from the queue given the song URI
    void removeSong(String uri) {
        for (int i = 0; i < queue.size(); i++){
            if (queue.get(i).getURI().equals(uri)) {
                queue.remove(i);
            }
        }
    }

    // Gets a specific song from the queue given the song URI
    Song getSong(String uri) {
        for (int i = 0; i < queue.size(); i++){
            if (queue.get(i).getURI().equals(uri)) {
                return queue.get(i);
            }
        }
        return null;
    }

    // Sorts the songs and puts them in vote order
    List<Song> sortSongs() {
        Collections.sort(queue);
        return queue;
    }

    // Adds a song to the queue
    void addSong(Song s) {
            queue.add(s);
        }

        //  Getter methods
        int getPartyLeaderID() {
            return partyLeaderID;
        }
        public List<Song> getQueue() {
        return queue;
    }

    // We use this to debug
    @Override
    public String toString() {
        return "SongQueue{" +
                "partyLeaderID=" + partyLeaderID +
                ", queue=" + queue +
                '}';
    }

    public int getQueueSize () {
        return queue.size();
    }

    public Song getSongAtIndex(int index) {
        return queue.get(index);
    }
}
