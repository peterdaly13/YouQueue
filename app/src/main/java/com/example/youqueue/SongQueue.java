package com.example.youqueue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;

public class SongQueue {

    public int partyLeaderID;
    public List<Song> queue;

        public SongQueue(){

        }

        public SongQueue(int partyLeaderID) {
            this.partyLeaderID = partyLeaderID;
            queue = new ArrayList<Song>(); /*{
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public boolean contains(@Nullable Object o) {
                    return false;
                }

                @NonNull
                @Override
                public Iterator<Song> iterator() {
                    return null;
                }

                @NonNull
                @Override
                public Object[] toArray() {
                    return new Object[0];
                }

                @NonNull
                @Override
                public <T> T[] toArray(@NonNull T[] a) {
                    return null;
                }

                @Override
                public boolean add(Song song) {
                    return false;
                }

                @Override
                public boolean remove(@Nullable Object o) {
                    return false;
                }

                @Override
                public boolean containsAll(@NonNull Collection<?> c) {
                    return false;
                }

                @Override
                public boolean addAll(@NonNull Collection<? extends Song> c) {
                    return false;
                }

                @Override
                public boolean addAll(int index, @NonNull Collection<? extends Song> c) {
                    return false;
                }

                @Override
                public boolean removeAll(@NonNull Collection<?> c) {
                    return false;
                }

                @Override
                public boolean retainAll(@NonNull Collection<?> c) {
                    return false;
                }

                @Override
                public void clear() {

                }

                @Override
                public Song get(int index) {
                    return null;
                }

                @Override
                public Song set(int index, Song element) {
                    return null;
                }

                @Override
                public void add(int index, Song element) {

                }

                @Override
                public Song remove(int index) {
                    return null;
                }

                @Override
                public int indexOf(@Nullable Object o) {
                    return 0;
                }

                @Override
                public int lastIndexOf(@Nullable Object o) {
                    return 0;
                }

                @NonNull
                @Override
                public ListIterator<Song> listIterator() {
                    return null;
                }

                @NonNull
                @Override
                public ListIterator<Song> listIterator(int index) {
                    return null;
                }

                @NonNull
                @Override
                public List<Song> subList(int fromIndex, int toIndex) {
                    return null;
                }
            };*/
        }

        Song nextSong() {
            return queue.get(0);
        }

        void addSong(Song s) {
            queue.add(s);
        }

        int getPartyLeaderID() {
            return partyLeaderID;
        }

    @Override
    public String toString() {
        return "SongQueue{" +
                "partyLeaderID=" + partyLeaderID +
                ", queue=" + queue +
                '}';
    }
}
