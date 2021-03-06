package com.example.youqueue;

import java.util.Arrays;
import java.util.List;

public class SongList {
    public static Song[] getSongs(){

        Song[] songs = new Song[50];

        songs[0] = new Song("spotify:track:0hKRSZhUGEhKU6aNSPBACZ", 0, 0, "A Day in the Life", 337, "The Beatles");
        songs[1] = new Song("spotify:track:6K4t31amVTZDgR3sKmwUJJ", 0, 0, "The Less I Know the Better", 216, "Tame Impala");
        songs[2] = new Song("spotify:track:5QvBXUm5MglLJ3iBfTX2Wo", 0, 0, "RUNNING OUT OF TIME", 177, "Tyler the Creator");
        songs[3] = new Song("spotify:track:2tznHmp70DxMyr2XhWLOW0", 0, 0, "Cigarette Daydreams", 208, "Cage the Elephant");
        songs[4] = new Song("spotify:track:2xLMifQCjDGFmkHkpNLD9h", 0, 0, "SICKO MODE", 312, "Travis Scott");
        songs[5] = new Song("spotify:track:7Jh1bpe76CNTCgdgAdBw4Z", 0, 0, "Heroes", 371, "David Bowie");
        songs[6] = new Song("spotify:track:3djNBlI7xOggg7pnsOLaNm", 0, 0, "California Love", 284, "Tupac");
        songs[7] = new Song("spotify:track:0wJoRiX5K5BxlqZTolB2LD", 0, 0, "Purple Haze", 170, "Jimi Hendrix");
        songs[8] = new Song("spotify:track:20VuO95A8RxUPlShnfYArW", 0, 0, "I Got The...", 395, "Labi Siffre");
        songs[9] = new Song("spotify:track:1eyzqe2QqGZUmfcPZtrIyt", 0, 0, "Midnight City", 240, "M83");
        songs[10] = new Song("spotify:track:4RNHbYuRaZesMODlihhsUG", 0, 0, "What's Going On", 236, "Marvin Gaye");
        songs[11] = new Song("spotify:track:3TZwjdclvWt7iPJUnMpgcs", 0, 0, "Jump Around", 214, "House of Pain");
        songs[12] = new Song("spotify:track:6sPOmDulFtLzfX25zICNrC", 0, 0, "Build Me Up Buttercup", 177, "The Foundations");
        songs[13] = new Song("spotify:track:4euxYgIl5XEqUj5WB9lHNq", 0, 0, "Home Depot Beat", 35, "Home Depot");
        songs[14] = new Song("spotify:track:6S3JlDAGk3uu3NtZbPnuhS", 0, 0, "Baby Blue", 217, "Badfinger");
        songs[15] = new Song("spotify:track:0vFOzaXqZHahrZp6enQwQb", 0, 0, "Money", 382, "Pink Floyd");
        songs[16] = new Song("spotify:track:6H3kDe7CGoWYBabAeVWGiD", 0, 0, "Gimme Shelter", 270, "The Rolling Stones");
        songs[17] = new Song("spotify:track:5ZLzl6T8JwqMTMdoE0nCbU", 0, 0, "Friend of the Devil", 201, "Grateful Dead");
        songs[18] = new Song("spotify:track:0j3p1p06deJ7f9xmJ9yG22", 0, 0, "Back in the U.S.S.R.", 163, "The Beatles");
        songs[19] = new Song("spotify:track:0UAJH0k4k3slcE83a9UGCe", 0, 0, "Lola", 241, "The Kinks");
        songs[20] = new Song("spotify:track:16qzGrIMWoxerw2gnW0zuv", 0, 0, "Pink Moon", 123, "Nick Drake");
        songs[21] = new Song("spotify:track:6FRwDxXsvSasw0y2eDArsz", 0, 0, "Sunshine of Your Love", 250, "Cream");
        songs[22] = new Song("spotify:track:78lgmZwycJ3nzsdgmPPGNx", 0, 0, "Immigrant Song", 146, "Led Zeppelin");
        songs[23] = new Song("spotify:track:3qT4bUD1MaWpGrTwcvguhb", 0, 0, "Black Dog", 293, "Led Zeppelin");
        songs[24] = new Song("spotify:track:0hCB0YR03f6AmQaHbwWDe8", 0, 0, "Whole Lotta Love", 333, "Led Zeppelin");
        songs[25] = new Song("spotify:track:6gZVQvQZOFpzIy3HblJ20F", 0, 0, "Man in the Box", 284, "Alice in Chains");
        songs[26] = new Song("spotify:track:2RlgNHKcydI9sayD2Df2xp", 0, 0, "Mr. Blue Sky", 303, "Electric Light Orchestra");
        songs[27] = new Song("spotify:track:72ahyckBJfTigJCFCviVN7", 0, 0, "Don't Bring Me Down", 243,"Electric Light Orchestra");
        songs[28] = new Song("spotify:track:2hdNya0b6Cc2YJ8IyaQIWp", 0, 0, "Livin' Thing", 212, "Electric Light Orchestra");
        songs[29] = new Song("spotify:track:58PSYdY0GFg0LFb2PxYk4T", 0, 0, "Mannish Boy", 320, "Muddy Waters");
        songs[30] = new Song("spotify:track:2wvMC5EyaaYQwBfiwwY2xE", 0, 0, "Life's Been Good", 535, "Joe Walsh");
        songs[31] = new Song("spotify:track:2XHjFJVXYlEzoDvN82h8s5", 0, 0, "Rocky Mountain Way", 315, "Joe Walsh");
        songs[32] = new Song("spotify:track:1Y373MqadDRtclJNdnUXVc", 0, 0, "Paranoid", 168, "Black Sabbath");
        songs[33] = new Song("spotify:track:3IOQZRcEkplCXg6LofKqE9", 0, 0, "Iron Man", 355, "Black Sabbath");
        songs[34] = new Song("spotify:track:63T7DJ1AFDD6Bn8VzG6JE8", 0, 0, "Paint it, Black", 202, "The Rolling Stones");
        songs[35] = new Song("spotify:track:4gMgiXfqyzZLMhsksGmbQV", 0, 0, "Another Brick in the Wall, Pt. 2", 238, "The Rolling Stones");
        songs[36] = new Song("spotify:track:6dGnYIeXmHdcikdzNNDMm2", 0, 0, "Here Comes the Sun", 185, "The Beatles");
        songs[37] = new Song("spotify:track:7iN1s7xHE4ifF5povM6A48", 0, 0, "Let it Be", 243, "The Beatles");
        songs[38] = new Song("spotify:track:2EqlS6tkEnglzr7tkKAAYD", 0, 0, "Come Together", 259, "The Beatles");
        songs[39] = new Song("spotify:track:3BQHpFgAp4l80e1XslIjNI", 0, 0, "Yesterday", 125, "The Beatles");
        songs[40] = new Song("spotify:track:5nrmGFJ87crVoJF5xdRqwn", 0, 0, "Waterloo Sunset", 194, "The Kinks");
        songs[41] = new Song("spotify:track:1akgiRM3mN2nxu2AX6ACCW", 0, 0, "I Love College", 241, "Asher Roth");
        songs[42] = new Song("spotify:track:27L8sESb3KR79asDUBu8nW", 0, 0, "Stacy's Mom", 198, "Fountains of Wayne");
        songs[43] = new Song("spotify:track:2lVDc57IMK6nypg2iuEWVR", 0, 0, "Tipsy", 242, "J-Kwon");
        songs[44] = new Song("spotify:track:1V4jC0vJ5525lEF1bFgPX2", 0, 0, "Shots", 222, "LMFAO ft. Lil Jon");
        songs[45] = new Song("spotify:track:3omXshBamrREltcf24gYDC", 0, 0, "First", 200, "Cold War Kids");
        songs[46] = new Song("spotify:track:4RY96Asd9IefaL3X4LOLZ8", 0, 0, "In Da Club", 193, "50 Cent");
        songs[47] = new Song("spotify:track:5jrdCoLpJSvHHorevXBATy", 0, 0, "Dark Horse", 215, "Katy Perry");
        songs[48] = new Song("spotify:track:7lPjS6Yd4lRk4BsboDsm1H", 0, 0, "Roundabout", 515, "Yes");

        return songs;
    }

    public static Song getSong(String title){
        Song[] songs = getSongs();
        for(int i=0; i<50;i++){
            if(songs[i].getName().toLowerCase().equals(title.toLowerCase())){
                return songs[i];
            }
        }
        return null;
    }

    public static List<Song> listSongs(){
        Song[] songs = getSongs();
        List<Song> songList = Arrays.asList(songs);
        return songList;
    }

    public static String[] getSongNames(){
        String[] songNames = new String[48];
        Song[] songs = getSongs();
        for(int i = 0; i < 48; i++){
            songNames[i] = songs[i].getName();
        }
        return songNames;
    }
}
