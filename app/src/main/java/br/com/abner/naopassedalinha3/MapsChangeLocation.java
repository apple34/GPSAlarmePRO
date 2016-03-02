package br.com.abner.naopassedalinha3;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.support.v7.internal.app.NotificationCompatImpl21;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Notifications;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 * Created by AbnerAdmin on 10/01/2016.
 */
public class MapsChangeLocation extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String BROADCAST_ACTION = "Hello";
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Intent intent;
    private LocationService locationService;
    private boolean running;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

        Log.i(TAG, "MapsActivity.onCreate() - Serviço iniciado");
    }

    public MapsChangeLocation() { }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.i(TAG, mLastLocation.getLatitude()+", "+mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "MapsActivity.onStartCommand() - Serviço iniciado: " + startId);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        locationService.onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient));

        running = true;
        new WorkThread().start();

        return super.onStartCommand(intent, flags, startId);
    }

    class WorkThread extends Thread{
        @Override
        public void run() {
            super.run();

            try {
                while (running) {
                    Log.i(TAG, mLastLocation.getLatitude()+", "+mLastLocation.getLongitude());
                }
            }catch (Exception e) {
                Log.i(TAG, e.getMessage(), e);
            } finally {
                stopSelf();
                Log.i(TAG, "MapsActivity encerrado.");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        Log.i(TAG, "MapsChangeLocation.onDestroy() - Serviço destruído");
    }

    public class LocationService implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            intent.putExtra("Latitude", location.getLatitude());
            intent.putExtra("Longitude", location.getLongitude());
            sendBroadcast(intent);
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
    }
}
