package emily.ackland.student.curtin.edu.au.audioplayer;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Created by Emily on 11/30/2017.
 */

public class AlbumsActivity extends AppCompatActivity {
	private TableLayout albumsView;
	private Set<Album> albumSet;
	private String[] permissions = new String[]{
					Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.WAKE_LOCK,
					Manifest.permission.MEDIA_CONTENT_CONTROL
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_albums);
		if(MyUtils.havePermissions(AlbumsActivity.this, this, permissions)){
			albumsView = findViewById(R.id.albums_table);
			MyUtils.getTracks(this);
			generateAlbumView();
		}
	}
	private void generateAlbumView() {
		int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120,
						getResources().getDisplayMetrics());
		int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120,
						getResources().getDisplayMetrics());
		TableRow.LayoutParams rowParams = new TableRow.LayoutParams
						(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
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
			albumArt.setImageBitmap(a.getAlbumArt(this));
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
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case 1:
				albumsView = findViewById(R.id.albums_table);
				MyUtils.getTracks(this);
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
			b.putByteArray("ALBUMART", f.getAlbumArt(this).getNinePatchChunk());
			bundles.add(b);
		}
		int bundleID = 0;
		for (Bundle b : bundles) {
			intent.putExtra(bundleType + bundleID, b);
			bundleID++;
		}
	}
	private void createDb(){
		try {
			AudioDB db = new AudioDB(this, R.integer.DATABASE_READ_WRITE_MODE);
		}catch (Exception e){

		}
		for(Album a : albumSet){
			for (AudioFile t : a.getTracks()){
				Map<String,String> map = new HashMap<>();
				map.put(DataBaseHelper._ID, String.valueOf(t.getID()));
				map.put(DataBaseHelper.TRACK, t.getTitle());
				map.put(DataBaseHelper.ARTIST, t.getArtist());
				map.put(DataBaseHelper.ALBUM, t.getAlbum());
				map.put(DataBaseHelper.DURATION, t.getDuration());

			}
		}
	}
}

