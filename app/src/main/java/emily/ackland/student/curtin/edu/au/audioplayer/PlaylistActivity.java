package emily.ackland.student.curtin.edu.au.audioplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static emily.ackland.student.curtin.edu.au.audioplayer.MyUtils.print;

/**
 * Created by Emily on 12/2/2017.
 */

public class PlaylistActivity extends AppCompatActivity
				implements NavigationView.OnNavigationItemSelectedListener{
	FloatingActionButton addNewPlFAB;
	ListView playlistsView;
	AudioDB db;
	Long playlistID;
	Map<Long, String> playlists;
	LinearLayout layout;
	PlaylistAdapter adapter;
	ArrayList<String> plStr;
	ArrayAdapter<String> arrayAdapter;
	@Override
	protected void onCreate(Bundle savedInst) {
		super.onCreate(savedInst);
		setContentView(R.layout.activity_playlists);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
						this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		plStr = new ArrayList<>();
		playlistsView = findViewById(R.id.playlist_list);
		addNewPlFAB = findViewById(R.id.add_playlistFAB);
		db = new AudioDB(this, R.integer.DATABASE_READ_WRITE_MODE);
		addNewPlFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createNewPlaylist();
			}
		});
		playlists = getPlaylists();
		arrayAdapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1,plStr);
		playlistsView.setAdapter(arrayAdapter);
		playlistsView.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
															long arg3) {

			}

		});
	}
	public void playlistPicked(View view){
		String plID = (view.getTag().toString());
		plStr.get(Integer.parseInt(plID));
		ArrayList<AudioFile> tracks = new ArrayList<>();
		tracks = MyUtils.getTracks(this,plStr.get(Integer.parseInt(plID)));
		Intent intent = new Intent(this, MainActivity.class);
		MyUtils.bundleTracks(tracks,intent,"PLAYLIST");
	}
	private void init() {
		layout = new LinearLayout(this);
		layout.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT)
		);

		playlistsView = new ListView(this);
		playlistsView.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT)
		);
		addNewPlFAB = findViewById(R.id.add_playlistFAB);
		layout.addView(addNewPlFAB);
		layout.addView(playlistsView);
		db = new AudioDB(this, R.integer.DATABASE_READ_WRITE_MODE);
		playlists=getPlaylists();
		addNewPlFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createNewPlaylist();
			}
		});
		setContentView(layout);
	}
	private void generateView(){
		for (Long s : playlists.keySet()){
			TextView plTitle = new TextView(this);
			plTitle.setLayoutParams( new LinearLayout.LayoutParams(
							 LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT
			));
			plTitle.setText(playlists.get(s));
			plTitle.setTag(s);
			playlistsView.addView(plTitle);
		}
	}
	private void createNewPlaylist() {
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.prompt_input, null);

		android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						this);

		alertDialogBuilder.setView(promptsView);

		final EditText userInput = promptsView
						.findViewById(R.id.editTextDialogUserInput);

		alertDialogBuilder
						.setCancelable(true)
						.setPositiveButton("Create Playlist",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int id) {
												Intent intent = new Intent(getBaseContext(), AddTracksToPlaylist.class);
												db = new AudioDB(getBaseContext(), R.integer.DATABASE_READ_WRITE_MODE);
												String newPlaylist = userInput.getText().toString();
												Long playlistID = db.createNewPlayList(newPlaylist);
												print("KEY "+playlistID.toString());
												intent.putExtra(AudioContract.FeedEntry.PLAYLIST_ID, Long.valueOf(playlistID));
												playlists.put(playlistID, newPlaylist);
												plStr.add(newPlaylist);
												arrayAdapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,plStr);
												playlistsView.setAdapter(arrayAdapter);
												startActivity(intent);
											}
										})
						.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int id) {
												dialog.cancel();
											}
										});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
private Map<Long,String> getPlaylists(){
		Map<Long,String> pl = new HashMap<>();
		Cursor cursor = db.query(
						AudioContract.FeedEntry.PLAYLISTS_TABLE,
						null,
						null,
						null,
						null,
						null,
						null
		);
		if (cursor!=null)
	if (cursor != null && cursor.moveToFirst()) {
		int playlistCol = cursor.getColumnIndex(AudioContract.FeedEntry.PLAYLIST_NAME);
		int idCol = cursor.getColumnIndex(AudioContract.FeedEntry._ID);
		do {
			Long id = cursor.getLong(idCol);
			String playlist = cursor.getString(playlistCol);
			pl.put(id,playlist);
			plStr.add(playlist);
		} while (cursor.moveToNext());
	}
	return pl;
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

