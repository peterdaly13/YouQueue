package com.example.youqueue;

public class Song implements Comparable<Song>{

    private int URI;
    private int userID;
    private int votes;

    public Song(int URI, int userID, int votes){
        this.URI=URI;
        this.userID=userID;
        this.votes=votes;
    }
    //Getters
    int getURI(){
        return URI;
    }
    int getUserID(){
        return userID;
    }
    int getVotes(){
        return votes;
    }

    //Setters (Shouldn't ever need to change URI or userID)
    private void setVotes(int v){
        this.votes=v;
    }
    private void incrementVotes(){
        this.votes+=1;
    }

    @Override
    public int compareTo(Song s) {
        int i = this.getVotes();
        int j =s.getVotes();

        if(i==j){
            return 0;
        }
        else if (i>j){
            return -1;
        }
        else{
            return 1;
        }
    }
}