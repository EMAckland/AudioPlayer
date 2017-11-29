package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    private ListView tracksView;
    private ArrayList<AudioFile> tracksList = new ArrayList<>();
    private MusicController musicContr;
    private AudioService audioSrv;
    private Intent playIntent;
    private boolean audioBound=false;
    private boolean paused=false, playbackPaused=false;
    private AudioAdapter viewAdpt;
    private SeekBar seekBar;
    private Handler seekHandler = new Handler();
    private ImageButton skip_next, skip_prev, play_pause, pause_play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewAdpt = new AudioAdapter(this, tracksList);
        tracksView = new ListView(this);
        tracksView.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
        addContentView(tracksView,new LinearLayout.LayoutParams(-1,-2));
        ActivityCompat.requestPermissions(this, new
                String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
        ActivityCompat.requestPermissions(this, new
                String[]{"android.permission.MEDIA_CONTROL"}, 0);
        ActivityCompat.requestPermissions(this, new
                String[]{"android.permission.WAKE_LOCK"}, 0);
        getAudioFiles();
        //sort by track title
        Collections.sort(tracksList, new Comparator<AudioFile>(){
            public int compare(AudioFile a, AudioFile b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        tracksView.setAdapter(viewAdpt);
        setController();
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
                AudioFile track = new AudioFile(thisId ,thisTitle, thisArtist);
                tracksList.add(track);
            }
            while (audioCursor.moveToNext());
        }
    }

    public void setController(){
        play_pause = findViewById(R.id.play_pause);
        pause_play = findViewById(R.id.pause_play);
        skip_next = findViewById(R.id.skip_next);
        skip_prev = findViewById(R.id.skip_prev);
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioSrv.playAudio();
            }
        });
        pause_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioSrv.pausePlayer();
            }
        });
        skip_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioSrv.playPrev();
            }
        });
        skip_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioSrv.playNext();
            }
        });
    }
    private void playNext(){
        audioSrv.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        musicContr.show(0);
    }

    private void playPrev(){
        audioSrv.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        musicContr.show(0);
    }
    public void trackPicked(View view){
        Intent intent = new Intent(this, NowPlayingActivity.class);
        startActivity(intent);
        audioSrv.setTrack(Integer.parseInt(view.getTag().toString()));
        audioSrv.playAudio();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        musicContr.show(0);
    }
    private ServiceConnection audioConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder)service;
            //get service
            audioSrv = binder.getService();
            //pass list
            audioSrv.setTracks(tracksList);
            audioBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
           audioBound = false;
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, AudioService.class);
            bindService(playIntent, audioConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            setController();
            paused=false;
        }
    }
    @Override
    protected void onStop() {
        musicContr.hide();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        audioSrv=null;
        super.onDestroy();
    }
    @Override
    public void pause() {
        playbackPaused = true;
        audioSrv.pausePlayer();
    }
    @Override
    public int getDuration() {
        if(audioSrv!=null && audioBound && audioSrv.isPlaying())
            return audioSrv.getDur();
        else return 0;
    }
    @Override
    public int getCurrentPosition() {
        if(audioSrv!=null && audioBound && audioSrv.isPlaying())
            return audioSrv.getPosn();
        else return 0;
    }
    @Override
    public void start() {audioSrv.go();}
    @Override
    public boolean canPause() {return true;}
    @Override
    public boolean isPlaying() {return false;}
    @Override
    public int getBufferPercentage() {return 0;}
    @Override
    public boolean canSeekBackward() {return false;}
    @Override
    public boolean canSeekForward() {return false;}
    @Override
    public void seekTo(int pos) {audioSrv.seek(pos);}
    @Override
    public int getAudioSessionId() {return 0;}

}
