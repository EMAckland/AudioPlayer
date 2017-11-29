package emily.ackland.student.curtin.edu.au.audioplayer;

/**
 * Created by Emily on 11/27/2017.
 */

public class AudioFile {
    private long id;
    private String title;
    private String artist;
    private String duration;

    public AudioFile(long songID, String songTitle, String songArtist, String inDuration) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        duration=inDuration;
    }
    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getDuration(){return duration;}
}
