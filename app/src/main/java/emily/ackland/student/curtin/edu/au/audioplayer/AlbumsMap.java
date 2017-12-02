package emily.ackland.student.curtin.edu.au.audioplayer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emily on 12/2/2017.
 */

public class AlbumsMap {
	public static final String _ID = AudioContract.FeedEntry._ID ;
	public static final String ALBUM = AudioContract.FeedEntry.ALBUM ;
	private Map<String,String> albums;
	public AlbumsMap(){
		albums = new HashMap<>();
	}
}
