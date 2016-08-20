package br.com.abner.gpsalarmepro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by AbnerAdmin on 20/08/2016.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentServiceBackground = new Intent(context, ServiceBackground.class);
        intentServiceBackground.setAction("null");
        context.startService(intentServiceBackground);
    }
}
