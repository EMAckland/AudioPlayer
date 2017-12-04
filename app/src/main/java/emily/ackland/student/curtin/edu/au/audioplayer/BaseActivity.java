package emily.ackland.student.curtin.edu.au.audioplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public  class BaseActivity extends FragmentActivity implements
				NavigationView.OnNavigationItemSelectedListener, MediaController.MediaPlayerControl{
	Manager manager;
	final String ALBUMS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.AlbumsViewFragment";
	final String PLAYLISTS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.PlaylistActivity";
	final String ADD_TRACKS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.AddTracksToPlaylist";
	final String TRACKS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.MainActivity";
	Intent playIntent;
	private AudioService audioSrv;
	private boolean audioBound = false;
	private boolean paused = false, playbackPaused = false;
	private SeekBar seekBar;
	private Handler seekHandler = new Handler();
	private ImageButton skip_next, skip_prev, play_pause, pause_play;
	private TextView duration, curr;
	AudioService audioService;
		 TableLayout albumsView;
		 Set<Album> albumSet;
		 ArrayList<AudioFile> tracksList;
		ArrayList<AudioFile> albumTracksList;
		ArrayList<AudioFile> playlistTracks;
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
			init();
			if (playIntent == null) {
				playIntent = new Intent(this, AudioService.class);
				bindService(playIntent, audioConnection, Context.BIND_AUTO_CREATE);
				startService(playIntent);
			}
		}
	private void init() {
		manager = new Manager(this);
		if (MyUtils.havePermissions(BaseActivity.this, this, permissions)) {
			setController();
			albumSet = MyUtils.getAlbums(this);
			tracksList = MyUtils.getTracks(this, null);
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace(R.id.main_drawer_fram, Fragment.instantiate(BaseActivity.this,
							ALBUMS_FRAGMENT), ALBUMS_FRAGMENT);
			tx.commit();
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
			tx.replace(R.id.main_drawer_fram, Fragment.instantiate(BaseActivity.this, PLAYLISTS_FRAGMENT),PLAYLISTS_FRAGMENT);
			tx.commit();

		} else if (id == R.id.nav_manage) {


		} else if (id == R.id.nav_albums) {
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace(R.id.main_drawer_fram, Fragment.instantiate(BaseActivity.this, ALBUMS_FRAGMENT),ALBUMS_FRAGMENT);
			tx.commit();

		}
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	public Manager getManager(){
		return manager;
	}
	public void setAlbumTracks(View view, ArrayList<AudioFile> tracks){
		albumTracksList = tracks;
		FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
		tx.replace(R.id.main_drawer_fram, Fragment.instantiate(BaseActivity.this, TRACKS_FRAGMENT),TRACKS_FRAGMENT);
		tx.commit();
	}
	public void setPlaylistTracks(ArrayList<AudioFile> tracks){
		playlistTracks = tracks;
	}
	public ArrayList<AudioFile> getAlbumTracks(){
		return albumTracksList;
	}
	private ServiceConnection audioConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
			//get service
			audioSrv = binder.getService();
			//pass list
			audioSrv.setTracks(tracksList);
			audioBound = true;
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			audioBound = false;
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		seekHandler.removeCallbacks(run);
		paused = true;
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (paused) {
			updateSeek();
			paused = false;
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
		playbackPaused = true;
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
		duration.setText(MyUtils.timeInMilliSecToFormattedTimeMM_SS(getDuration()));
		curr.setText(R.string.track_start);
	}

	private void playNext() {
		audioSrv.playNext();
		if (playbackPaused) {
			playbackPaused = false;
		}
	}
		public void setController() {
			play_pause = findViewById(R.id.play_pause);
			pause_play = findViewById(R.id.pause_play);
			skip_next = findViewById(R.id.skip_next);
			skip_prev = findViewById(R.id.skip_prev);
			duration = findViewById(R.id.track_duration);
			curr = findViewById(R.id.current_position);
			seekBar = findViewById(R.id.seek_bar);
			seekBar.setEnabled(true);
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
					audioSrv.play();
					updateSeek();
				}
			});
			pause_play.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					audioSrv.pausePlayer();
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
	Runnable run = new Runnable() {
		boolean stopped = true;

		@Override
		public void run() {
			setStopped(false);
			if (!paused && audioSrv.isPlaying()) {
				updateSeek();
			} else {
				seekHandler.removeCallbacks(run);
			}
		}

		public void setStopped(boolean inStopped) {
			stopped = inStopped;
		}

		public boolean isStopped() {
			return stopped;
		}

		public void stop() {
			setStopped(true);
		}
	};
	private void playPrev() {
		audioSrv.playPrev();
		if (playbackPaused) {
			playbackPaused = false;
		}
	}

	public void onTrackPicked(View view) {
		audioSrv.setTrack(Integer.parseInt(view.getTag().toString()));
		audioSrv.playAudio();
		if (playbackPaused) {
			playbackPaused = false;
		}
		updateView();
		updateSeek();
	}
	public ArrayList<AudioFile> getAudioFiles(){
		return tracksList;
	}
}
