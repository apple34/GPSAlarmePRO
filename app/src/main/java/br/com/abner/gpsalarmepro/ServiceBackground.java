package br.com.abner.gpsalarmepro;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import br.com.abner.gpsalarmepro.R;

/**
 * Created by Abner on 26/02/2016.
 */
public class ServiceBackground extends Service {
    private static final String TAG = "TESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 100;
    private static final float LOCATION_DISTANCE = 0.1f;
    private BDNew bdNew;
    private BDOld bdOld;
    private NotificationCompat.Builder notifBuilder;
    private NotificationManager notificationManager;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        public LocationListener(){}

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            gpsAlarm(location);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

        private void gpsAlarm( Location location ){
            Log.i("Alarm", "gpsAlarm() level 1");
            for( final Marcadores m : bdNew.buscar() ) {
                if( m != null && m.getAtivo() == 1 ) {
                    final long distance = (int) SphericalUtil.computeDistanceBetween(
                            new LatLng(location.getLatitude(), location.getLongitude())
                            , new LatLng(m.getLatitude(), m.getLongitude()));
                    if( distance < m.getDistancia() ) {
                        notifBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon( R.drawable.alarmgps )
                                        .setVibrate( new long[]{ 0, 10000, 0 } )
                                        .setSound(RingtoneManager.getDefaultUri( RingtoneManager.TYPE_ALARM ))
                                        .setContentTitle(m.getNome()+" - "+m.getEndereco());
                        final Intent resultIntent = new Intent(getApplicationContext(), MapsActivity.class);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                        stackBuilder.addParentStack(MapsActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        final PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        //notifBuilder.setFullScreenIntent(resultPendingIntent, true);
                        notifBuilder.setContentIntent( resultPendingIntent );

                        final Intent intent1 = new Intent(getApplicationContext(), ServiceBackground.class);
                        intent1.setAction("more");
                        intent1.putExtra( "address", m.getEndereco() );
                        intent1.putExtra("distance", distance);
                        final PendingIntent pendingIntent1 = PendingIntent.getService(getApplicationContext(),
                                1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                        final Intent intent2 = new Intent(getApplicationContext(), ServiceBackground.class);
                        intent2.setAction("deactivite");
                        intent2.putExtra("address", m.getEndereco());
                        intent2.putExtra( "isMore", 1);
                        final PendingIntent pendingIntent2 = PendingIntent.getService(getApplicationContext(),
                                1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

                        new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        int incr = (int) (100 - (100 * distance) / m.getDistancia());
                                        notifBuilder.setProgress(100, incr, false)
                                                .setContentText(getResources().getString(R.string.distance_of)
                                                        + " " + distance + " " + getResources().getString(R.string.meters))
                                                .setPriority(2)
                                                .setColor(getApplicationContext().getResources().getColor(R.color.colorAccent))
                                                .addAction(R.mipmap.ic_add_black_18dp, "100 "
                                                        + getResources().getString(R.string.meters), pendingIntent1)
                                                .addAction(R.mipmap.ic_alarm_off_black_18dp, ""
                                                        + getResources().getString(R.string.deactive), pendingIntent2)
                                                .setAutoCancel(true);
                                        notificationManager.notify(0, notifBuilder.build());
                                    }
                                }
                        ).start();
                        Log.i("Alarm", "gpsAlarm() level 2");
                    }
                }
            }

        }

    }

    private void BDOldToBDNew(BDOld bdOld) {
        bdNew.deletarAllLines();
        for (Marcadores m : bdOld.buscar()) {
            bdNew.inserir(m);
        }
    }

    private void BDOldToBDNewDel(BDOld bdOld, double lat, double lng) {
        bdNew.deletarAllLines();
        for (Marcadores m : bdOld.buscar()) {
            if (m.getLatitude() != lat && m.getLongitude() != lng) {
                bdNew.inserir(m);
            }
        }
    }

    private void BDNewToBDOld(BDNew bdNew) {
        bdOld.deletarAllLines();
        for (Marcadores m : bdNew.buscar()) {
            bdOld.inserir(m);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        try {
            switch ( intent.getAction() ){
                case "more":
                    Log.e("TESTE PENDINGINTENT", "more");
                    for( Marcadores marcadores : bdNew.buscar() ) {
                        Log.e("marcadores", marcadores.getEndereco() );
                        Log.e("address", (String) intent.getExtras().get("address") );
                        if ( marcadores.getEndereco().equals(intent.getExtras().get("address")) ) {
                            marcadores.setDistancia((Long) intent.getExtras().get("distance") - 100);
                            notificationManager.cancel(0);
                            //bdOld.atualizar( marcadores );
                            bdNew.atualizar( marcadores );
                            BDNewToBDOld(bdNew);
                            Log.i("SCRIPT", "busca BDOld-->" + bdOld.buscar());
                            Log.i("SCRIPT", "busca BDNew-->" + bdNew.buscar());
                        }
                    }
                    break;
                case "deactivite":
                    Log.e("TESTE PENDINGINTENT","deactivite");
                    for( Marcadores marcadores : bdNew.buscar() ) {
                        if ( marcadores.getEndereco().equals(intent.getExtras().get("address")) ) {
                            marcadores.setAtivo(0);
                            notificationManager.cancel(0);
                            //bdOld.atualizar(marcadores);
                            bdNew.atualizar(marcadores);
                            BDNewToBDOld(bdNew);
                            Log.i("SCRIPT", "busca BDOld-->" + bdOld.buscar());
                            Log.i("SCRIPT", "busca BDNew-->" + bdNew.buscar());
                        }
                    }
                    break;
                case "null":
                    break;
            }
        }catch ( Exception e ) {

        }

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        notificationManager
                = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        bdNew = new BDNew(this);
        bdOld = new BDOld(this);
    }
    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }


}
