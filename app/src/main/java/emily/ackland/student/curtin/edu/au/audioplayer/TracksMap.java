package emily.ackland.student.curtin.edu.au.audioplayer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emily on 12/2/2017.
 */

public class TracksMap{
	public static final String TRACK_ID = AudioContract.FeedEntry.TRACK_ID ;
	public static final String PLAYLIST_NAME = AudioContract.FeedEntry.PLAYLIST_NAME ;
	public static final String TRACK = AudioContract.FeedEntry.TRACK ;
	public static final String ARTIST = AudioContract.FeedEntry.ARTIST ;
	public static final String DURATION = AudioContract.FeedEntry.DURATION  ;
	public static final String ALBUM_ID = AudioContract.FeedEntry.ALBUM_ID ;
	private Map<String,String> tracks;

	public TracksMap(){
		tracks = new HashMap<>();
	}
	public void addTrack(){

	}
}
