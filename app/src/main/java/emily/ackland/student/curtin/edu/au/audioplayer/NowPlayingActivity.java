package emily.ackland.student.curtin.edu.au.audioplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;

/**
 * Created by Emily on 11/29/2017.
 */

public class NowPlayingActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    @Override
    public void onCreate(Bundle savedInstance){
     super.onCreate(savedInstance);
     setContentView(R.layout.audioplaying_activity);
    }
    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
