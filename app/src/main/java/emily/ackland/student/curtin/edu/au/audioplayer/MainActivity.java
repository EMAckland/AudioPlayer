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
	private ListView tracksView;
	private ArrayList<AudioFile> tracksList = new ArrayList<>();
	private AudioService audioSrv;
	private Intent playIntent;
	private boolean audioBound = false;
	private boolean paused = false, playbackPaused = false;
	private AudioAdapter viewAdpt;
	private SeekBar seekBar;
	private Handler seekHandler = new Handler();
	private ImageButton skip_next, skip_prev, play_pause, pause_play;
	private TextView duration, curr;
	View view;
	BaseActivity baseActivity;
	public MainActivity() {

	}

	public static Fragment newInstance(Context context) {
		MainActivity f = new MainActivity();
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
		tracksList = baseActivity.getAlbumTracks();
		viewAdpt = new AudioAdapter(getContext(), tracksList);
		tracksView.setAdapter(viewAdpt);

	}


	public void trackPicked(View view) {
		baseActivity.onTrackPicked(view);
	}
}
