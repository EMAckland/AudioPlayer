package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

	public long insertIntoPlayList(Map<String, String> s){
		ContentValues trackValues = new ContentValues();
		ContentValues playlistValues = new ContentValues();
		trackValues.put(DataBaseHelper.TRACK_ID, s.get(DataBaseHelper.TRACK_ID));
		trackValues.put(DataBaseHelper.PLAYLIST_ID, s.get(DataBaseHelper.PLAYLIST_ID));
		return database.insert(DataBaseHelper.PLAYLIST_TRACKS_TABLE, null, trackValues);
	}
	public long createNewPlayList(String playlist){
		ContentValues values = new ContentValues();
		//values.put(DataBaseHelper.PLAYLIST_ID, s.get(DataBaseHelper.PLAYLIST_ID));
		values.put(DataBaseHelper.PLAYLIST_NAME, playlist);
		return database.insert(DataBaseHelper.PLAYLISTS_TABLE, null, values);
	}
	public Cursor query(String table, String[] columns, String selection,
											String[] args, String groupBy, String having, String orderBy){

		return database.query(table,columns,selection,args,groupBy,having,orderBy);
	}
	public void wipeAll(){
		dbHelper.wipeAll(database);
	}
}
