package com.example.youqueue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;

public class SongQueue {

    public int partyLeaderID;
    public List<Song> queue;

        public SongQueue(){}

        public SongQueue(int partyLeaderID) {
            this.partyLeaderID = partyLeaderID;
            queue = new ArrayList<Song>();
        }

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

        void removeSong(String uri) {
            for (int i = 0; i < queue.size(); i++){
                if (queue.get(i).getURI().equals(uri)) {
                    queue.remove(i);
                }
            }
        }

        Song getSong(String uri) {
            for (int i = 0; i < queue.size(); i++){
                if (queue.get(i).getURI().equals(uri)) {
                    return queue.get(i);
                }
            }
            return null;
        }

        List<Song> sortSongs() {
            Collections.sort(queue);
            return queue;
        }

        void addSong(Song s) {
            queue.add(s);
        }

        int getPartyLeaderID() {
            return partyLeaderID;
        }

    public List<Song> getQueue() {
        return queue;
    }

    @Override
    public String toString() {
        return "SongQueue{" +
                "partyLeaderID=" + partyLeaderID +
                ", queue=" + queue +
                '}';
    }
}
