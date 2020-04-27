package com.example.youqueue;

public class Song implements Comparable<Song>{

    private String URI;
    private int userID;
    private int votes;
    private String name;

    public Song(String URI, int userID, int votes, String name){
        this.URI=URI;
        this.userID=userID;
        this.votes=votes;
        this.name=name;
    }
    //Getters
    String getURI(){
        return URI;
    }
    int getUserID(){
        return userID;
    }
    int getVotes(){
        return votes;
    }
    String getName(){ return name;}

    //Setters (Shouldn't ever need to change URI, userID or name)
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
