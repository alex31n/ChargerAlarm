package com.tamubytes.chargeralarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by paulruiz on 10/28/14.
 */
public class MediaPlayerService extends Service {

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_ACTIVITY = "action_activity";

    private static final int NOTIFICATION_ID = 100;

    public static final int ACTION_REQUEST_CODE = 1;

    NotificationManager notificationManager;
    Uri alarmUri;
    Ringtone ringtone;

    int level;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void buildNotification(){

        level = getBatteryLevel ();

        Intent serviceIntent = new Intent(getApplicationContext(), MediaPlayerService.class);
        serviceIntent.setAction(ACTION_STOP);
        Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent servicePendingIntent = PendingIntent.getService(getApplicationContext(),ACTION_REQUEST_CODE,serviceIntent,0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setSmallIcon(R.drawable.perm_group_affects_battery);
        mBuilder.setAutoCancel(false);
        mBuilder.setContentIntent(servicePendingIntent);
        mBuilder.setDeleteIntent(servicePendingIntent);
        //Log.e("Alex", "Battery level : " + level);

        if (level<=25){
            mBuilder.setContentTitle(getApplicationContext().getString(R.string.notify_low_title));
            mBuilder.setContentText(getApplicationContext().getString(R.string.notify_low_msg));
        }else if (level>=100){
            mBuilder.setContentTitle(getApplicationContext().getString(R.string.notify_full_title));
            mBuilder.setContentText(getApplicationContext().getString(R.string.notify_full_msg));
        }
        mBuilder.addAction(actionBuilder(R.drawable.perm_group_device_alarms, "Stop", ACTION_STOP));
        mBuilder.addAction(actionBuilder(R.drawable.perm_group_user_dictionary, "View State", ACTION_ACTIVITY));
        //Log.e("Alex", "Battery level : " + level);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        playAlarm();
    }

    private NotificationCompat.Action actionBuilder(int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), ACTION_REQUEST_CODE, intent, 0);
        return new NotificationCompat.Action.Builder( icon, title, pendingIntent ).build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Alex", "Media Service Create");
        // Gets an instance of the NotificationManager service
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        initAlarm();

        buildNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        Log.e("Alex", "Media Service Started");

        //level = intent.getIntExtra("level",50);
        //Log.e("Alex", "Battery level : " + level);
        handleIntent(intent);

        return START_STICKY;
    }

    private void initAlarm(){
        alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
    }

    public void playAlarm(){
        if (!ringtone.isPlaying())  ringtone.play();
        Toast.makeText(getApplicationContext(),"Alarm Start", Toast.LENGTH_SHORT).show();
    }

    public void stopAlarm(){
        if (ringtone.isPlaying())  ringtone.stop();
    }

    private void handleIntent(Intent intent){
        if( intent == null || intent.getAction() == null )
            return;

        String action = intent.getAction();


        if( action.equalsIgnoreCase( ACTION_STOP ) ) {

        }else if( action.equalsIgnoreCase( ACTION_ACTIVITY ) ) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        notificationManager.cancelAll();
        ringtone.stop();
        stopSelf();
        stopService(new Intent(getApplicationContext(), MediaPlayerService.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Alex", "Media Service Destroyed");
    }


    int getBatteryLevel (){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent bIntent = getApplicationContext().registerReceiver(null, ifilter);

        int level = bIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        return level;
    }
}