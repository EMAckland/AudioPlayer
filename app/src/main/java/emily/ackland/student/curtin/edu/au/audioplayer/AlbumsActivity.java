package emily.ackland.student.curtin.edu.au.audioplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
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
	private List<Album> albumList = new ArrayList<>();
	private Map<String,Bitmap> albumMap = new HashMap<>();
	private Map<String, ArrayList<AudioFile>> mapAlbumToTracks = new HashMap<>();
	private String[] permissions = new String[]{
					Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.WAKE_LOCK,
					Manifest.permission.MEDIA_CONTENT_CONTROL
	};
	private Button viewAllButton;
@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_albums);
		if(MyUtils.havePermissions(AlbumsActivity.this, this, permissions)){
			albumsView = findViewById(R.id.albums_table);
			getAudioFiles();
			generateAlbumView();
		}
	}
	private void generateAlbumView() {
		int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
		int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());

		TableRow.LayoutParams rowParams = new TableRow.LayoutParams
						(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
		viewAllButton = new Button(this);
		viewAllButton.setText("View All");
		viewAllButton.setLayoutParams(rowParams);
		viewAllButton.setTag(tracksList);
		viewAllButton.setOnClickListener(new View.OnClickListener() {
@Override
			public void onClick(View view) {
				ArrayList<AudioFile> tracks = (ArrayList<AudioFile>)view.getTag();
				Intent intent = new Intent(getBaseContext(), MainActivity.class);
				bundleTracks(tracks,intent,"VIEW ALL");
				startActivity(intent);
			}
		});
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
					ArrayList<AudioFile> tracks = ((Album)view.getTag()).getTracks();
					Intent intent = new Intent(getBaseContext(), MainActivity.class);
					bundleTracks(tracks,intent,"ALBUM");
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
		row = new TableRow(this);
		row.setLayoutParams(rowParams);
		row.addView(viewAllButton);
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
					albumBMP = MyUtils.getBitmapFromDrawable(this, R.drawable.ic_wallpaper_black_24dp);
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
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case 1:
				albumsView = findViewById(R.id.albums_table);
				getAudioFiles();
				generateAlbumView();
				break;
		}
	}
	public void bundleTracks(ArrayList<AudioFile> tracks, Intent intent, final String bundleType){
		List<Bundle> bundles = new ArrayList<>();
		for (AudioFile f : tracks){
			Bundle b = new Bundle();
			b.putString("ARTIST", f.getArtist());
			b.putString("ALBUM", f.getAlbum());
			b.putString("DURATION", f.getDuration());
			b.putString("TITLE", f.getTitle());
			b.putLong("ID", f.getID());
			b.putByteArray("ALBUMART", f.getAlbumArt().getNinePatchChunk());
			bundles.add(b);
		}
		int bundleID = 0;
		for (Bundle b : bundles) {
			intent.putExtra(bundleType + bundleID, b);
			bundleID++;
		}
	}

}
