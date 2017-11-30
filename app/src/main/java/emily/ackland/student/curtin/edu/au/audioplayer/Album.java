package emily.ackland.student.curtin.edu.au.audioplayer;

import java.util.ArrayList;

/**
 * Created by Emily on 11/30/2017.
 */

public class Album {
    private ArrayList<AudioFile> tracks = new ArrayList<>();
    private String artist;
    private String title;
    public Album(String artist, String title, ArrayList<AudioFile> tracks){
        this.artist = artist;
        this.title = title;
        this.tracks = tracks;
    }
    public ArrayList<AudioFile> getTracks() {return tracks;}
    public String getArtist() {return artist;}
    public String getTitle() {return title;}
}
