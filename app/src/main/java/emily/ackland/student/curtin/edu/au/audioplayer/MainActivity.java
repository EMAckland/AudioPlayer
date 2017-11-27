package emily.ackland.student.curtin.edu.au.audioplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView tracksView;
    private List<String> audioStrList = new ArrayList<>();
    private Resources r;
    int pxHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tracksView = new ListView(this);
        tracksView.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
        addContentView(tracksView,new LinearLayout.LayoutParams(-1,-2));
        ActivityCompat.requestPermissions(this, new
                String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 0);
        getAudioFiles();
        generateListView();
    }

    private void getAudioFiles() {
        ContentResolver audioResolver = getContentResolver();
        Uri audioUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor audioCursor = audioResolver.query(audioUri, null, null,
                null, null);
        if (audioCursor != null && audioCursor.moveToFirst()) {
            //get columns
            int titleColumn = audioCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = audioCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = audioCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = audioCursor.getLong(idColumn);
                String thisTitle = audioCursor.getString(titleColumn);
                String thisArtist = audioCursor.getString(artistColumn);
                audioStrList.add((thisArtist + "\n" + thisTitle).trim());
            }
            while (audioCursor.moveToNext());
        }
    }

    private void generateListView() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, audioStrList);
        tracksView.setAdapter(arrayAdapter);
    }
    public void playTrack(android.view.View inView){

    }
}
/*    private void sortList(){
        Collections.sort(audioList, new Comparator<AudioFile>(){
            @Override
            public int compare(AudioFile a, AudioFile b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }
}*/
/*public class MainActivity extends AppCompatActivity {
    private ListView tracksView;
    private List<String> trackStrList = new ArrayList<>();
    private Resources r;
    int pxHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        r = this.getResources();
        pxHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48,
                r.getDisplayMetrics());
        setContentView(R.layout.activity_main);
        tracksView = findViewById(R.id.tracklist);
        trackStrList.add(("artist1\ntrack1").trim());
        trackStrList.add("artist2\ntrack1");
        trackStrList.add("artist3\ntrack1");


        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48,
                r.getDisplayMetrics());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, trackStrList);
        tracksView.setAdapter(arrayAdapter);
    }


}*/