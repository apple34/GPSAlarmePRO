package br.com.abner.gpsalarmepro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by AbnerAdmin on 27/08/2016.
 */
public class BroadcastReceiverLocation extends BroadcastReceiver {

    public LatLng latLng;
    private LatLng myLatLng;

    private double latitude, longitude;

    public BroadcastReceiverLocation(LatLng latLng){this.myLatLng = latLng;}

    @Override
    public void onReceive(Context context, Intent intent) {
        latitude = intent.getDoubleExtra("latitude", myLatLng.latitude);
        longitude = intent.getDoubleExtra("longitude", myLatLng.longitude);

        latLng = new LatLng(latitude, longitude);
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
