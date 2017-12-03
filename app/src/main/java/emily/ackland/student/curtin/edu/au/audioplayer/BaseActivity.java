package emily.ackland.student.curtin.edu.au.audioplayer;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;

import java.util.ArrayList;
import java.util.Set;

public abstract class BaseActivity extends FragmentActivity implements FragmentActivityInterface,
				NavigationView.OnNavigationItemSelectedListener{

	public abstract void onAlbumSelected(Context ctx, View view);
		  final String ALBUMS_FRAGMENT = "ALBUMS_FRAGMENT";
		  final String PLAYLISTS_FRAGMENT = "PLAYLISTS_FRAGMENT";
		  final String ADD_TRACKS_FRAGMENT = "ADD_TRACKS_FRAGMENT";
		 TableLayout albumsView;
		 Set<Album> albumSet;
		 ArrayList<AudioFile> tracks;
		 String[] permissions = new String[]{
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE,
						Manifest.permission.WAKE_LOCK,
						Manifest.permission.MEDIA_CONTENT_CONTROL
		};
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main_drawer);
			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.playlist_fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Snackbar.make(view, ".AddTracksToPlaylists", Snackbar.LENGTH_LONG)
									.setAction("AddTracksToPlaylists", null).show();
				}
			});

			DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
			ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
							this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
			drawer.addDrawerListener(toggle);
			toggle.syncState();
			NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
			navigationView.setNavigationItemSelectedListener(this);
			init();
		}
		@Override
		public void onStart(){
			super.onStart();
			if(MyUtils.havePermissions(BaseActivity.this,this, permissions)){
				FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
				tx.replace(R.id.main_drawer_fram, Fragment.instantiate(BaseActivity.this,
								ALBUMS_FRAGMENT));
				tx.commit();
			}
			init();
		}
	private void init(){
		albumSet = MyUtils.getAlbums(this);
		tracks = MyUtils.getTracks(this,null);
		FragmentManager manager = getSupportFragmentManager();

		Fragment albumsFragment = (AlbumsViewFragment)
						getSupportFragmentManager().findFragmentByTag(ALBUMS_FRAGMENT);
		if (albumsFragment != null && albumsFragment.isVisible()) {
			albumsFragment.getView().findViewById(R.id.al)
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
		getMenuInflater().inflate(R.menu.main_drawer, menu);
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
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace(R.id.main_drawer_fram, Fragment.instantiate(MainDrawerActivity.this, "emily.ackland.student.curtin.edu.au.audioplayer.PlaylistActivity"));
			tx.commit();

		} else if (id == R.id.nav_manage) {


		} else if (id == R.id.nav_albums) {
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace(R.id.main_drawer_fram, Fragment.instantiate(MainDrawerActivity.this, "emily.ackland.student.curtin.edu.au.audioplayer.AlbumsViewFragment"));
			tx.commit();

		}
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
	}

}