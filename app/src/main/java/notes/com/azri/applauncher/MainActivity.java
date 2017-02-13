package notes.com.azri.applauncher;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements SensorEventListener {
    private TextView txtSpeechInput,greeting;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int MY_PERMISSIONS_REQUEST_CALL = 999;
    ArrayList<JSONObject> jsonObjectArrayList;
    ArrayList<JSONObject> jsonObjectContactList;
    TextToSpeach tts;
    String usernumber = "";
    Button appsbutton;
    static boolean ring = false;
    static boolean callReceived = false;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 11;
    EditText appn_ame;
    CheckBox shake;
    private float lastX, lastY, lastZ;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float predeltaX = 0;
    private float predeltaY = 0;
    private float predeltaZ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtSpeechInput = (TextView) findViewById(R.id.stot);
        shake = (CheckBox) findViewById(R.id.shake);
        greeting = (TextView) findViewById(R.id.clock);
        appn_ame = (EditText) findViewById(R.id.appname);
        appsbutton = (Button) findViewById(R.id.apps_button);
        appsbutton.setVisibility(View.GONE);
        tts = new TextToSpeach(getApplicationContext());
        TextClock textClock = (TextClock) findViewById(R.id.textClock);
        textClock.setFormat12Hour(null);
        //textClock.setFormat24Hour("dd/MM/yyyy hh:mm:ss a");
        textClock.setFormat24Hour("hh:mm:ss a  EEE MMM d");
        java.util.Date date = new java.util.Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int hours = c.get(Calendar.HOUR_OF_DAY);
        String greetingtext="";
        if(hours>=1 && hours<=12){
            greetingtext = "Good Morning Ajay Saini";
        }else if(hours>=12 && hours<=16){
            greetingtext = "Good Afternoon Ajay Saini";
        }else if(hours>=16 && hours<=21){
            greetingtext = "Good Evening Ajay Saini";
        }else if(hours>=21 && hours<=24){
            greetingtext = "Good Night Ajay Saini";
        }
        greeting.setText(greetingtext);
        //tts.speach(greetingtext);
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyMgr.listen(new TeleListener(), PhoneStateListener.LISTEN_CALL_STATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
        askForContactPermission();
        getAllapps();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // fai! we dont have an accelerometer!
            Toast.makeText(getApplicationContext() , "dont have an accelerometer " ,Toast.LENGTH_SHORT).show();
        }

    }

    public void askForContactPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Should" );
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                System.out.println("permission" );
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Contacts access needed");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage("please confirm Contacts access");
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(
                                new String[]
                                        {Manifest.permission.READ_CONTACTS}
                                , MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                });
                builder.show();

            } else {

                // No explanation needed, we can request the permission.
                System.out.println("No explanation" );
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else{
            System.out.println("No else" );
            getallcontact();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("granted" );
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getallcontact();

                } else {
                    Toast.makeText(MainActivity.this, "Please confirm Read contact access ", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CALL: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    phoneCall();

                } else {
                    System.out.println("not granted contact" );
                    Toast.makeText(MainActivity.this, "Please confirm Call access ", Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void getallcontact()
    {
        jsonObjectContactList = new ArrayList<JSONObject>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
        if (cursor != null)
        {
            while (cursor.moveToNext()) {
                String name =cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //System.out.println("name : + " +name + "number: + " + phoneNumber);
                    try {
                        JSONObject dbdata = new JSONObject();
                        dbdata.put("name", name);
                        dbdata.put("number", phoneNumber);
                        jsonObjectContactList.add(dbdata);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
            System.out.println("jsonObjectContactList : + " +jsonObjectContactList);
            cursor.close();
        }
        else
        {
            System.out.println("cursor : + " + cursor);
        }
    }
    public void setting(View v) {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);
    }

    class TeleListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {

                case TelephonyManager.CALL_STATE_IDLE:

                    if (ring == true && callReceived == false && incomingNumber.length() !=0) {

                        tts.speach("Missed call from " + incomingNumber);
                        Toast.makeText(getApplicationContext(), "Missed call from : " + incomingNumber, Toast.LENGTH_LONG).show();
                    }
                    // CALL_STATE_IDLE;
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // CALL_STATE_OFFHOOK;
                    callReceived = true;
                    break;

                case TelephonyManager.CALL_STATE_RINGING:
                    ring = true;
                    // CALL_STATE_RINGING
                    break;

                default:
                    break;
            }
        }

    }

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            tts.speach("Notification from " + title + "message is " + text);
            System.out.println("title : " + title + "text : " + text);
        }
    };

    public void showApps(View v){
        Intent i = new Intent(this, AppsListActivity.class);
        startActivity(i);
    }

    public void googleSpeach(View v){
        promptSpeechInput();

    }

    public void getAllapps() {

        jsonObjectArrayList = new ArrayList<JSONObject>();

        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            try {
                JSONObject dbdata = new JSONObject();
                dbdata.put("package", packageInfo.packageName);
                dbdata.put("appname", pm.getApplicationLabel(packageInfo));
                System.out.println("appname : + " + pm.getApplicationLabel(packageInfo));
                jsonObjectArrayList.add(dbdata);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(jsonObjectArrayList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject fruit2, JSONObject fruit1)
            {
                try {
                    return fruit1.getString("appname").compareToIgnoreCase(fruit2.getString("appname"));

                }catch (JSONException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        Collections.reverse(jsonObjectArrayList);
    }
    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        appn_ame.setText("");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("resultCode" +resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    openApp(result.get(0).toLowerCase());
                }
                break;
            }

        }
    }

    public void openApps(View v)
    {
        String enterappname = appn_ame.getText().toString().toLowerCase();
        if(enterappname.length() !=0) {
            openApp(enterappname);
        }
        else
        {
            appn_ame.setError("Enter appname");
        }
    }

    public void openApp(String txt)
    {
        System.out.println("txt" + txt);
        Boolean find = false;
        String pkname = "";
        if(!txt.contains("call"))
        {
            for (int i = 0; i < jsonObjectArrayList.size(); i++) {
                try {
                    JSONObject row = jsonObjectArrayList.get(i);
                    System.out.println("Installed package*****************" + row.getString("appname"));
                    String appnem = row.getString("appname").toLowerCase();
                    if (txt.equals(appnem)) {
                        pkname = row.getString("package");
                        find = true;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (find) {
                Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(pkname);
                if (intent == null) {
                    // Bring user to the market or let them choose an app?
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + pkname));

                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                appn_ame.setText("");

            } else if (txt.equals("show apps")) {
                appsbutton.setVisibility(View.VISIBLE);
            } else if (txt.equals("hide apps")) {
                appsbutton.setVisibility(View.GONE);
            } else {
                if (appn_ame.getText().length() == 0)
                     tts.speach(txt + " is not found, please speach correct app name");
                else
                    Toast.makeText(MainActivity.this,txt + " is not found, please type correct app name",Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            for (int i = 0; i < jsonObjectContactList.size(); i++) {
                try {
                    JSONObject row = jsonObjectContactList.get(i);
                    String usernem = row.getString("name").toLowerCase();
                    if (txt.equals("call " +usernem)) {
                        usernumber = row.getString("number");
                        find = true;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (find) {
                askForCallPermission();
                appn_ame.setText("");
            }
            else {
                if (appn_ame.getText().length() == 0)
                    tts.speach(txt + " is not found, please speach correct name");
                else
                    Toast.makeText(MainActivity.this,txt + " is not found, please type 'call username'",Toast.LENGTH_LONG).show();
            }
        }

    }

    public void askForCallPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                System.out.println("permission ++++++" );
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Call access needed");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage("Please confirm Call access");
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(
                                new String[]
                                        {android.Manifest.permission.CALL_PHONE}
                                , MY_PERMISSIONS_REQUEST_CALL);
                    }
                });
                builder.show();

            } else {

                // No explanation needed, we can request the permission.
                System.out.println("No explanation" );
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else{
            phoneCall();
        }
    }

    public void phoneCall()
    {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+ usernumber));
            startActivity(callIntent);
        }catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // display the max x,y,z accelerometer values

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.round(Math.abs(lastX - event.values[0]));
        deltaY = Math.round(Math.abs(lastY - event.values[1]));
        deltaZ = Math.round(Math.abs(lastZ - event.values[2]));

        if( deltaX !=predeltaX && deltaY !=predeltaY && deltaZ !=predeltaZ)
        {
            if (deltaX < 1)
                deltaX = 0;
            if (deltaY < 1)
                deltaY = 0;
            if (deltaZ < 1)
                deltaZ = 0;
            predeltaX = deltaX;
            predeltaY = deltaY;
            predeltaZ = deltaZ;
            // display the current x,y,z accelerometer values
            displayCurrentValues();
        }

    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        if(deltaX > 0 && deltaX > 0 && deltaZ > 0 && shake.isChecked()) {
            tts.speach("Do not touch mobile phone");
        }
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy(){
        super.onPause();
        tts.onPause();
    }

    /*@Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
    }*/
}

