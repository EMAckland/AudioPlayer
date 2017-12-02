package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
/**
 * Adapter for creating list view out of ArrayList<AudioFile> elements
 * Created by Emily on 11/29/2017.
 */

public class PlaylistAdapter extends BaseAdapter {
	private Map<Long, String> playlist;
	private LayoutInflater audioInfl;
	Iterator<Long> iterator;
	private ArrayList<String> plstr;

	public PlaylistAdapter(Context ctx, Map<Long, String> inpl, ArrayList<String> plStr) {
		playlist = inpl;
		iterator = playlist.keySet().iterator();
		audioInfl = LayoutInflater.from(ctx);
		this.plstr = plStr;
	}
	@Override
	public int getCount() {return playlist.size();}

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
		LinearLayout plLay = (LinearLayout)audioInfl.inflate(R.layout.playlist, parent, false);
/*		if(iterator.hasNext()){
			TextView plView = (TextView)plLay.findViewById(R.id.playlist_title);
			String plTitle = playlist.get(iterator.next());
			plView.setText(plTitle);
			plLay.setTag(position);
		}*/
		TextView plView = (TextView)plLay.findViewById(R.id.playlist_title);
		String plTitle = playlist.get(position);
		plView.setText(plTitle);
		plLay.setTag(position);
		return plLay;
	}
}

