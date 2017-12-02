package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Emily on 12/2/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
	public static final String pragma = "PRAGMA foreign_keys=ON;";
	public static final String PLAYLISTS_TABLE = AudioContract.FeedEntry.PLAYLISTS_TABLE ;
	public static final String PLAYLIST_TRACKS_TABLE = AudioContract.FeedEntry.PLAYLIST_TRACKS_TABLE ;
	public static final String TRACKS_TABLE = AudioContract.FeedEntry.TRACKS_TABLE ;
	public static final String ALBUM_TABLE = AudioContract.FeedEntry.ALBUM_TABLE ;
	public static final String _ID = AudioContract.FeedEntry._ID ;
	public static final String ALBUM_ID = AudioContract.FeedEntry.ALBUM_ID ;
	public static final String PLAYLIST_ID = AudioContract.FeedEntry.PLAYLIST_ID ;
	public static final String TRACK_ID = AudioContract.FeedEntry.TRACK_ID ;
	public static final String PLAYLIST_NAME = AudioContract.FeedEntry.PLAYLIST_NAME ;
	public static final String TRACK = AudioContract.FeedEntry.TRACK ;
	public static final String ARTIST = AudioContract.FeedEntry.ARTIST ;
	public static final String DURATION = AudioContract.FeedEntry.DURATION  ;
	public static final String ALBUM = AudioContract.FeedEntry.ALBUM ;
	public static final String ALBUM_ART = AudioContract.FeedEntry.ALBUM_ART ;
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "AudioPlayerTest.db";
	private static DataBaseHelper instance;

/*	public static final String SQL_CREATE_ALBUM_ENTRIES =
					"CREATE TABLE " + ALBUM_TABLE + " (" +
									AudioContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
									AudioContract.FeedEntry.TRACK_ID + " INTEGER FOREIGN KEY," +
									AudioContract.FeedEntry.ALBUM + " TEXT)";

	public static final String SQL_CREATE_TRACK_ENTRIES =
					"CREATE TABLE " + TRACKS_TABLE + " (" +
									AudioContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
									AudioContract.FeedEntry.TRACK + " TEXT," +
									AudioContract.FeedEntry.ARTIST + " TEXT,"+
									AudioContract.FeedEntry.DURATION + " TEXT,"+
									AudioContract.FeedEntry.DURATION + " TEXT)";*/

	public static final String SQL_CREATE_PLAYLIST_ENTRIES =
					"CREATE TABLE " + PLAYLISTS_TABLE + " (" +
									AudioContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
									AudioContract.FeedEntry.PLAYLIST_NAME + " TEXT)";

	public static final String SQL_CREATE_PLAYLIST_TRACKS_ENTRIES =
					"CREATE TABLE " + PLAYLIST_TRACKS_TABLE + " (" +
									AudioContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
									AudioContract.FeedEntry.TRACK_ID + " TEXT, " +
									AudioContract.FeedEntry.PLAYLIST_ID + " INTEGER,"+
									"FOREIGN KEY("+ PLAYLIST_ID +")" +
									" REFERENCES " +  PLAYLISTS_TABLE + "(_ID))";

	public static synchronized DataBaseHelper getHelper(Context context) {
		if (instance == null)
			instance = new DataBaseHelper(context);
		return instance;
	}
	private DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_PLAYLIST_ENTRIES);
		db.execSQL(SQL_CREATE_PLAYLIST_TRACKS_ENTRIES);
	/*	db.execSQL(SQL_CREATE_ALBUM_ENTRIES);
		db.execSQL(SQL_CREATE_TRACK_ENTRIES);*/
	}
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL(pragma);
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		final String  SQL_DELETE_PLAYLIST_TRACK_ENTRIES =
						"DROP TABLE IF EXISTS " + AudioContract.FeedEntry.PLAYLIST_TRACKS_TABLE;
		final String  SQL_DELETE_PLAYLIST_ENTRIES =
						"DROP TABLE IF EXISTS " + AudioContract.FeedEntry.PLAYLISTS_TABLE;

		db.execSQL(SQL_DELETE_PLAYLIST_ENTRIES);
		db.execSQL(SQL_DELETE_PLAYLIST_TRACK_ENTRIES);
		onCreate(db);
	}
	public void wipeAll(SQLiteDatabase db){
		final String  SQL_DELETE_PLAYLIST_TRACK_ENTRIES =
						"DROP TABLE IF EXISTS " + AudioContract.FeedEntry.PLAYLIST_TRACKS_TABLE;
		final String  SQL_DELETE_PLAYLIST_ENTRIES =
						"DROP TABLE IF EXISTS " + AudioContract.FeedEntry.PLAYLISTS_TABLE;

		db.execSQL(SQL_DELETE_PLAYLIST_ENTRIES);
		db.execSQL(SQL_DELETE_PLAYLIST_TRACK_ENTRIES);
		onCreate(db);

	}
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}





