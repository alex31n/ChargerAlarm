package com.tamubytes.chargeralarm;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DELL on 11/2/2015.
 */
public class BatteryService extends Service {

    // constant
    public static final long NOTIFY_INTERVAL = 10 * 1000; // 10 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    Intent brIntent;

    boolean canAlarm;

    SharedPreferences pref;

    @Override
    public void onCreate() {
        super.onCreate();

        pref = getSharedPreferences("util", MODE_PRIVATE);
        canAlarm = pref.getBoolean("canAlarm", true);

        //Log.e("Alex", "Service Created");
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }

        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

        //brIntent = new Intent(BROADCAST_ACTION);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e("Alex", "Service Started");
        //Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        //Log.e("Alex", "Service Destroyed");
    }



    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    batteryLevelUpdate();
                }

            });
        }

        private void batteryLevelUpdate(){
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent bIntent = getApplicationContext().registerReceiver(null, ifilter);

            int level = bIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            //int scale = bIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            //float batteryPct = level / (float)scale;

            Log.e("Alex", "Battery level : " + level);

            if (canAlarm){
                if (level<=25 || level>=100){

                    canAlarm = false;

                    Intent notifyIntent = new Intent(getApplicationContext(), MediaPlayerService.class );
                    notifyIntent.putExtra("level", level);
                    startService(notifyIntent);


                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("canAlarm", canAlarm);
                    editor.commit();

                }
            }else{
                if (level>30 && level<80){
                    canAlarm = true;
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("canAlarm", canAlarm);
                    editor.commit();

                }
            }

        }

    }
}
