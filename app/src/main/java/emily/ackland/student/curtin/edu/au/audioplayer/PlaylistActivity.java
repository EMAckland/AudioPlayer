package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Emily on 12/2/2017.
 */

public class PlaylistActivity extends Fragment {
	final String ALBUMS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.AlbumsViewFragment";
	final String PLAYLISTS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.PlaylistActivity";
	final String ADD_TRACKS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.AddTracksToPlaylist";
	final String TRACKS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.MainActivity";
	final String BASE_ACTIVITY = ".BaseAcitivity";
	ListView playlistsView;
	AudioDB db;
	Long playlistID;
	//Map<Long, String> playlists;
	LinearLayout layout;
	BaseAdapter adapter;
	ArrayList<String> playlistnames;
	ArrayList<String> playlistskeys;
	Map<String,String> plKeystoname;
	ArrayAdapter<String> arrayAdapter;

	public PlaylistActivity() {

	}

	public static Fragment newInstance(Context context) {
		PlaylistActivity f = new PlaylistActivity();
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
		plKeystoname = ((BaseActivity) (getActivity())).getPlaylists();
		playlistskeys = new ArrayList<>();
		playlistnames = new ArrayList<>();
		for(String s : plKeystoname.keySet()){
			playlistnames.add(plKeystoname.get(s));
			playlistskeys.add(s);
		}

		playlistsView = view.findViewById(R.id.playlist_list);
		arrayAdapter = new ArrayAdapter<String>(getContext(),
						android.R.layout.simple_list_item_1, playlistnames);
		playlistsView.setAdapter(arrayAdapter);
		playlistsView.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
				((BaseActivity)getActivity()).setCurrPlaylist((playlistskeys.get(pos)));
				((BaseActivity)getActivity()).setTracksSource(true);
				FragmentTransaction tx = getFragmentManager().beginTransaction();
				tx.replace(R.id.main_drawer_fram, Fragment.instantiate(getContext(),
								TRACKS_FRAGMENT),
								TRACKS_FRAGMENT).addToBackStack(PLAYLISTS_FRAGMENT).commit();
			}
		});
	}
}
/*	public void playlistPicked(View view){
		String plID = (view.getTag().toString());
		plStr.get(Integer.parseInt(plID));
		ArrayList<AudioFile> tracks = new ArrayList<>();
		tracks = MyUtils.getTracks(getContext(),plStr.get(Integer.parseInt(plID)));
		Intent intent = new Intent(getContext(), MainActivity.class);
		MyUtils.bundleTracks(tracks,intent,"PLAYLIST");
	}*/



/*	private void init() {
		layout = new LinearLayout(getContext());
		layout.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT)
		);

		playlistsView = new ListView(getContext());
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
			TextView plTitle = new TextView(getContext());
			plTitle.setLayoutParams( new LinearLayout.LayoutParams(
							 LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT
			));
			plTitle.setText(playlists.get(s));
			plTitle.setTag(s);
			playlistsView.addView(plTitle);
		}
	}*/