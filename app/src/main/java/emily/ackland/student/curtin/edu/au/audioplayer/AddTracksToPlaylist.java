package emily.ackland.student.curtin.edu.au.audioplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Emily on 12/2/2017.
 */

public class AddTracksToPlaylist extends AppCompatActivity {
	private ArrayList<AudioFile> tracksList;
	private String playlist;
	private AudioAdapter viewAdpt;
	private ListView tracksView;

	protected void onCreate(Bundle savedInst) {
		super.onCreate(savedInst);
		setContentView(R.layout.activity_playlists);
		init();
	}
	private void init() {
		playlist = getIntent().getExtras().getString("PLAYLIST NAME");
		tracksList = MyUtils.getTracks(this);
		viewAdpt = new AudioAdapter(this, tracksList);
		tracksView = findViewById(R.id.tracks_list);
		tracksView.setAdapter(viewAdpt);
	}
}
