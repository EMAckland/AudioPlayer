package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Emily on 11/30/2017.
 */

public class AlbumsActivity extends AppCompatActivity {
    private TableLayout albumsView;
    private List<String> artists = new ArrayList<>();
    private Set<String> albumSet = new HashSet<>();
    private ArrayList<AudioFile> tracksList = new ArrayList<>();
    private List<String> albums = new ArrayList<>();
    private List<Album> albumList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        albumsView = findViewById(R.id.albums_table);
        getAudioFiles();
        matchArtistToAlbum();
        generateAlbumView();
    }
    private void generateAlbumView(){
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,120, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams
                (TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams
                (TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        Iterator<Album> iterator = albumList.iterator();
        TableRow row = new TableRow(this);
        row.setLayoutParams(rowParams);
        int rowIdx = 0;
        for (Album a : albumList){
            for(AudioFile t : a.getTracks()){
            }
        }
        for (Album a: albumList){
            ImageButton albumArt = new ImageButton(this);
            albumArt.setLayoutParams(new TableRow.LayoutParams(width,height));
            albumArt.setBackgroundColor(111);
            albumArt.setAdjustViewBounds(true);
            albumArt.setTag(a);
            albumArt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Album album = (Album) view.getTag();
                    List<Bundle> bundles = new ArrayList<>();
                    for (AudioFile f : album.getTracks()){
                        Bundle b = new Bundle();
                        b.putString("ARTIST",f.getArtist());
                        b.putString("ALBUM",f.getAlbum());
                        b.putString("DURATION",f.getDuration());
                        b.putString("TITLE", f.getTitle());
                        b.putLong("ID",f.getID());
                        b.putByteArray("ALBUMART",f.getAlbumArt().getNinePatchChunk());
                        bundles.add(b);
                    }
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    int bundleID = 0;
                    for (Bundle b : bundles){
                        intent.putExtra("ALBUM"+bundleID, b);
                        bundleID++;
                    }
                    startActivity(intent);
                }
            });
            albumArt.setImageBitmap(a.getAlbumArt());
            row.addView(albumArt);
            rowIdx++;
            if (rowIdx==3){
                albumsView.addView(row);
                rowIdx=0;
                row = new TableRow(this);
                row.setLayoutParams(rowParams);
            }
        }
        albumsView.addView(row);
    }
    private void matchArtistToAlbum(){

        Album tempAlbum = null;
        for (String s : albumSet){
            for (AudioFile t : tracksList){
                if (s.trim().equals(t.getAlbum().trim())){
                    ArrayList<AudioFile> albumTracks = new ArrayList<>();
                    albumList.add(new Album(t.getArtist(), t.getAlbum(), t.getAlbumArt()));
                }
            }
        }
        int idx = 0;
        ArrayList<String> added = new ArrayList<>();
        Album album;
        for (Album a : albumList){
            print(a.getTitle());
        }
        for (String a : albumSet){
            print(a);
        }
        for (String s : albumSet){
            ArrayList<AudioFile> albumTracks = new ArrayList<>();
            for (AudioFile t : tracksList){
                if (s.equals(t.getAlbum())){
                    albumTracks.add(t);
                }
            }
            albumList.get(idx).setTracks(albumTracks);
        }
/*        for (AudioFile a : tracksList){
            for (AudioFile b : tracksList){
                if(a.getAlbum().equals(b.getAlbum())){
                    albumSet.add(new Album(a.getArtist(),a.getTitle(), a.getAlbumArt()));
                }
            }
        }
        for (AudioFile t : tracksList){
            for(Album a : albumSet) {
                albumList.add(a);
                if(a.getTitle().equals(t.getAlbum())){
                    tempAlbum = a;
                    albumTracks.add(t);
                }
            }
            if (tempAlbum!=null)
                tempAlbum.setTracks(albumTracks);
            albumTracks = new ArrayList<>();
        }*/
    }
    private void getAudioFiles() {
        List<String> albums2 = new ArrayList<>();
        ContentResolver audioResolver = getContentResolver();
        ContentResolver albumResolver = getContentResolver();
        ContentResolver mContentResolver = getContentResolver();
        Uri audioUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri albumUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor albumCursor = albumResolver.query(albumUri, null, null,
                null, null);
        Cursor audioCursor = audioResolver.query(audioUri, null, null,
                null, null);
        String[] mProjection =
                {
                        MediaStore.Audio.Artists._ID,
                        MediaStore.Audio.Artists.ARTIST,
                        MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
                };

        Cursor artistCursor = mContentResolver.query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                mProjection,
                null,
                null,
                MediaStore.Audio.Artists.ARTIST + " ASC");
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
            int albCol = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int albumArtCol = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);

            //add songs to list
            do {
                Bitmap albumBMP;
                Long thisId = null;
                String thisTitle = null ;
                String thisArtist = null ;
                String thisDuration = null;
                String album = null;
                thisId = audioCursor.getLong(idColumn);
                String album2 = null;
                try {
                    thisTitle = audioCursor.getString(titleColumn);
                    if(thisTitle==null)
                        thisTitle = "<Unknown Title>";
                }catch (Exception e){
                    thisTitle = "<Unknown Title>";
                }
                try {
                    thisArtist = audioCursor.getString(artistColumn);
                    if(thisArtist==null)
                        thisArtist = "<Unknown Artist>";
                } catch (Exception e){
                    thisArtist = "<Unknown Artist>";
                }
                thisDuration = audioCursor.getString(durColumn);
                String albumPath = albumCursor.getString(albumArtCol);
                if (albumPath != null)
                    albumBMP = BitmapFactory.decodeFile(albumPath);
                else {
                    albumBMP = getBitmapFromDrawable(this, R.drawable.ic_wallpaper_black_24dp);
                }
                try {
                    album = audioCursor.getString(albumColumn);
                    if(album==null){
                        album = "<Unknown Album>";
                    }
                }catch (Exception e){
                    album = "<Unknown Album>";
                }
                try {
                    album2 = audioCursor.getString(albCol);
                    if(album2==null){
                        album2 = "<Unknown Album>";
                    }
                }catch (Exception e){
                    album2 = "<Unknown Album>";
                }
                albums2.add(album2);
                albumSet.add(album);
                albums.add(album);
                artists.add(thisArtist);
                AudioFile track = new AudioFile(thisId, thisTitle, thisArtist, thisDuration,
                        album, albumBMP);
                tracksList.add(track);
            }
            while (audioCursor.moveToNext() && albumCursor.moveToNext());
        }
        print("ALBUMSET");
        for (String s : albumSet)
            print(s);
        print("ALBUMLIST");
        for (String s : albums)
            print(s);
        print("ALBUMLIST2");
        for (String s : albums2)
            print(s);
        print("TRACKS");
        for (AudioFile a : tracksList){
            print(a.getTitle()+a.getArtist()+a.getAlbum());

        }
    }
    public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }
    public static void print(String msg){
        Log.v("DEBUG", msg);
    }
}
