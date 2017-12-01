package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Emily on 11/30/2017.
 */

public class AlbumsActivity extends AppCompatActivity {
	private TableLayout albumsView;
	private Set<Album> albumSet = new HashSet<>();
	private ArrayList<AudioFile> tracksList = new ArrayList<>();
	private List<String> albums = new ArrayList<>();
	private List<Album> albumList = new ArrayList<>();
	private Map<String,Bitmap> albumMap = new HashMap<>();
	private Map<String, ArrayList<AudioFile>> mapAlbumtoTracks = new HashMap<>();
	private List<String> artists = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_albums);
		albumsView = findViewById(R.id.albums_table);
		getAudioFiles();
		generateAlbumView();
	}

	private void generateAlbumView() {
		int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
		int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
		TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams
						(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
		TableRow.LayoutParams rowParams = new TableRow.LayoutParams
						(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
		Iterator<Album> iterator = albumList.iterator();
		TableRow row = new TableRow(this);
		row.setLayoutParams(rowParams);
		int rowIdx = 0;

		for (Album a : albumSet) {
			ImageButton albumArt = new ImageButton(this);
			albumArt.setLayoutParams(new TableRow.LayoutParams(width, height));
			albumArt.setBackgroundColor(111);
			albumArt.setAdjustViewBounds(true);
			albumArt.setTag(a);
			albumArt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Album album = (Album) view.getTag();
					List<Bundle> bundles = new ArrayList<>();
					for (AudioFile f : album.getTracks()) {
						Bundle b = new Bundle();
						b.putString("ARTIST", f.getArtist());
						b.putString("ALBUM", f.getAlbum());
						b.putString("DURATION", f.getDuration());
						b.putString("TITLE", f.getTitle());
						b.putLong("ID", f.getID());
						b.putByteArray("ALBUMART", f.getAlbumArt().getNinePatchChunk());
						bundles.add(b);
					}
					Intent intent = new Intent(getBaseContext(), MainActivity.class);
					int bundleID = 0;
					for (Bundle b : bundles) {
						intent.putExtra("ALBUM" + bundleID, b);
						bundleID++;
					}
					startActivity(intent);
				}
			});
			albumArt.setImageBitmap(a.getAlbumArt());
			row.addView(albumArt);
			rowIdx++;
			if (rowIdx == 3) {
				albumsView.addView(row);
				rowIdx = 0;
				row = new TableRow(this);
				row.setLayoutParams(rowParams);
			}
		}
		albumsView.addView(row);
	}

	private void getAudioFiles() {
		ContentResolver audioResolver = getContentResolver();
		ContentResolver albumResolver = getContentResolver();
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
			do {
				String albumTitle = albumCursor.getString(albumCol);
				String albumPath = albumCursor.getString(albumArtCol);
				String artist = albumCursor.getString(artistCol);
				if (albumPath != null)
					albumBMP = BitmapFactory.decodeFile(albumPath);
				else {
					albumBMP = getBitmapFromDrawable(this, R.drawable.ic_wallpaper_black_24dp);
				}
				albumMap.put(albumTitle, albumBMP);
				Album album = new Album(artist,albumTitle,albumBMP);
				albumSet.add(album);
			} while (albumCursor.moveToNext());
		}
		if (audioCursor != null && audioCursor.moveToFirst()) {
			int artistCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
			int titleCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
			int idColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media._ID);
			int durColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
			int albumCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
			do {
				String artist = audioCursor.getString(artistCol);
				String title = audioCursor.getString(titleCol);
				long trackID = audioCursor.getLong(idColumn);
				String duration = audioCursor.getString(durColumn);
				String albumTitle = audioCursor.getString(albumCol);
				AudioFile track = new AudioFile(trackID, title, artist, duration,
								albumTitle, albumMap.get(albumTitle));
				tracksList.add(track);
				if (mapAlbumtoTracks.keySet().contains(albumTitle))
					mapAlbumtoTracks.get(albumTitle).add(track);
				else{
					mapAlbumtoTracks.put(albumTitle, new ArrayList<AudioFile>());
					mapAlbumtoTracks.get(albumTitle).add(track);
				}
			} while (audioCursor.moveToNext());
		}
		for (Album album : albumSet){
			album.setTracks(mapAlbumtoTracks.get(album.getTitle()));
		}
	}

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

	public static void print(String msg) {
		Log.v("DEBUG", msg);
	}
}
