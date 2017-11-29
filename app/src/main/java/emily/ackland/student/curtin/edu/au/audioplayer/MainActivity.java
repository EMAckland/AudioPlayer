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
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    private ListView tracksView;
    private ArrayList<AudioFile> tracksList = new ArrayList<>();
    private AudioService audioSrv;
    private Intent playIntent;
    private boolean audioBound=false;
    private boolean paused=false, playbackPaused=false;
    private AudioAdapter viewAdpt;
    private SeekBar seekBar;
    private Handler seekHandler = new Handler();
    private ImageButton skip_next, skip_prev, play_pause, pause_play;
    private TextView duration, curr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewAdpt = new AudioAdapter(this, tracksList);
        tracksView = findViewById(R.id.tracks_list);
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
            int durColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            //add songs to list
            do {
                long thisId = audioCursor.getLong(idColumn);
                String thisTitle = audioCursor.getString(titleColumn);
                String thisArtist = audioCursor.getString(artistColumn);
                String thisDuration = audioCursor.getString(durColumn);
                AudioFile track = new AudioFile(thisId ,thisTitle, thisArtist, thisDuration);
                tracksList.add(track);
            }
            while (audioCursor.moveToNext());
        }
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
        setController();
    }
    public void setController(){
        play_pause = findViewById(R.id.play_pause);
        pause_play = findViewById(R.id.pause_play);
        skip_next = findViewById(R.id.skip_next);
        skip_prev = findViewById(R.id.skip_prev);
        duration = findViewById(R.id.track_duration);
        curr = findViewById(R.id.current_position);
        seekBar = findViewById(R.id.seek_bar);
        seekBar.setEnabled(true);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    seekTo(progress);
                    updateSeek();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekHandler.removeCallbacks(run);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekHandler.postDelayed(run, 1000);
            }
        });
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioSrv.play();
                updateSeek();
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
                updateView();
                updateSeek();
            }
        });
        skip_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioSrv.playNext();
                updateSeek();
                updateView();
            }
        });
    }
    Runnable run = new Runnable() {
        boolean stopped = true;
        @Override
        public void run() {
            setStopped(false);
            if(!paused && audioSrv.isPlaying()){
                updateSeek();
            }else {
                seekHandler.removeCallbacks(run);
            }
        }
        public void setStopped(boolean inStopped){
            stopped = inStopped;
        }
        public boolean isStopped(){
            return stopped;
        }
        public void stop(){
            setStopped(true);
        }
    };

    private void updateSeek(){
        seekBar.setProgress(getCurrentPosition());
        curr.setText(timeStrToSeconds_MM_SS(getCurrentPosition()));
        seekHandler.postDelayed(run, 1000);
    }
    private void updateView() {
        seekBar.setMax(getDuration());
        duration.setText(timeStrToSeconds_MM_SS(getDuration()));
        curr.setText(R.string.track_start);
    }
    private void playNext(){
        audioSrv.playNext();
        if(playbackPaused){
            playbackPaused=false;
        }
    }

    private void playPrev(){
        audioSrv.playPrev();
        if(playbackPaused){
            playbackPaused=false;
        }
    }
    public void trackPicked(View view){
        audioSrv.setTrack(Integer.parseInt(view.getTag().toString()));
        audioSrv.playAudio();
        if(playbackPaused){
            playbackPaused=false;
        }
        updateView();
        updateSeek();
    }

    @Override
    protected void onPause(){
        super.onPause();
        seekHandler.removeCallbacks(run);
        paused=true;
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            updateSeek();
            paused=false;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        audioSrv=null;
        seekHandler.removeCallbacks(run);
        super.onDestroy();
    }
    @Override
    public void pause() {
        playbackPaused = true;
        audioSrv.pausePlayer();
    }
    @Override
    public int getDuration() {
        return Integer.parseInt(audioSrv.getCurrentTrack().getDuration());
    }
    @Override
    public int getCurrentPosition() {
        if(audioSrv!=null && audioBound && audioSrv.isPlaying())
            return audioSrv.getPositon();
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
    /*converts string time in format mm:ss to total seconds value*/
    private String timeStrToSeconds_MM_SS(int time){
        String formattedTime;
        Log.d("time", String.valueOf(time));
        try {
            int seconds = time / 1000;
            int minutes = seconds / 60;
            seconds = seconds % 60;
            if (seconds < 10) {
                    formattedTime = String.valueOf(minutes) + ":0" + String.valueOf(seconds);
                Log.d("formattedTime", formattedTime);
            } else {
                formattedTime = String.valueOf(minutes) + ":" + String.valueOf(seconds);
                Log.d("formattedTime", formattedTime);
            }
        } catch (NumberFormatException e) {
                formattedTime = "error";
        }
        return formattedTime;
    }
}

