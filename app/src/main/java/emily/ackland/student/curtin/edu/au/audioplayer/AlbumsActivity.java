package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Emily on 11/30/2017.
 */

public class AlbumsActivity extends AppCompatActivity {
    private TableLayout albumsView;
    private List<String> artists = new ArrayList<>();
    private Map<String, AudioFile> albumTracks = new HashMap<>();
    private Map<String, AudioFile> artistAlbums = new HashMap<>();
    private ArrayList<AudioFile> tracksList = new ArrayList<>();
    private List<String> albums = new ArrayList<>();
    private List<Album> albumList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        albumsView = findViewById(R.id.albums_table);
        getAudioFiles();
        Collections.sort(tracksList, new Comparator<AudioFile>() {
            public int compare(AudioFile a, AudioFile b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        matchArtistToAlbum();
        generateAlbumView();
    }
    private void generateAlbumView(){
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams
                (TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams
                (TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        Iterator<AudioFile> iterator = tracksList.iterator();
        Iterator<String> iter = albumTracks.keySet().iterator();

        while (iterator.hasNext()){
            TableRow albumRow = new TableRow(this);
            albumRow.setLayoutParams(rowParams);
            for (int ii = 0; ii<3; ii++){
                ImageButton album = new ImageButton(this);
                album.setLayoutParams(rowParams);
                if (iterator.hasNext()){
                    album.setImageBitmap(iterator.next().getAlbumArt());
                    albumRow.addView(album);
                }else {
                    album.setImageDrawable(getDrawable(R.drawable.ic_web_black_24dp));
                }
            }
            albumsView.addView(albumRow);
        }
    }
    private void matchArtistToAlbum(){
        ArrayList<AudioFile> albumTracks = new ArrayList<>();
        String artist= "<Unknown Artist>";
        for (String album : albums){
            for (AudioFile track : tracksList){
                if(album.equals(track.getAlbum())){
                    albumTracks.add(track);
                }
                artist = track.getArtist();
            }
            albumList.add(new Album(artist, album, albumTracks ));
        }
    }
    private void getAudioFiles() {
        ContentResolver audioResolver = getContentResolver();
        ContentResolver albumResolver = getContentResolver();
        Uri audioUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri albumUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor albumCursor = albumResolver.query(albumUri, null, null,
                null, null);
        Cursor audioCursor = audioResolver.query(audioUri, null, null,
                null, null);
        if (audioCursor != null && audioCursor.moveToFirst() && albumCursor.moveToFirst()) {
            //get columns
            int titleColumn = audioCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = audioCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = audioCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int durColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int albumColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumArt = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);

            //add songs to list
            do {
                long thisId = audioCursor.getLong(idColumn);
                String thisTitle = audioCursor.getString(titleColumn);
                String thisArtist = audioCursor.getString(artistColumn);
                String thisDuration = audioCursor.getString(durColumn);
                String albumPath = albumCursor.getString(albumArt);
                Bitmap albumBMP = BitmapFactory.decodeFile(albumPath);
                String album = audioCursor.getString(albumColumn);
                artists.add(thisArtist);
                albums.add(album);
                AudioFile track = new AudioFile(thisId, thisTitle, thisArtist, thisDuration,
                        album, albumBMP);
                tracksList.add(track);
            }
            while (audioCursor.moveToNext());
        }
    }
}
