package emily.ackland.student.curtin.edu.au.audioplayer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emily on 12/2/2017.
 */

public class PlaylistMap extends HashMap implements Map {
	public static final String PLAYLIST_NAME = AudioContract.FeedEntry.PLAYLIST_NAME ;
	public static final String PLAYLIST_ID = AudioContract.FeedEntry.PLAYLIST_ID ;
	private Map<String,String> playlistsID;
	private Map<String,String> playlistsName;
	public PlaylistMap(){
		playlistsID = new HashMap<>();
	}
	public void addNewPlaylist(String name, String id){
		playlistsID.put(name,id);
		playlistsName.put(id,name);
	}
}
