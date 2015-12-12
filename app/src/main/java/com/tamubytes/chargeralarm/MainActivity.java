package com.tamubytes.chargeralarm;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tvBatteryLevel;
    ImageView imBattery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.e("Alex", "Activity Created");
        tvBatteryLevel = (TextView) findViewById(R.id.tv_battery_level);
        imBattery = (ImageView) findViewById(R.id.im_battery);

        startBatteryService();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.e("Alex", "Activity onResume");
        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.e("Alex", "Activity onPause");
        stopBatteryService();
        unregisterReceiver(broadcastReceiver);
    }

    private void startBatteryService(){
        if (!isServiceRunning()) {
            startService(new Intent(getApplicationContext(), BatteryService.class));
        }
    }

    private void stopBatteryService(){
        //stopService(new Intent(getApplicationContext(), BatteryService.class));
    }


    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.tamubytes.chargeralarm.BatteryService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);


            if (level>=100){
                imBattery.setBackgroundResource(R.drawable.b_1);
            }else if (level>60){
                imBattery.setBackgroundResource(R.drawable.b_2);
            }else if (level>25){
                imBattery.setBackgroundResource(R.drawable.b_3);
            }else if (level<=25){
                imBattery.setBackgroundResource(R.drawable.b_4);
                //Log.e("Alex", level + " %");
            }

            if (level >=100){
                tvBatteryLevel.setText("FULL");
            }else{
                tvBatteryLevel.setText(String.valueOf(level) + "%");
            }

        }


    };


}
