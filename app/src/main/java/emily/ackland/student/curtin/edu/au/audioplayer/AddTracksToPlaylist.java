package emily.ackland.student.curtin.edu.au.audioplayer;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emily on 12/2/2017.
 */

public class AddTracksToPlaylist extends Fragment{
	final String ALBUMS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.AlbumsViewFragment";
	final String PLAYLISTS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.PlaylistActivity";
	final String ADD_TRACKS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.AddTracksToPlaylist";
	final String TRACKS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.MainActivity";
	final String BASE_ACTIVITY = ".BaseAcitivity";
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
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.select_tracks_activity, null);
		return root;
	}
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		db = new AudioDB(getContext(), R.integer.DATABASE_READ_WRITE_MODE);

		init(view);
	}
	private void init(View view) {
		tracksListView=view.findViewById(R.id.checkable_list);
		tracksList = MyUtils.getTracks(getContext(),null);
		viewAdpt = new PlaylistAdapter(getContext(), tracksList);
		tracksListView.setAdapter(viewAdpt);
	}
	public void savePlTracks(){
		plTracks = viewAdpt.getSelectedTracks();
		plID = ((BaseActivity)getActivity()).getCurrPlaylist();
		for (AudioFile t : plTracks){
			MyUtils.print(t.getTitle());
		}
		Map<String,String> plMap = new HashMap<>();
		for(AudioFile t:plTracks){
			db.insertIntoPlayList(plID, ((Long)t.getID()).toString());
		}
		FragmentManager manager = getFragmentManager();

	}
	public ArrayList<AudioFile> getNewPlaylistTracks(){
		return viewAdpt.getSelectedTracks();
	}
}
