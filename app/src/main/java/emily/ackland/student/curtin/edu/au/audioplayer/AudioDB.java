package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

/*	public long insertIntoPlayList(Map<String, String> s){
		ContentValues trackValues = new ContentValues();
		ContentValues playlistValues = new ContentValues();
		trackValues.put(DataBaseHelper.TRACK_ID, s.get(DataBaseHelper.TRACK_ID));
		trackValues.put(DataBaseHelper.PLAYLIST_ID, s.get(DataBaseHelper.PLAYLIST_ID));
		return database.insert(DataBaseHelper.PLAYLIST_TRACKS_TABLE, null, trackValues);
	}*/
	public long insertIntoPlayList(String playlist, String track){
		ContentValues trackValues = new ContentValues();
		ContentValues playlistValues = new ContentValues();
		trackValues.put(DataBaseHelper.TRACK_ID, track);
		trackValues.put(DataBaseHelper.PLAYLIST_ID, playlist);
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
	public Cursor rawQuery(String query, String[] args){
		return database.rawQuery(query, args);
	}
	public void wipeAll(){
		dbHelper.wipeAll(database);
	}

	public List<String> getPlaylistFromDB(String[] playlistID){
		List<String> pl = new ArrayList<>();
		//("select * from " + SQLiteHelper.TABLE_NAME + "  where _id = ?", selectionArgs);
		Cursor cursor = database.query(
						AudioContract.FeedEntry.PLAYLIST_TRACKS_TABLE,
						new String[]{"TRACK_ID", "PLAYLIST_ID"},
						"PLAYLIST_ID" + " =?",
						playlistID,
						null,
						null,
						null
		);
		Log.v("Cursor", "Cursor is" + cursor);

		if (cursor != null && cursor.moveToFirst()) {
			int trackCol = cursor.getColumnIndex(AudioContract.FeedEntry.TRACK_ID);

			do {
				pl.add(cursor.getString(trackCol));
				MyUtils.print("PL TRACK" + cursor.getString(trackCol));

			} while (cursor.moveToNext());
			cursor.close();
		}
		return pl;
	}
	public  Map<String,String> getPlaylistsFromDB( ){
		Map<String,String> pl = new HashMap<>();
		Cursor cursor = database.query(
						AudioContract.FeedEntry.PLAYLISTS_TABLE,
						null,
						null,
						null,
						null,
						null,
						null
		);
		if (cursor != null && cursor.moveToFirst()) {
			int playlistCol = cursor.getColumnIndex(AudioContract.FeedEntry.PLAYLIST_NAME);
			int idCol = cursor.getColumnIndex(AudioContract.FeedEntry._ID);
			do {
				Long id = (cursor.getLong(idCol));
				String playlist = cursor.getString(playlistCol);
				pl.put(id.toString(),playlist);
			} while (cursor.moveToNext());
			cursor.close();
		}
		return pl;
	}
}
