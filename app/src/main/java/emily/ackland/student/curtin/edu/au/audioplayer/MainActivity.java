package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Fragment{
	final String ALBUMS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.AlbumsViewFragment";
	final String PLAYLISTS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.PlaylistActivity";
	final String ADD_TRACKS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.AddTracksToPlaylist";
	final String TRACKS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.MainActivity";
	final String BASE_ACTIVITY = ".BaseAcitivity";
	private ListView tracksView;
	private ArrayList<AudioFile> tracksList = new ArrayList<>();
	private AudioService audioSrv;
	private Intent playIntent;
	private boolean audioBound = false;
	private boolean paused = false, playbackPaused = false;
	private TracksAdapter viewAdpt;
	private SeekBar seekBar;
	private Handler seekHandler = new Handler();
	private ImageButton skip_next, skip_prev, play_pause, pause_play;
	private TextView duration, curr;
	View view;
	BaseActivity baseActivity;
	public MainActivity() {

	}

	public static Fragment newInstance(Context context) {
		AlbumsViewFragment f = new AlbumsViewFragment();
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_main, null);
		return root;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		this.view = view;
		baseActivity = (BaseActivity)getActivity();
		tracksView = view.findViewById(R.id.tracks_list);
		tracksList = baseActivity.getCurrTracks();
		viewAdpt = new TracksAdapter(getContext(), tracksList);
		tracksView.setAdapter(viewAdpt);
	}
}
