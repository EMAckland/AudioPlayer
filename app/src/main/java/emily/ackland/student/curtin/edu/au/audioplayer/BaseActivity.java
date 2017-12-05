package emily.ackland.student.curtin.edu.au.audioplayer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static emily.ackland.student.curtin.edu.au.audioplayer.MyUtils.print;

public class BaseActivity extends FragmentActivity implements
				NavigationView.OnNavigationItemSelectedListener, MediaController.MediaPlayerControl {
	Manager manager;
	final String ALBUMS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.AlbumsViewFragment";
	final String PLAYLISTS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.PlaylistActivity";
	final String ADD_TRACKS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.AddTracksToPlaylist";
	final String TRACKS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.MainActivity";
	final String BASE_ACTIVITY = ".BaseAcitivity";
	Intent playIntent = null;
	private AudioService audioSrv;
	private boolean audioBound = false, isPlaylist = false;
	private boolean paused = true;
	private SeekBar seekBar;
	private Handler seekHandler = new Handler();
	private ImageButton skip_next, skip_prev, play_pause, pause_play;
	private TextView duration, curr;
	int menuID, menuDrawable;
	boolean updateMenu = false;
	TableLayout albumsView;
	Set<Album> albumSet;
	Menu menu;
	ArrayList<AudioFile> tracksList;
	ArrayList<AudioFile> currTracks;
	ArrayList<AudioFile> albumTracksList;
	ArrayList<AudioFile> playlistTracks;
	ArrayList<String> playlistsStr;
	String currPlaylist;
	FloatingActionButton fab;
	AudioDB db;
	Map<String,String> plKeystoName;

	Map<String,List<String>> plIDtoTracks;
	String fabState = BASE_ACTIVITY;
	String[] permissions = new String[]{
					Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.WAKE_LOCK,
					Manifest.permission.MEDIA_CONTENT_CONTROL
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new AudioDB(this, R.integer.DATABASE_READ_WRITE_MODE);
		plKeystoName = db.getPlaylistsFromDB();
		playlistsStr = new ArrayList<>();
		for(String s : plKeystoName.keySet())
			playlistsStr.add(s);
		setContentView(R.layout.activity_main_drawer);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		fab = (FloatingActionButton) findViewById(R.id.playlist_fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (fabState.equals(ADD_TRACKS_FRAGMENT)){
					onFABDone();
				}else
					onFABAddNewPlaylist();
			}
		});
		play_pause = findViewById(R.id.play_pause);
		pause_play = findViewById(R.id.pause_play);
		skip_next = findViewById(R.id.skip_next);
		skip_prev = findViewById(R.id.skip_prev);
		duration = findViewById(R.id.track_duration);
		curr = findViewById(R.id.current_position);
		seekBar = findViewById(R.id.seek_bar);
		seekBar.setEnabled(true);
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
						this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		tracksList = MyUtils.getTracks(this, null);
		if(playIntent==null){
			playIntent = new Intent(this, AudioService.class);
			bindService(playIntent, audioConnection, Context.BIND_IMPORTANT);
		}
		startService(playIntent);
		setController();
		init();
	}

	private void onFABAddNewPlaylist(){
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
												String newPlaylist = userInput.getText().toString();
												currPlaylist = ((Long)(db.createNewPlayList(newPlaylist))).toString();
												print("KEY "+currPlaylist);
												plKeystoName.put(currPlaylist, newPlaylist);
												playlistsStr.add(currPlaylist);
												fabState = ADD_TRACKS_FRAGMENT;
												fab.setImageResource(R.drawable.ic_done_black_24dp);
												loadNewFragmentWithBackstack(ADD_TRACKS_FRAGMENT, BASE_ACTIVITY);
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
	public void onFABDone(){
		fabState = BASE_ACTIVITY;
		getSupportFragmentManager().executePendingTransactions();
		fab.setImageResource(R.drawable.ic_playlist_add_black_36dp);
		((AddTracksToPlaylist)(getSupportFragmentManager().findFragmentByTag(ADD_TRACKS_FRAGMENT))).savePlTracks();
		getSupportFragmentManager().popBackStack();

	}
	public String getCurrPlaylist(){
		return currPlaylist;
	}
	public void setTracksSource(String source){
		switch (source) {
			case ALBUMS_FRAGMENT:
				isPlaylist = false;
			case PLAYLISTS_FRAGMENT:
				isPlaylist = true;
		}
	}
	public void loadNewFragment(String fragment){
		FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
		tx.replace(R.id.main_drawer_fram, Fragment.instantiate(BaseActivity.this,
						fragment), fragment);
		tx.commit();
	}
	public void loadNewFragmentWithBackstack(String fragment, String backstack){
		FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
		tx.replace(R.id.main_drawer_fram, Fragment.instantiate(BaseActivity.this, fragment),
						fragment).addToBackStack(backstack).commit();
	}
	public void changeToolbarState(int id, int drawable){
		updateMenu = true;
		menuID = id;
		menuDrawable = drawable;
		invalidateOptionsMenu();
	}

	private ServiceConnection audioConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			AudioService.AudioBinder binder = (AudioService.AudioBinder)service;
			audioSrv = binder.getService();
			audioBound = true;
			audioSrv.setTracks(tracksList);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			audioBound = false;
		}
	};

	@Override
	public void onStart() {
		super.onStart();
		if (playIntent == null) {
			playIntent = new Intent(this, AudioService.class);
			bindService(playIntent, audioConnection, Context.BIND_IMPORTANT);
		}
	}

	private void init() {
		if (MyUtils.havePermissions(BaseActivity.this, this, permissions)) {
			albumSet = MyUtils.getAlbums(this);
			loadNewFragment(ALBUMS_FRAGMENT);
		}
	}
	public ArrayList<AudioFile> getCurrTracks(){
		if(isPlaylist)
			return playlistTracks;
		else
			return albumTracksList;
	}
	public void setCurrPlaylist(String inPlaylist){
		currPlaylist = inPlaylist;
		if (playlistTracks!=null)
			playlistTracks.clear();
		else
			playlistTracks = new ArrayList<>();
		List<String> plStr = db.getPlaylistFromDB(new String[]{inPlaylist});
		for (String s : plStr){
			for (AudioFile t : tracksList){
				if(((Long)t.getID()).toString().equals(s)){
					playlistTracks.add(t);
				}
			}
		}
	}
	public void finishedSelectingPLTracks(View view) {
		ArrayList<AudioFile> plTracks =
						((AddTracksToPlaylist)(getSupportFragmentManager().findFragmentByTag(ADD_TRACKS_FRAGMENT))).getNewPlaylistTracks();
		for(AudioFile t:plTracks){
			db.insertIntoPlayList(currPlaylist, ((Long)t.getID()).toString());
		}

		updateMenu = false;
		invalidateOptionsMenu();
		getSupportFragmentManager().popBackStack();
	}
	public Map<String,String> getPlaylists(){
		return plKeystoName;
	}
	@Override
	protected void onPause() {
		super.onPause();
		seekHandler.removeCallbacks(run);
		updateView();
		paused = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (paused) {
			paused = false;
			updateSeek();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		stopService(playIntent);
		audioSrv = null;
		seekHandler.removeCallbacks(run);
		super.onDestroy();
	}

	@Override
	public void pause() {
		paused = true;
		audioSrv.pausePlayer();
	}

	@Override
	public int getDuration() {
		return Integer.parseInt(audioSrv.getCurrentTrack().getDuration());
	}

	@Override
	public int getCurrentPosition() {
		if (audioSrv != null && audioBound && audioSrv.isPlaying())
			return audioSrv.getPositon();
		else return 0;
	}

	@Override
	public void start() {
		audioSrv.go();
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean isPlaying() {
		return false;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public boolean canSeekBackward() {
		return false;
	}

	@Override
	public boolean canSeekForward() {
		return false;
	}

	@Override
	public void seekTo(int pos) {
		audioSrv.seek(pos);
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	private void updateSeek() {

		seekBar.setProgress(getCurrentPosition());
		curr.setText(MyUtils.timeInMilliSecToFormattedTimeMM_SS(getCurrentPosition()));
		seekHandler.postDelayed(run, 1000);
	}

	private void updateView() {
		seekBar.setMax(getDuration());
		if (paused){
			play_pause.setImageResource(R.drawable.ic_pause_black_24dp);
		}else
			play_pause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
		duration.setText(MyUtils.timeInMilliSecToFormattedTimeMM_SS(getDuration()));
		curr.setText(R.string.track_start);
	}

	private void playNext() {
		audioSrv.playNext();
		if (paused) {
			paused = false;
		}
	}


	@Override
	public void onBackPressed() {
		int count = getFragmentManager().getBackStackEntryCount();
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else if (count == 0){
			super.onBackPressed();
		}else {
			getFragmentManager().popBackStack();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(updateMenu) {
			menu.findItem(menuID).setIcon(menuDrawable);
		}
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
		int id = item.getItemId();
		if (id == R.id.nav_playlists) {
			loadNewFragmentWithBackstack(PLAYLISTS_FRAGMENT, BASE_ACTIVITY);
		} else if (id == R.id.nav_manage) {

		} else if (id == R.id.nav_albums) {
			loadNewFragmentWithBackstack(ALBUMS_FRAGMENT, BASE_ACTIVITY);
		}
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	public Manager getManager() {
		return manager;
	}

	public void setAlbumTracks(View view, ArrayList<AudioFile> tracks) {
		albumTracksList = tracks;
	}

	public void setPlaylistTracks(ArrayList<AudioFile> tracks) {
		playlistTracks = tracks;
	}

	public ArrayList<AudioFile> getAlbumTracks() {
		return albumTracksList;
	}

	public void setController() {
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					seekTo(progress);
					updateSeek();
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				seekHandler.removeCallbacks(run);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				seekHandler.postDelayed(run, 1000);
			}
		});
		play_pause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (paused){
					audioSrv.play();
					updateSeek();
					updateView();
					paused=false;
				}else {
					audioSrv.pausePlayer();
					updateView();
					paused=true;
				}
			}
		});
		skip_prev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				audioSrv.playPrev();
				updateView();
				updateSeek();
			}
		});
		skip_next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				audioSrv.playNext();
				updateSeek();
				updateView();
			}
		});
	}

	private void playPrev() {
		audioSrv.playPrev();
		if (paused) {
			paused = false;
		}
	}

	Runnable run = new Runnable() {
		@Override
		public void run() {
			if(audioSrv==null){
				seekHandler.removeCallbacks(run);
			}
			else if (!paused && audioSrv.isPlaying() && audioBound) {
				updateSeek();
			} else {
				seekHandler.removeCallbacks(run);
			}
		}
	};


	public ArrayList<AudioFile> getAudioFiles() {
		return tracksList;
	}
	public void trackPicked(View view) {
		audioSrv.setTrack(Integer.parseInt(view.getTag().toString()));
		audioSrv.playAudio();
		if (paused) {
			paused = false;
		}
		updateView();
		updateSeek();

	}
}
