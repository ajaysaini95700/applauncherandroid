package notes.com.azri.applauncher;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by azri on 13/12/16.
 */

public class TextToSpeach {

    Context pcntx;
    TextToSpeech t1;

    TextToSpeach(Context cntx)
    {
        pcntx = cntx;
        t1=new TextToSpeech(pcntx, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
    }

    public void speach(String text)
    {
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
    }
}
