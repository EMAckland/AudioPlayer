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
		public static final String TRACKS_TABLE = "TRACKS_TABLE";
		public static final String ALBUM_TABLE = "ALBUM_TABLE";
		public static final String TRACK_ID = "TRACK_ID";
		public static final String ALBUM_ID = "ALBUM_ID";
		public static final String _ID = "_ID";
		public static final String TRACK = "TRACK";
		public static final String ARTIST = "ARTIST";
		public static final String ALBUM = "ALBUM";
		public static final String DURATION = "DURATION";
		public static final String ALBUM_ART = "ALBUM_ART";
		public static final String PLAYLISTS_TABLE = "PLAYLISTS_TABLE";
		public static final String PLAYLIST_NAME = "PLAYLIST_NAME";
		public static final String PLAYLIST_ID = "PLAYLIST_ID";
		public static final String PLAYLIST_TRACKS_TABLE = "PLAYLIST_TRACKS_TABLE";
		public static final String ARTIST_ID = "ARTIST_ID";
	}
}
