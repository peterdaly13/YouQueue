package com.example.youqueue;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.PriorityQueue;
import java.util.HashMap;

public class SongQueue {

    final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private int partyLeaderID;
    private PriorityQueue<Song> queue;
    public static HashMap<Integer,SongQueue> hashMap = new HashMap<Integer, SongQueue>();

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

        public void updateDatabase(SongQueue queue){
            //queueRef.setValueAsync(queue);
        }

}
