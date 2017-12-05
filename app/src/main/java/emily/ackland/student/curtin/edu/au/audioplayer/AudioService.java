package emily.ackland.student.curtin.edu.au.audioplayer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emily on 11/28/2017.
 */

public class AudioService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener {
    private MediaPlayer player;
    private List<AudioFile> tracks;
    private List<Long> trackIDs;
    private int trackPosn;
    private final IBinder audioBind = new AudioBinder();
    private int pausePos=0;

    public void onCreate() {
        super.onCreate();
        trackPosn = 0;
        player = new MediaPlayer();
        initAudioPlayer();
    }
    public void play(){
        if(!player.isPlaying()) {
            player.seekTo(pausePos);
            player.start();
        }
    }

    public void setTracks(List<AudioFile> inTracks) {tracks = inTracks;}
    public void setTrack(int trackIdx) {trackPosn = trackIdx;}

    public void setTracks(ArrayList<Long> trackIDs){
        List<AudioFile> newtracks = new ArrayList<>();
        this.trackIDs = trackIDs;
        for (AudioFile t : tracks){
            for(Long l : trackIDs){
                if(l.equals(t.getID())){
                    newtracks.add(t);
                }
            }
        }
        tracks=newtracks;
        trackPosn=0;
    }

    public void initAudioPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public class AudioBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
    }
    public AudioFile getCurrentTrack(){
        return tracks.get(trackPosn);
    }
    public void playAudio() {
        player.reset();
        AudioFile track = tracks.get(trackPosn);
        long currTrack = track.getID();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currTrack);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        try{
            player.prepare();
        }catch (IOException e){
            player.prepareAsync();
        }
    }

    public int getPositon(){return player.getCurrentPosition();}
    public int getDuration(){return player.getDuration();}
    public boolean isPlaying(){return player.isPlaying();}
    public void pausePlayer(){
        pausePos = player.getCurrentPosition();
        player.pause();
    }
    public void seek(int posn){player.seekTo(posn);}
    public void go() {player.start();}

    public void playNext(){
        trackPosn++;
        if(trackPosn >= tracks.size())
            trackPosn=0;
        playAudio();
    }
    public void playPrev(){
        trackPosn--;
        if (trackPosn < 0)
            trackPosn=0;
        playAudio();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return audioBind;
    }
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()>0){
            mp.reset();
            playNext();
        }
    }
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
