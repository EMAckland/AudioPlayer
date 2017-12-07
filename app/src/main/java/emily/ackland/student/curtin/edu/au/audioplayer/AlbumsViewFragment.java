package emily.ackland.student.curtin.edu.au.audioplayer;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Emily on 12/3/2017.
 */

public class AlbumsViewFragment extends Fragment {
	final String ALBUMS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.AlbumsViewFragment";
	final String PLAYLISTS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.PlaylistActivity";
	final String ADD_TRACKS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.AddTracksToPlaylist";
	final String TRACKS_FRAGMENT = "emily.ackland.student.curtin.edu.au.audioplayer.MainActivity";
	final String BASE_ACTIVITY = ".BaseAcitivity";
	private TableLayout albumsView;
	private Manager manager;
	private Set<Album> albumSet;
	private ArrayList<AudioFile> tracks;
	private String[] permissions = new String[]{
					Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.WAKE_LOCK,
					Manifest.permission.MEDIA_CONTENT_CONTROL
	};
	public AlbumsViewFragment(){

	}
	public static Fragment newInstance(Context context) {
		AlbumsViewFragment f = new AlbumsViewFragment();
		return f;
	}
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.albums_import_fragment, null);

		return root;
	}
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		FragmentManager manager = getFragmentManager();
		MyUtils.getTracks(getContext(),null);
		albumsView = view.findViewById(R.id.albums_table);
		generateAlbumView();
	}

	private void generateAlbumView() {
		int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120,
						getResources().getDisplayMetrics());
		int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120,
						getResources().getDisplayMetrics());
		TableRow.LayoutParams rowParams = new TableRow.LayoutParams
						(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
		TableRow row = new TableRow(getContext());
		row.setLayoutParams(rowParams);
		int rowIdx = 0;
		albumSet = MyUtils.getAlbums(getContext());

		for (Album a : albumSet) {
			ImageButton albumArt = new ImageButton(getContext());
			albumArt.setLayoutParams(new TableRow.LayoutParams(width, height));
			albumArt.setBackgroundColor(111);
			albumArt.setAdjustViewBounds(true);
			albumArt.setTag(a);
			albumArt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					ArrayList<AudioFile> albumtracks = ((Album)view.getTag()).getTracks();
					for (AudioFile a : albumtracks)
						MyUtils.print("TRACKS "+a.getTitle());
					((BaseActivity)getActivity()).setTracksSource(false);
					((BaseActivity)getActivity()).setAlbumTracks(view, albumtracks);
					((BaseActivity)getActivity()).loadNewFragmentWithBackstack(TRACKS_FRAGMENT,BASE_ACTIVITY);
				}
			});
			albumArt.setImageBitmap(a.getAlbumArt(getContext()));
			row.addView(albumArt);
			rowIdx++;
			if (rowIdx == 3) {
				albumsView.addView(row);
				rowIdx = 0;
				row = new TableRow(getContext());
				row.setLayoutParams(rowParams);
			}
		}
		albumsView.addView(row);
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case 1:
				albumsView = getView().findViewById(R.id.albums_table);
				MyUtils.getTracks(getContext(),null);
				generateAlbumView();
				break;
		}
	}

}
