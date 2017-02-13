package notes.com.azri.applauncher;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */

public class VoiceListener extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
       System.out.println("Intent Detected.*&*&*&*&*&*&*&*&");
        //Intent startServiceIntent = new Intent(context, MainActivity.class);
        //context.startService(startServiceIntent);
    }
}