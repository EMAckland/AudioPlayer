package emily.ackland.student.curtin.edu.au.audioplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emily on 12/2/2017.
 */

public class AddTracksToPlaylist extends AppCompatActivity {
	private ArrayList<AudioFile> tracksList;
	private String playlist;
	private AudioAdapter viewAdpt;
	private ListView tracksListView;
	private CheckBox checkBox;
	private ArrayList<AudioFile> plTracks;
	private Button doneButton;
	private AudioDB db;
	private String plID;
	protected void onCreate(Bundle savedInst) {
		super.onCreate(savedInst);
		setContentView(R.layout.select_tracks_activity);
		db = new AudioDB(this, R.integer.DATABASE_READ_WRITE_MODE);
		init();
	}
	private void init() {
		plID =((Bundle) (getIntent().getExtras())).getString(AudioContract.FeedEntry.TRACK_ID);
		//playlist = getIntent().getExtras().getString("PLAYLIST NAME");
		tracksListView=findViewById(R.id.checkable_list);
		tracksList = MyUtils.getTracks(this);
		viewAdpt = new AudioAdapter(this, tracksList);
		tracksListView.setAdapter(viewAdpt);
		doneButton = findViewById(R.id.donebutton);
		doneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				plTracks = viewAdpt.getSelectedTracks();
				savePlTracks();
			}
		});
	}
	private void savePlTracks(){
		Map<String,String> plMap = new HashMap<>();
		for(AudioFile t:plTracks){
			plMap.put(AudioContract.FeedEntry.PLAYLIST_ID, plID);
			plMap.put(AudioContract.FeedEntry.TRACK_ID, plID);
		}
		db.insertIntoPlayList(plMap);
		super.onBackPressed();
	}
}
