package com.example.youqueue;

public class Song implements Comparable<Song>{

    public String URI;
    public int userID;
    public int votes;
    public String name;
    public int length;
    public String artist;

    public Song(){

    }

    public Song(String URI, int userID, int votes, String name, int length, String artist){
        this.URI=URI;
        this.userID=userID;
        this.votes=votes;
        this.name=name;
        this.length = length;
        this.artist = artist;

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
    int getLength(){return length;}
    String getArtist(){return artist;}

    //Setters (Shouldn't ever need to change URI, userID or name)
    private void setVotes(int v){
        this.votes=v;
    }
    public void incrementVotes(){
        this.votes+=1;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public void setArtist(String artist) {
        this.artist = artist;
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

    @Override
    public String toString() {
        return "Song{" +
                "URI='" + URI + '\'' +
                ", userID=" + userID +
                ", votes=" + votes +
                ", name='" + name + '\'' +
                ", length=" + length +
                '}';
    }
}
