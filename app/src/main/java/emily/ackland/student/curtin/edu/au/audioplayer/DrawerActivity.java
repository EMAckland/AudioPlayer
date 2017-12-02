package emily.ackland.student.curtin.edu.au.audioplayer;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DrawerActivity extends AppCompatActivity
				implements NavigationView.OnNavigationItemSelectedListener {
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
		setContentView(R.layout.activity_drawer);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
						this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		init();
	}
	private void init(){

		if(MyUtils.havePermissions(DrawerActivity.this, this, permissions)){
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
	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.drawer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_playlists) {
			Intent intent = new Intent(this, PlaylistActivity.class);
			startActivity(intent);
		} else if (id == R.id.nav_manage) {


		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
