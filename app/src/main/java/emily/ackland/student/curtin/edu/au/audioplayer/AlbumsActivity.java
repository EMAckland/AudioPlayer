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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        String artist = "<Unknown Artist>";
        String albumDefaultTitle = "<Unknown Album>";
        String albumActualTitle = "<Unknown Album>";
        int numAlbums = 0;
        boolean matchDone = false;
        Bitmap albumArt;
        for (AudioFile a : tracksList){
            ArrayList<AudioFile> currAlbumTracks = new ArrayList<>();
            for (AudioFile b : tracksList){
                if(a.getAlbum().equals(b.getAlbum())){
                    ArrayList<AudioFile> albumTracks = new ArrayList<>();
                    for (AudioFile c : tracksList){
                        if(c.getAlbum().equals(b.getAlbum())){
                            albumTracks.add(c);
                        }
                    }
                    currAlbumTracks = albumTracks;
                }
            }
            Log.v("ADDED ", a.getTitle());
            albumList.add(new Album(a.getArtist(),a.getTitle(),
                    currAlbumTracks,a.getAlbumArt()));
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
            int albumArtCol = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);

            //add songs to list
            do {
                Bitmap albumBMP;
                long thisId = audioCursor.getLong(idColumn);
                String thisTitle = audioCursor.getString(titleColumn);
                String thisArtist = audioCursor.getString(artistColumn);
                String thisDuration = audioCursor.getString(durColumn);
                long albumID = albumCursor.getLong(albumArtCol);
                String albumPath = albumCursor.getString(albumArtCol);
                if(albumPath!=null)
                    albumBMP = BitmapFactory.decodeFile(albumPath);
                else {
                    albumBMP = getBitmapFromDrawable(this,R.drawable.ic_wallpaper_black_24dp);
                }
                String album = audioCursor.getString(albumColumn);
                artists.add(thisArtist);
                AudioFile track = new AudioFile(thisId, thisTitle, thisArtist, thisDuration,
                        album, albumBMP);
                tracksList.add(track);
            }
            while (audioCursor.moveToNext() && albumCursor.moveToNext());
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
}
