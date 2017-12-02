package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Emily on 11/30/2017.
 */

public class Album {
    private ArrayList<AudioFile> tracks;
    private String artist;
    private String title;
    private Long albumID;
    private Long ID;

    public Album(String artist, String title, Long ID, Long albumID) {
        this.artist = artist;
        this.title = title;
        this.albumID = albumID;
        this.ID = ID;
        tracks = new ArrayList<>();
    }

    public Album(String artist, String title, ArrayList<AudioFile> tracks, Long ID,Long albumID) {
        this.artist = artist;
        this.title = title;
        this.albumID = albumID;
        this.tracks = tracks;
        this.ID = ID;
    }


    public ArrayList<AudioFile> getTracks() {
        return tracks;
    }
    public Long getAlbumID(){return albumID;}
    public String getArtist() {
        return artist;
    }
    public String getTitle() {
        return title;
    }
    public Bitmap getAlbumArt(Context ctx) {
        return MyUtils.getAlbumart(ctx, ID);
    }

    public void setTracks(ArrayList<AudioFile> inTracks) {

        tracks = inTracks;
    }

    public boolean equals(Album a) {
        if (artist.equals(a.getArtist()) && title.equals(a.getTitle()))
            return true;
        else
            return false;
    }
    public int hashCode() {
        return Objects.hash(artist, title);
    }

}
