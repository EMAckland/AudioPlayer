package emily.ackland.student.curtin.edu.au.audioplayer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emily on 12/2/2017.
 */

public class PlaylistTracksMap{
	private static final String PLAYLIST_ID = AudioContract.FeedEntry.PLAYLIST_ID ;
	private static final String TRACK_ID = AudioContract.FeedEntry.TRACK_ID ;
	private Map<String,String> playlistToTracks;
	public PlaylistTracksMap(){
		playlistToTracks = new HashMap<>();
	}
	public void addTrackToPlaylist(String track, String playlist){
		playlistToTracks.put(TRACK_ID, track);
		playlistToTracks.put(PLAYLIST_ID, playlist);
	}
}

