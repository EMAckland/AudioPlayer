package emily.ackland.student.curtin.edu.au.audioplayer;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by Emily on 11/28/2017.
 * removes auto hide of mediacontroller
 */

public class MusicController extends MediaController {

    public MusicController(Context inContext){
        super(inContext);
    }
    @Override
    public void hide(){

    }
}
