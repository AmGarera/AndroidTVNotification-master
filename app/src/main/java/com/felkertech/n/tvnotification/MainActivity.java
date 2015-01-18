package com.felkertech.n.tvnotification;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {

    private TextView txtView;
    private NotificationReceiver nReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtView = (TextView) findViewById(R.id.textView);
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.felkertech.n.tvnotification.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver,filter);
        Toast.makeText(this, "V5", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    public void buttonClicked(View v){
        Intent i;
        switch(v.getId()) {
            case R.id.btnCreateNotify:
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);
                NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
                ncomp.setContentTitle("My Notification");
                ncomp.setContentText("Notification Listener Service Example");
                ncomp.setSmallIcon(R.drawable.ic_launcher);
                ncomp.setAutoCancel(true);
                ncomp.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setColor(getResources().getColor(R.color.material_deep_teal_500))
                        .setCategory(NotificationCompat.CATEGORY_STATUS)
                        .setWhen(new Date().getTime()-100000)
                        .addAction(R.drawable.abc_ic_menu_paste_mtrl_am_alpha, "Go to Settings", pendIntent);
                nManager.notify((int)System.currentTimeMillis(),ncomp.build());
                return;
            case R.id.btnClearNotify:
                i = new Intent("com.felkertech.n.tvnotification.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
                i.putExtra("command","clearall");
                sendBroadcast(i);
                return;
            case R.id.btnListNotify:
                i = new Intent("com.felkertech.n.tvnotification.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
                i.putExtra("command","list");
                sendBroadcast(i);
                return;
            case R.id.btnSettings:
                i =new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(i);
                return;
        }
    }

    class NotificationReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event") + "\n" + txtView.getText();
            txtView.setText(temp);
        }
    }

    public ArrayList getGameControllerIds() {
        ArrayList gameControllerDeviceIds = new ArrayList();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();

            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    || ((sources & InputDevice.SOURCE_JOYSTICK)
                    == InputDevice.SOURCE_JOYSTICK)) {
                // This device is a game controller. Store its device ID.
                if (!gameControllerDeviceIds.contains(deviceId)) {
                    gameControllerDeviceIds.add(deviceId);
                }
            }
        }
        return gameControllerDeviceIds;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            if (event.getRepeatCount() == 0) {
                switch (keyCode) {
                    // Handle gamepad and D-pad button presses to
                    // navigate the ship


                    default:
                        if (isFireKey(keyCode)) {
                            // Update the ship object to fire lasers

                            handled = true;
                        }
                        break;
                }
            }
            if (handled) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private static boolean isFireKey(int keyCode) {
        // Here we treat Button_A and DPAD_CENTER as the primary action
        // keys for the game.
        return keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                || keyCode == KeyEvent.KEYCODE_BUTTON_A;
    }
}