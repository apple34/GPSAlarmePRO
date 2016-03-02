package br.com.abner.naopassedalinha3;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Abner on 26/02/2016.
 */
public class ServiceBackground extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "ServiceBackground";
    private static final int MAX = 10;
    private boolean running;
    private int count;
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    private Bundle extras;

    public ServiceBackground () {}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, " criado");
        super.onCreate();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }
        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, " iniciado");
        extras = intent.getExtras();
        Log.i(TAG, "ALL EXTRAS: "+extras.get("location"));
        count = 0;
        running = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    class WorkThread extends Thread {

        Location location;
        public WorkThread (Location location) {
            this.location = location;
        }

        @Override
        public void run() {
            Log.i(TAG, "WorkThread: "+location);

            super.run();

            try {
                while (running && count < MAX) {
                    Log.i(TAG, "Location: "+location.toString());
                }
            }catch (Exception e) {
                Log.i(TAG, e.getMessage(), e);
            } finally {
                stopSelf();
                Log.i(TAG, " encerrado.");
            }
        }
    }

    //****************************************************//

    @Override
    public void onLocationChanged(Location location) {
        Intent intent = new Intent(this, ServiceBackground.class);
        intent.putExtra("location", location);
        Log.i(TAG, "onLocationChanged( ): " + location.toString());
        onStartCommand(intent,0,0);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //****************************************************//

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.i(TAG, "onConnected: "+String.valueOf(mLastLocation.getLatitude())+", "+
                    String.valueOf(mLastLocation.getLongitude()));
        }

        onLocationChanged(mLastLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}
