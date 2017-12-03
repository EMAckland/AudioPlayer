package emily.ackland.student.curtin.edu.au.audioplayer;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emily on 12/2/2017.
 */

public class AddTracksToPlaylist extends Fragment{
	private ArrayList<AudioFile> tracksList;
	private String playlist;
	private PlaylistAdapter viewAdpt;
	private ListView tracksListView;
	private CheckBox checkBox;
	private ArrayList<AudioFile> plTracks;
	private Button doneButton;
	private AudioDB db;
	private String plID;

	public AddTracksToPlaylist(){

	}
	public static Fragment newInstance(Context context) {
		AddTracksToPlaylist f = new AddTracksToPlaylist();
		return f;
	}
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_playlists, null);
		return root;
	}
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

		db = new AudioDB(getContext(), R.integer.DATABASE_READ_WRITE_MODE);
		init(view);
	}
	private void init(View view) {
		//plID =((Bundle) (getIntent().getExtras())).getString(AudioContract.FeedEntry.TRACK_ID);
		//playlist = getIntent().getExtras().getString("PLAYLIST NAME");
		tracksListView=view.findViewById(R.id.checkable_list);
		tracksList = MyUtils.getTracks(getContext(),null);
		viewAdpt = new PlaylistAdapter(getContext(), tracksList);
		tracksListView.setAdapter(viewAdpt);
		doneButton = view.findViewById(R.id.donebutton);
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
		FragmentManager manager = getFragmentManager();
		manager.popBackStack();
	}
}
