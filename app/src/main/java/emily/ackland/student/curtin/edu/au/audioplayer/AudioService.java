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

import java.util.List;

/**
 * Created by Emily on 11/28/2017.
 */

public class AudioService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener {
    private MediaPlayer player;
    private List<AudioFile> tracks;
    private int trackPosn;
    private final IBinder audioBind = new AudioBinder();

    public void onCreate() {
        super.onCreate();
        trackPosn = 0;
        player = new MediaPlayer();
        initAudioPlayer();
    }
    public void initAudioPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }
    public void setTracks(List<AudioFile> inTracks) {tracks = inTracks;}
    public void setTrack(int trackIdx) {trackPosn = trackIdx;}
    public class AudioBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
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
        player.prepareAsync();
    }

    public int getPosn(){return player.getCurrentPosition();}
    public int getDur(){return player.getDuration();}
    public boolean isPlaying(){return player.isPlaying();}
    public void pausePlayer(){player.pause();}
    public void seek(int posn){player.seekTo(posn);}
    public void go() {player.start();}
    public void updateSeek(){
    }
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
