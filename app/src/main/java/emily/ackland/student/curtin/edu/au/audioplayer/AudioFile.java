package emily.ackland.student.curtin.edu.au.audioplayer;

/**
 * Created by Emily on 11/27/2017.
 */

public class AudioFile {
    private long id;
    private String title;
    private String artist;

    public AudioFile(long songID, String songTitle, String songArtist) {
        id=songID;
        title=songTitle;
        artist=songArtist;
    }
    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
}
