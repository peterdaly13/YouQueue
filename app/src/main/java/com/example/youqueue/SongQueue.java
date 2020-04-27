package com.example.youqueue;

import java.util.PriorityQueue;

public class SongQueue {

    private int partyLeaderID;
    private PriorityQueue<Song> queue;

        public SongQueue(int partyLeaderID) {
            this.partyLeaderID = partyLeaderID;
            queue = new PriorityQueue<Song>();
        }

        Song nextSong() {
            return queue.poll();
        }

        void addSong(Song s) {
            queue.add(s);
        }

        int getPartyLeaderID() {
            return partyLeaderID;
        }
}
