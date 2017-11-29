package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for creating list view out of ArrayList<AudioFile> elements
 * Created by Emily on 11/29/2017.
 */

public class AudioAdapter extends BaseAdapter {
    private ArrayList<AudioFile> tracks;
    private LayoutInflater audioInfl;

    public AudioAdapter(Context ctx, ArrayList<AudioFile> inTracks) {
        tracks = inTracks;
        audioInfl = LayoutInflater.from(ctx);
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

    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout)audioInfl.inflate(R.layout.track, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        //get song using position
        AudioFile currSong = tracks.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}

