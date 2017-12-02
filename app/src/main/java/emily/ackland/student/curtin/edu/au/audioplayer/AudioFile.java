package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Emily on 11/27/2017.
 */

public class AudioFile {
    private long id;
    private String title;
    private String artist;
    private String duration;
    private String album;
    private Long albumID;

    public AudioFile(long songID, String songTitle, String songArtist,
                     String inDuration, String inAlbum, Long inAlbumID) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        duration=inDuration;
        album=inAlbum;
        albumID=inAlbumID;
    }
    public AudioFile(AudioFile inTrack){
        id=inTrack.id;
        title=inTrack.title;
        artist=inTrack.artist;
        duration=inTrack.duration;
        album=inTrack.album;
        albumID=inTrack.albumID;
    }
    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getDuration(){return duration;}
    public String getAlbum(){return album;}
    public Bitmap getAlbumArt(Context ctx){return MyUtils.getAlbumart(ctx,albumID);}

    @Override
    public boolean equals(Object o){
        if(o instanceof AudioFile){
           return ((AudioFile)o).getID() == id;
        }else
            return false;
    }
}
