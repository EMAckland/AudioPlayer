package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

/**
 * Created by Emily on 12/1/2017.
 */
public class AudioDB {
	private static final String WHERE_ID_EQUALS = DataBaseHelper._ID
					+ " =?";
	protected SQLiteDatabase database;
	private DataBaseHelper dbHelper;
	private Context mContext;

	public AudioDB(Context context, int mode) throws SQLException {
		this.mContext = context;
		try{
			dbHelper = DataBaseHelper.getHelper(mContext);
			if(mode==R.integer.DATABASE_READ_WRITE_MODE)
				open();
			else
				read();
		}catch (SQLException e){
			throw e;
		}
	}

	public void open() throws SQLException {
		if (dbHelper == null)
			dbHelper = DataBaseHelper.getHelper(mContext);
		database = dbHelper.getWritableDatabase();
	}
	public void read() throws SQLException {
		if (dbHelper == null)
			dbHelper = DataBaseHelper.getHelper(mContext);
		database = dbHelper.getReadableDatabase();
	}
/*public long insert(Map<String, String> s, String table) {
	ContentValues albumValues = new ContentValues();
	ContentValues trackValues = new ContentValues();
		trackValues.put(DataBaseHelper._ID, s.get(DataBaseHelper.TRACK_ID));
		albumValues.put(DataBaseHelper._ID, s.get(DataBaseHelper.ALBUM_ID));
		albumValues.put(DataBaseHelper.TRACK, s.get(DataBaseHelper.TRACK));
		trackValues.put(DataBaseHelper.TRACK, s.get(DataBaseHelper.TRACK));
		albumValues.put(DataBaseHelper.ARTIST, s.get(DataBaseHelper.ARTIST));
		trackValues.put(DataBaseHelper.ARTIST, s.get(DataBaseHelper.ARTIST));
		trackValues.put(DataBaseHelper.ALBUM, s.get(DataBaseHelper.ALBUM));
		albumValues.put(DataBaseHelper.ALBUM_ART, s.get(DataBaseHelper.ALBUM_ART));
		trackValues.put(DataBaseHelper.DURATION, s.get(DataBaseHelper.DURATION));
		database.insert(DataBaseHelper.ALBUM_TABLE+s.get(DataBaseHelper.ALBUM_ID), null, albumValues);
	return database.insert(DataBaseHelper.TRACKS_TABLE, null, trackValues);
	}*/
	public long insertIntoPlayList(Map<String, String> s, String playlist){
		ContentValues trackValues = new ContentValues();
		ContentValues playlistValues = new ContentValues();
		trackValues.put(DataBaseHelper.TRACK_ID, s.get(DataBaseHelper.TRACK_ID));
		trackValues.put(DataBaseHelper.PLAYLIST_ID, s.get(DataBaseHelper.PLAYLIST_ID));
		return database.insert(DataBaseHelper.PLAYLIST_TRACKS_TABLE, null, trackValues);
	}
	public long createNewPlayList(Map<String, String> s, String playlist){
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.PLAYLIST_ID, s.get(DataBaseHelper.PLAYLIST_ID));
		values.put(DataBaseHelper.PLAYLIST_NAME, s.get(DataBaseHelper.PLAYLIST_NAME));
		return database.insert(DataBaseHelper.PLAYLISTS_TABLE, null, values);
	}
}
