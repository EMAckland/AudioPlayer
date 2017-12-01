package emily.ackland.student.curtin.edu.au.audioplayer;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Emily on 11/30/2017.
 */

public class Album {
    private ArrayList<AudioFile> tracks = new ArrayList<>();
    private String artist;
    private String title;
    private Bitmap albumArt;

    public Album(String artist, String title, Bitmap albumArt) {
        this.artist = artist;
        this.title = title;
        this.albumArt = albumArt;
    }

    public Album(String artist, String title, ArrayList<AudioFile> tracks, Bitmap albumArt) {
        this.artist = artist;
        this.title = title;
        this.albumArt = albumArt;
        this.tracks = tracks;
    }

    public void setTracks(ArrayList<AudioFile> inTracks) {
        tracks = inTracks;
    }

    public ArrayList<AudioFile> getTracks() {
        return tracks;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
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
