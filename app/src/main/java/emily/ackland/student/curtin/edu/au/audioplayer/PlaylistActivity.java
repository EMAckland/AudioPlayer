package emily.ackland.student.curtin.edu.au.audioplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static emily.ackland.student.curtin.edu.au.audioplayer.MyUtils.print;

/**
 * Created by Emily on 12/2/2017.
 */

public class PlaylistActivity extends Fragment {
	FloatingActionButton addNewPlFAB;
	ListView playlistsView;
	AudioDB db;
	Long playlistID;
	Map<Long, String> playlists;
	LinearLayout layout;
	PlaylistAdapter adapter;
	ArrayList<String> plStr;
	ArrayAdapter<String> arrayAdapter;
	public PlaylistActivity(){

	}
	public static Fragment newInstance(Context context) {
		AlbumsViewFragment f = new AlbumsViewFragment();
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
		plStr = new ArrayList<>();
		addNewPlFAB = view.findViewById(R.id.add_playlistFAB);
		playlistsView = (ListView) view.findViewById(R.id.playlist_list);
		db = new AudioDB(getContext(), R.integer.DATABASE_READ_WRITE_MODE);
		addNewPlFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createNewPlaylist();
			}
		});
		playlists = getPlaylists();
		arrayAdapter = new ArrayAdapter<String>(getContext(),
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
		tracks = MyUtils.getTracks(getContext(),plStr.get(Integer.parseInt(plID)));
		Intent intent = new Intent(getContext(), MainActivity.class);
		MyUtils.bundleTracks(tracks,intent,"PLAYLIST");
	}
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
	private void createNewPlaylist() {
		LayoutInflater li = LayoutInflater.from(getContext());
		View promptsView = li.inflate(R.layout.prompt_input, null);

		android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getContext());

		alertDialogBuilder.setView(promptsView);

		final EditText userInput = promptsView
						.findViewById(R.id.editTextDialogUserInput);

		alertDialogBuilder
						.setCancelable(true)
						.setPositiveButton("Create Playlist",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int id) {
												Intent intent = new Intent(getContext(), AddTracksToPlaylist.class);
												db = new AudioDB(getContext(), R.integer.DATABASE_READ_WRITE_MODE);
												String newPlaylist = userInput.getText().toString();
												Long playlistID = db.createNewPlayList(newPlaylist);
												print("KEY "+playlistID.toString());
												intent.putExtra(AudioContract.FeedEntry.PLAYLIST_ID, Long.valueOf(playlistID));
												playlists.put(playlistID, newPlaylist);
												plStr.add(newPlaylist);
												arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,plStr);
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
}

