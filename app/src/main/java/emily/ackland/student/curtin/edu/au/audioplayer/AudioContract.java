package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.Context;
import android.provider.BaseColumns;

/**
 * Created by Emily on 12/1/2017.
 */

public class AudioContract {
	Context ctx;
	private AudioContract() {
	}
	public void setContext(Context ctx){
		this.ctx=ctx;
	}
	/* Inner class that defines the table contents */
	public static class FeedEntry implements BaseColumns {
		public static final String TRACKS_TABLE = "Tracks Table";
		public static final String ALBUM_TABLE = "Albums Table";
		public static final String TRACK_ID = "Track Key";
		public static final String ALBUM_ID = "Album Key";
		public static final String _ID = "Primary Key";
		public static final String TRACK = "Track";
		public static final String ARTIST = "Artist";
		public static final String ALBUM = "Album";
		public static final String DURATION = "Duration";
		public static final String ALBUM_ART = "Album Art Path";
		public static final String PLAYLISTS_TABLE = "Playlist Table";
		public static final String PLAYLIST_NAME = "Playlist Name";
		public static final String PLAYLIST_ID = "Playlist ID";
		public static final String PLAYLIST_TRACKS_TABLE = "Playlist Tracks";
		public static final String ARTIST_ID = "ARTIST ID";
	}
}
