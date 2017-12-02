package emily.ackland.student.curtin.edu.au.audioplayer;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Emily on 12/1/2017.
 */

public class MyUtils {
	public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
		Drawable drawable = ContextCompat.getDrawable(context, drawableId);

		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
			Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);

			return bitmap;
		} else {
			throw new IllegalArgumentException("unsupported drawable type");
		}
	}

	public static boolean havePermissions(Activity activity, Context ctx, String[] permissions) {
		boolean permission = false;
		if (ActivityCompat.checkSelfPermission(ctx,
						Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
						ActivityCompat.checkSelfPermission(ctx,
										Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
						ActivityCompat.checkSelfPermission(ctx,
										Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
			permission = false;
			if (ActivityCompat.shouldShowRequestPermissionRationale(
							activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
				ActivityCompat.requestPermissions(activity, permissions, 1);
			}
		} else
			permission = true;
		return permission;
	}

	public static void print(String msg) {
		Log.v("DEBUG", msg);
	}

	public static String timeInMilliSecToFormattedTimeMM_SS(int time) {
		String formattedTime;
		Log.d("time", String.valueOf(time));
		try {
			int seconds = time / 1000;
			int minutes = seconds / 60;
			seconds = seconds % 60;
			if (seconds < 10) {
				formattedTime = " " + String.valueOf(minutes) + ":0" + String.valueOf(seconds) + " ";
				Log.d("formattedTime", formattedTime);
			} else {
				formattedTime = " " + String.valueOf(minutes) + ":" + String.valueOf(seconds) + " ";
				Log.d("formattedTime", formattedTime);
			}
		} catch (NumberFormatException e) {
			formattedTime = "error";
		}
		return formattedTime;
	}

	public static Bitmap getAlbumart(Context context, Long album_id) {
		Bitmap albumArtBitMap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		try {
			final Uri sArtworkUri = Uri
							.parse("content://media/external/audio/albumart");
			Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
			ParcelFileDescriptor pfd = context.getContentResolver()
							.openFileDescriptor(uri, "r");
			if (pfd != null) {
				FileDescriptor fd = pfd.getFileDescriptor();
				albumArtBitMap = BitmapFactory.decodeFileDescriptor(fd, null,
								options);
				pfd = null;
				fd = null;
			}
		} catch (Error ee) {
		} catch (Exception e) {
		}
		return albumArtBitMap;
	}

	public static ArrayList<AudioFile> getTracks(Context ctx, String args) {
		String[] SELECTION_ARGS;
		if(args==null)
			SELECTION_ARGS = null;
		else
			SELECTION_ARGS = new String[] {"_ID" + " =?"+args};
		ArrayList<AudioFile> tracksList = new ArrayList<>();
		ContentResolver audioResolver = ctx.getContentResolver();

		Cursor audioCursor = audioResolver.query(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						null,
						null,
						SELECTION_ARGS,
						null
		);

		if (audioCursor != null && audioCursor.moveToFirst()) {
			int artistCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
			int titleCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
			int idColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media._ID);
			int durColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
			int albumCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
			int albumIDCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
			do {
				String artist = audioCursor.getString(artistCol);
				String title = audioCursor.getString(titleCol);
				long trackID = audioCursor.getLong(idColumn);
				String duration = audioCursor.getString(durColumn);
				String albumTitle = audioCursor.getString(albumCol);
				Long albumID = audioCursor.getLong(albumIDCol);
				AudioFile track = new AudioFile(trackID, title, artist, duration,
								albumTitle, albumID);
				tracksList.add(track);
			} while (audioCursor.moveToNext());
		}
		return tracksList;
	}

	public static ArrayList<AudioFile> getAlbum(Context ctx, String inAlbumID) {
		ArrayList<AudioFile> tracksList = new ArrayList<>();
		ContentResolver audioResolver = ctx.getContentResolver();
		Cursor audioCursor = audioResolver.query(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						null,
						MediaStore.Audio.Media._ID + "=" + inAlbumID,
						null,
						null
		);
		if (audioCursor != null && audioCursor.moveToFirst()) {
			int artistCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
			int titleCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
			int idColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media._ID);
			int durColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
			int albumCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
			int albumIDCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
			do {
				String artist = audioCursor.getString(artistCol);
				String title = audioCursor.getString(titleCol);
				long trackID = audioCursor.getLong(idColumn);
				String duration = audioCursor.getString(durColumn);
				String albumTitle = audioCursor.getString(albumCol);
				Long albumID = audioCursor.getLong(albumIDCol);
				AudioFile track = new AudioFile(trackID, title, artist, duration,
								albumTitle, albumID);
				tracksList.add(track);
			} while (audioCursor.moveToNext());
		}
		audioCursor.close();
		return tracksList;
	}

	public static Set<Album> getAlbums(Context ctx ) {
		Map<String, ArrayList<AudioFile>> mapAlbumToTracks = new HashMap<>();
		Set<Album> albumSet = new HashSet<>();
		ArrayList<AudioFile> tracksList = new ArrayList<>();
		Map<String,Bitmap> albumMap = new HashMap<>();
		ContentResolver audioResolver = ctx.getContentResolver();
		ContentResolver albumResolver = ctx.getContentResolver();
		Bitmap albumBMP;
		Cursor albumCursor = albumResolver.query(
						MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
						null,
						null,
						null,
						null
		);
		Cursor audioCursor = audioResolver.query(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						null,
						null,
						null,
						null
		);
		if (albumCursor != null && albumCursor.moveToFirst()) {
			int albumCol = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
			int albumArtCol = albumCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART);
			int artistCol = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
			int IDCol = albumCursor.getColumnIndex(MediaStore.Audio.Albums._ID);
			//int albumIDCol = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);
			do {
				String albumTitle = albumCursor.getString(albumCol);
				String artist = albumCursor.getString(artistCol);
				long id = albumCursor.getLong(IDCol);
				//long albumID = albumCursor.getLong(albumIDCol);
				String art = albumCursor.getString(albumArtCol);
				albumMap.put(albumTitle, MyUtils.getAlbumart(ctx,id));
				Album album = new Album(artist,albumTitle,id, id);
				albumSet.add(album);
			} while (albumCursor.moveToNext());
		}
		if (audioCursor != null && audioCursor.moveToFirst()) {
			int artistCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
			int titleCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
			int idColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media._ID);
			int durColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
			int albumCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
			int albumIDCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
			do {
				String artist = audioCursor.getString(artistCol);
				String title = audioCursor.getString(titleCol);
				long trackID = audioCursor.getLong(idColumn);
				String duration = audioCursor.getString(durColumn);
				String albumTitle = audioCursor.getString(albumCol);
				Long albumID = audioCursor.getLong(albumIDCol);
				AudioFile track = new AudioFile(trackID, title, artist, duration,
								albumTitle, albumID);
				tracksList.add(track);
				if (mapAlbumToTracks.keySet().contains(albumTitle))
					mapAlbumToTracks.get(albumTitle).add(track);
				else{
					mapAlbumToTracks.put(albumTitle, new ArrayList<AudioFile>());
					mapAlbumToTracks.get(albumTitle).add(track);
				}
			} while (audioCursor.moveToNext());
		}
		for (Album album : albumSet){
			album.setTracks(mapAlbumToTracks.get(album.getTitle()));
		}
		return albumSet;
	}
	public static int getDIP(Context ctx, int val){
		return   (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val,
						ctx.getResources().getDisplayMetrics());
	}
	public static void bundleTracks(ArrayList<AudioFile> tracks, Intent intent, final String bundleType){
		List<Bundle> bundles = new ArrayList<>();
		for (AudioFile f : tracks){
			Bundle b = new Bundle();
			b.putString("ARTIST", f.getArtist());
			b.putString("ALBUM", f.getAlbum());
			b.putString("DURATION", f.getDuration());
			b.putString("TITLE", f.getTitle());
			b.putLong("ID", f.getID());
			bundles.add(b);
		}
		int bundleID = 0;
		for (Bundle b : bundles) {
			intent.putExtra(bundleType + bundleID, b);
			bundleID++;
		}
	}
}
