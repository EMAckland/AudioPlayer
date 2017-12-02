package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for creating list view out of ArrayList<AudioFile> elements
 * Created by Emily on 11/29/2017.
 */

public class AudioAdapter extends BaseAdapter {
    private ArrayList<AudioFile> tracks, selectedTracks;
    private LayoutInflater audioInfl;
		private Context ctx;

    public AudioAdapter(Context ctx, ArrayList<AudioFile> inTracks) {
        tracks = inTracks;
        selectedTracks = new ArrayList<>();
        audioInfl = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }
    @Override
    public int getCount() {return tracks.size();}

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }
    public ArrayList<AudioFile> getSelectedTracks(){
    	return selectedTracks;
		}

    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song select_tracks_activity
        LinearLayout songLay = (LinearLayout)audioInfl.inflate(R.layout.checkable_tracks, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
				LinearLayout checkLay = songLay.findViewById(R.id.check_element);
				final AudioFile currSong = tracks.get(position);
				final CheckBox checkBox = (CheckBox) checkLay.getChildAt(0);
				checkBox.setTag(currSong);
				checkBox.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										final boolean isChecked = ((CheckBox)view).isChecked();
										if(isChecked)
											selectedTracks.add((AudioFile)view.getTag());
										else {
											for(AudioFile t : tracks){
												if(view.getTag().equals(t)){
													selectedTracks.remove(t);
												}
											}
										}
								}
				});
								//get song using position

								//get title and artist strings
				songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}

