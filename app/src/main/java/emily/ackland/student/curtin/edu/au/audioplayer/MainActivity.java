package emily.ackland.student.curtin.edu.au.audioplayer;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import emily.ackland.student.curtin.edu.au.audioplayer.R;

public class MainActivity extends AppCompatActivity {
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
/*        Button track = new Button(this);
        track.setText(trackStrList.get(0));
        track.setLayoutParams(new LinearLayout.LayoutParams(-2, px));
        tracksView.add;*/
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, trackStrList);
        tracksView.setAdapter(arrayAdapter);
    }

}
/*package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<AudioFile> audioList = new ArrayList<>();
    private List<String> audioStrList = new ArrayList<>();
    private LinearLayout audioFileView;
    private Resources r = getResources();
    int pxHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
            r.getDisplayMetrics());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioFileView = findViewById(R.id.audiotracklist);
    }
    private void getAudioFiles(){
        ContentResolver audioResolver = getContentResolver();
        Uri audioUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor audioCursor = audioResolver.query(audioUri, null, null,
                null, null);
        if(audioCursor!=null && audioCursor.moveToFirst()){
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
                audioList.add(new AudioFile(thisId, thisTitle, thisArtist));
                audioStrList.add(thisArtist);
                audioStrList.add(thisTitle);
            }
            while (audioCursor.moveToNext());
        }
    }
    private void generateListView(){
        TextView audioFile;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, pxHeight);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_2, audioStrList);

        lv.setAdapter(arrayAdapter);
    }
    private void sortList(){
        Collections.sort(audioList, new Comparator<AudioFile>(){
            @Override
            public int compare(AudioFile a, AudioFile b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }
}*/
