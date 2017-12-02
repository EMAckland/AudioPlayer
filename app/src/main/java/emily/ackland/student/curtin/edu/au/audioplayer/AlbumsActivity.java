package emily.ackland.student.curtin.edu.au.audioplayer;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Set;
/**
 * Created by Emily on 11/30/2017.
 */

public class AlbumsActivity extends DrawerActivity implements NavigationView.OnNavigationItemSelectedListener {
	private TableLayout albumsView;
	private Set<Album> albumSet;
	private ArrayList<AudioFile> tracks;
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
		Toolbar toolbar = super.toolbar;
		setSupportActionBar(toolbar);
		DrawerLayout drawer = super.drawer;
		ActionBarDrawerToggle toggle = super.toggle;
		drawer.addDrawerListener(toggle);
		toggle.syncState();
		NavigationView navigationView = super.navigationView;

		if(MyUtils.havePermissions(AlbumsActivity.this, this, permissions)){
			albumsView = findViewById(R.id.albums_table);
			MyUtils.getTracks(this,null);
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
		albumSet = MyUtils.getAlbums(this);

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
					MyUtils.bundleTracks(tracks,intent,"ALBUM");
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
				MyUtils.getTracks(this,null);
				generateAlbumView();
				break;
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
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		return super.onNavigationItemSelected(item);
	}
}

