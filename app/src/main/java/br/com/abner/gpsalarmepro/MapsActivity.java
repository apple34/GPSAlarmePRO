package br.com.abner.gpsalarmepro;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.nearby.messages.Strategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapLoadedCallback {

    private GoogleMap mMap;
    private List<Address> addresses = null, testeAddresses = null;
    private AlertDialog myDialog;
    private GoogleApiClient mGoogleApiClient;
    private Geocoder geocoder;
    private LatLng myLatLng;
    private AlertDialog alertDialog;
    private List<Address> addressMarker;
    private boolean boolMapType = false;
    private BDNew bdNew;
    private BDOld bdOld;
    private Marcadores marcadores;
    private AlertDialog.Builder builder;
    private List<Marcadores> currentMarcadores;
    private List<Circle> circles;
    private Circle circleMarker;
    private Intent intent;
    private CoordinatorLayout coordinatorLayout;
    protected static final String TAG = "LifeCycle";
    private List<String> textoNomes = new ArrayList<>();
    private List<String> textoEnderecos = new ArrayList<>();
    private List<Boolean> booleanAtivos = new ArrayList<>();
    private List<String> textoLatLng = new ArrayList<>();
    private MyAdapter myAdapter;
    private com.getbase.floatingactionbutton.FloatingActionButton minfbAtencao, minfb1, minfb2, minfb3;
    private FloatingActionsMenu menufb;
    private FloatingActionButton fab;
    private TextView textEndereco;
    private CheckBox checkBoxDomingo, checkBoxSegunda, checkBoxTerca, checkBoxQuarta, checkBoxQuinta
            , checkBoxSexta, checkBoxSabado;
    private Vibrator vibrator;
    private ProgressBar progressBar;
    private SphericalUtil sphericalUtil;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                if (isFirstStart) {

                    Intent i = new Intent(MapsActivity.this, Intro.class);
                    startActivity(i);

                    SharedPreferences.Editor e = getPrefs.edit();

                    e.putBoolean("firstStart", false);

                    e.apply();
                }
            }
        });

        t.start();

        ActivityCompat.requestPermissions(MapsActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLoadedCallback(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        geocoder = new Geocoder(this);
        bdNew = new BDNew(this);
        bdOld = new BDOld(this);
        marcadores = new Marcadores();
        currentMarcadores = bdNew.buscar();
        circles = new ArrayList();
        vibrator = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        sphericalUtil = new SphericalUtil();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                vibrator.vibrate(500);
                add(latLng);
            }
        });
        clickMarker(geocoder);
        buildGoogleApiClient();

        for (Marcadores m : bdNew.buscar()) {
            if ( m != null ) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(m.getLatitude(),
                        m.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(215.f))
                        .title(m.getNome()));
                addCircle(m);
                textoNomes.add(m.getNome());
                textoEnderecos.add(m.getEndereco());
                booleanAtivos.add(m.getAtivo() == 1? true : false);
                textoLatLng.add(m.getLatitude()+", "+m.getLongitude());
            }
        }

        myAdapter = new MyAdapter(MapsActivity.this, textoNomes, textoEnderecos);
        myAdapter.notifyDataSetChanged();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.titulo_alerta_lista_de_marcadores, null);
                builder.setCustomTitle(view);
                builder.setAdapter(myAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (Marcadores marcadores : bdNew.buscar()) {
                            if (marcadores != null) {
                                if (myAdapter.getItem(which).toString()
                                        .equals(marcadores.getEndereco())) {
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(marcadores.getLatitude(),
                                                    marcadores.getLongitude()), 15.0f));
                                }
                            }
                        }
                    }
                });
                myDialog = builder.create();
                myDialog.show();
            }
        });

        menufb = (FloatingActionsMenu) findViewById(R.id.menuFloat);
        menufb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    minfbAtencao.setVisibility(View.GONE);
                } else if (ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    minfbAtencao.setVisibility(View.VISIBLE);
                }
            }
        });

        minfbAtencao = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.minfabAlert);
        minfbAtencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menufb.collapse();
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        });

        minfb1 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.minfab1);
        minfb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menufb.collapse();
                if (!boolMapType) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    boolMapType = true;
                } else if (boolMapType) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    boolMapType = false;
                }
            }
        });

        minfb2 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.minfab2);
        minfb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    add(myLatLng);
                }catch (Exception e){

                }
            }
        });
        minfb3 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.minfab3);
        minfb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menufb.collapse();
                builder = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.titulo_alerta_sobre, null);
                myDialog = builder.setCustomTitle(view)
                        .setMessage(R.string.about)
                        .setNegativeButton(R.string.submit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(R.string.go_play_store, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                try {
                                    startActivity(goToMarket);
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("http://play.google.com/store/apps/details?id="
                                                    + getApplicationContext().getPackageName())));
                                }

                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                            }
                        })
                        .create();
                myDialog.show();
            }
        });

        if( !isMyServiceRunning(ServiceBackground.class) ){
            intent = new Intent(this, ServiceBackground.class);
            intent.setAction("null");
            startService(intent);
        }

        Log.i(TAG, getClassName() + ".onCreate() chamado");
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        Log.i(TAG, getClassName() + ".onResume() chamado");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, getClassName() + ".onStart() chamado");

        BroadcastReceiverLocation receiverLocation = new BroadcastReceiverLocation(myLatLng);
        LatLng latLng = receiverLocation.getLatLng();

        try {
            LatLngBounds.Builder b = new LatLngBounds.Builder();
            b.include(latLng);
            LatLngBounds myLatLngBounds = b.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(myLatLngBounds, 0));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        }catch (Exception e){
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, getClassName() + ".onRestart() chamado");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, getClassName() + ".onStop() chamado");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, getClassName() + ".onPause() chamado");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, getClassName() + ".onDestroy() chamado");
    }

    private String getClassName(){
        String s = getClass().getName();
        return s.substring(s.lastIndexOf("."));
    }

    private void add ( final LatLng latLng ){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] endereco = new String[1];
        final boolean[] booleanCheckboxArray = new boolean[7];
        final Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        final Circle circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(500)
                .strokeColor(0xFF00FF00)
                .strokeWidth(5)
                .fillColor(0x4400FF00));
        circle.setCenter(latLng);

        LatLngBounds.Builder b = new LatLngBounds.Builder();
        b.include(latLng);
        LatLngBounds latLngBounds = b.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),15.0f));

        try {
            addressMarker = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            endereco[0] = "" + addressMarker.get(0).getThoroughfare() + ", nº " + addressMarker.get(0)
                    .getSubThoroughfare();
        } catch (Exception e) {
            endereco[0] = "null::"+latLng.latitude+", "+latLng.longitude;
        }

        final int distanceInt;
        final int[] distanceProgress = new int[1];
        final String distanceString;

        distanceInt = (int) sphericalUtil.computeDistanceBetween(myLatLng, latLng);
        distanceString = " ("+ getResources().getString(R.string.at) + " "
                + String.valueOf(distanceInt) + " " + getResources().getString(R.string.meters) + ")";

        Snackbar snackbar = Snackbar.make(coordinatorLayout, endereco[0], Snackbar.LENGTH_LONG)
                .setDuration(5000)
                .setAction(R.string.add, new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onClick(View v) {
                        LayoutInflater layoutInflater = getLayoutInflater();
                        View view = layoutInflater.inflate(R.layout.titulo_alerta_add_marcador, null);

                        textEndereco = (TextView) view.findViewById(R.id.text_endereco);

                        if (endereco[0].contains("null::")) {
                            textEndereco.setText(latLng.latitude + ", " + latLng.longitude + distanceString);
                            builder.setCustomTitle(view);
                        } else {
                            textEndereco.setText(endereco[0] + distanceString);
                            builder.setCustomTitle(view);
                        }

                        final CharSequence[] alarme = new CharSequence[]{getResources().getString(R.string.activite_alarme)};
                        final boolean[] ativo = new boolean[alarme.length];
                        ativo[0] = true;
                        builder.setMultiChoiceItems(alarme, ativo, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                ativo[which] = isChecked;
                            }
                        });

                        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                        v = inflater.inflate(R.layout.seekbar, null);

                        checkBoxDomingo = (CheckBox) v.findViewById(R.id.checkbox_domingo);
                        checkBoxSegunda = (CheckBox) v.findViewById(R.id.checkbox_segunda);
                        checkBoxTerca = (CheckBox) v.findViewById(R.id.checkbox_terca);
                        checkBoxQuarta = (CheckBox) v.findViewById(R.id.checkbox_quarta);
                        checkBoxQuinta = (CheckBox) v.findViewById(R.id.checkbox_quinta);
                        checkBoxSexta = (CheckBox) v.findViewById(R.id.checkbox_sexta);
                        checkBoxSabado = (CheckBox) v.findViewById(R.id.checkbox_sabado);

                        builder.setView(v);

                        SeekBar sbBetVal = (SeekBar) v.findViewById(R.id.sbBetVal);
                        final TextView tvBetVal = (TextView) v.findViewById(R.id.tvBetVal);
                        final EditText etBetVal = (EditText) v.findViewById(R.id.etBetVal);
                        sbBetVal.setMax(50);
                        sbBetVal.setProgress(1);
                        sbBetVal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress,
                                                          boolean fromUser) {
                                // TODO Auto-generated method stub
                                distanceProgress[0] = 100 * progress;
                                marcadores.setDistancia(100 * progress);
                                tvBetVal.setText(String.valueOf(100 * progress) + " m");
                            }
                        });

                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.canceled), Toast.LENGTH_SHORT).show();
                            }
                        }).setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mMap.addMarker(new MarkerOptions().position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(215.f)));
                                if(distanceProgress[0] == 0){
                                    distanceProgress[0] = 500;
                                }
                                circleMarker = mMap.addCircle(new CircleOptions()
                                        .center(latLng)
                                        .radius(distanceProgress[0])
                                        .strokeColor(0xFF2196F3)
                                        .strokeWidth(5)
                                        .fillColor(0x442196F3)
                                        .center(latLng));
                                circles.add(circleMarker);
                                if (!etBetVal.getText().toString().isEmpty()) {
                                    marcadores.setNome(etBetVal.getText().toString());
                                } else {
                                    marcadores.setNome(getResources().getString(R.string.without_name));
                                }

                                booleanCheckboxArray[0] = checkBoxDomingo.isChecked();
                                booleanCheckboxArray[1] = checkBoxSegunda.isChecked();
                                booleanCheckboxArray[2] = checkBoxTerca.isChecked();
                                booleanCheckboxArray[3] = checkBoxQuarta.isChecked();
                                booleanCheckboxArray[4] = checkBoxQuinta.isChecked();
                                booleanCheckboxArray[5] = checkBoxSexta.isChecked();
                                booleanCheckboxArray[6] = checkBoxSabado.isChecked();

                                marcadores.setEndereco(endereco[0]);
                                marcadores.setLatitude(latLng.latitude);
                                marcadores.setLongitude(latLng.longitude);
                                marcadores.toLong(ativo[0]);
                                marcadores.setDiasDaSemana(arrayBooleanToStringNumber(booleanCheckboxArray));
                                bdOld.inserir(marcadores);
                                BDOldToBDNew(bdOld);
                                BDNewToBDOld(bdNew);
                                currentMarcadores.clear();
                                currentMarcadores = bdNew.buscar();
                                textoNomes.add(marcadores.getNome());
                                textoEnderecos.add(marcadores.getEndereco());
                                booleanAtivos.add(marcadores.getAtivo()==1?true:false);
                                textoLatLng.add(marcadores.getLatitude()+", "+marcadores.getLongitude());
                                Log.i("SCRIPT", "busca BDOld>" + bdOld.buscar());
                                Log.i("SCRIPT", "busca BDNew>" + bdNew.buscar());
                            }
                        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                            }
                        });
                        alertDialog = builder.create();
                        alertDialog.show();
                    }
                })
                .setActionTextColor(Color.GREEN)
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        marker.remove();
                        circle.remove();
                    }
                });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.argb(255, 33, 150, 243));
        snackbar.show();
    }

    private String arrayBooleanToStringNumber(boolean[] booleanArray){

        StringBuilder stringBuilder = new StringBuilder();
        int[] intArray = new int[booleanArray.length];

        for( int i=0; i<booleanArray.length; i++ ){
            if(booleanArray[i]){
                intArray[i] = 1;
            }else{
                intArray[i] = 0;
            }
        }

        for( int number : intArray ){
            stringBuilder.append(number);
        }

        return stringBuilder.toString();
    }

    private void addCircle(Marcadores m){
        circles.add(mMap.addCircle(new CircleOptions()
                .center(new LatLng(m.getLatitude(), m.getLongitude()))
                .radius(m.getDistancia())
                .strokeColor(0xFF2196F3)
                .strokeWidth(5)
                .fillColor(0x442196F3)));

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onMapLoaded() {
    }

    private class SearchFiltro implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            try {
                testeAddresses = geocoder.getFromLocationName(query, 1);
                add(new LatLng(testeAddresses.get(0).getLatitude(), testeAddresses.get(0).getLongitude()));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), R.string.local_not_found,
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }

    private void removeCircle(Marker rMarker){
        Circle circle = null;
        for(Circle c : circles){
            if(c.getCenter().equals(new LatLng(rMarker.getPosition().latitude,
                    rMarker.getPosition().longitude))){
                circle = c;
                c.remove();
                break;
            }
        }
        circles.remove(circle);
    }

    private void removeMarker(Marker rMarker) {
        removeCircle(rMarker);
        rMarker.remove();
        double lat = rMarker.getPosition().latitude;
        double lng = rMarker.getPosition().longitude;
        BDOldToBDNewDel(bdOld, lat, lng);
        BDNewToBDOld(bdNew);
        currentMarcadores.clear();
        currentMarcadores = bdNew.buscar();
        Log.i("SCRIPT", "busca BDOld-------->" + bdOld.buscar());
        Log.i("SCRIPT", "busca BDNew-------->" + bdNew.buscar());
    }

    private void clickMarker(final Geocoder geocoder) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        final boolean[] booleanCheckboxArray = new boolean[7];

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public int distance;
            final CharSequence[] alarme = new CharSequence[]{getResources().getString(R.string.active)};
            public boolean[] ativo = new boolean[alarme.length];


            @Override
            public boolean onMarkerClick(final Marker marker) {
                final LatLng latLng = new LatLng(marker.getPosition().latitude,
                        marker.getPosition().longitude);
                int progress = 1;
                final String[] endereco = new String[]{"null"};
                Marcadores marcadores = new Marcadores();

                for (Marcadores m : bdNew.buscar()) {
                    if (m.getLatitude().equals(latLng.latitude) &&
                            m.getLongitude().equals(latLng.longitude) &&
                            m.getAtivo() == 1) {
                        endereco[0] = m.getEndereco();
                        progress = (int) m.getDistancia();
                        ativo[0] = true;
                        marcadores = m;
                    } else if (m.getLatitude().equals(latLng.latitude) &&
                            m.getLongitude().equals(latLng.longitude) &&
                            m.getAtivo() == 0) {
                        endereco[0] = m.getEndereco();
                        progress = (int) m.getDistancia();
                        ativo[0] = false;
                        marcadores = m;
                    }
                }
                if( endereco[0].contains("null::") ){
                    try {
                        addressMarker = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        endereco[0] = "" + addressMarker.get(0).getThoroughfare() + ", nº "
                                + addressMarker.get(0).getSubThoroughfare();
                        marcadores.setEndereco(endereco[0]);
                        bdNew.atualizar(marcadores);
                        //bdOld.atualizar(marcadores);
                        BDNewToBDOld(bdNew);
                        Log.i("Banco atualizado","com o endereço "+endereco[0]);
                    } catch (Exception e) {
                    }
                }


                final int[] finalProgress = new int[1];
                finalProgress[0] = progress;
                final Marcadores finalMarcadores = marcadores;
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.titulo_alerta_clique_marcador, null);
                builder.setMessage(endereco[0])
                        .setCustomTitle(view)
                        .setPositiveButton(R.string.tools, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LayoutInflater layoutInflater = getLayoutInflater();
                                View view = layoutInflater.inflate(R.layout.titulo_alerta_editar_marcador, null);
                                builder2.setCustomTitle(view)
                                        .setMultiChoiceItems(alarme, ativo, new DialogInterface.OnMultiChoiceClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                                ativo[which] = isChecked;
                                            }
                                        });

                                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                                View v = inflater.inflate(R.layout.seekbar, null);

                                checkBoxDomingo = (CheckBox) v.findViewById(R.id.checkbox_domingo);
                                checkBoxSegunda = (CheckBox) v.findViewById(R.id.checkbox_segunda);
                                checkBoxTerca = (CheckBox) v.findViewById(R.id.checkbox_terca);
                                checkBoxQuarta = (CheckBox) v.findViewById(R.id.checkbox_quarta);
                                checkBoxQuinta = (CheckBox) v.findViewById(R.id.checkbox_quinta);
                                checkBoxSexta = (CheckBox) v.findViewById(R.id.checkbox_sexta);
                                checkBoxSabado = (CheckBox) v.findViewById(R.id.checkbox_sabado);

                                boolean[] booleenArray = stringNumberToArrayBoolean(finalMarcadores.getDiasDaSemana());
                                checkBoxDomingo.setChecked(booleenArray[0]);
                                checkBoxSegunda.setChecked(booleenArray[1]);
                                checkBoxTerca.setChecked(booleenArray[2]);
                                checkBoxQuarta.setChecked(booleenArray[3]);
                                checkBoxQuinta.setChecked(booleenArray[4]);
                                checkBoxSexta.setChecked(booleenArray[5]);
                                checkBoxSabado.setChecked(booleenArray[6]);

                                builder2.setView(v);

                                SeekBar sbBetVal = (SeekBar) v.findViewById(R.id.sbBetVal);
                                final TextView tvBetVal = (TextView) v.findViewById(R.id.tvBetVal);
                                final EditText etBetVal = (EditText) v.findViewById(R.id.etBetVal);
                                etBetVal.setHint("" + finalMarcadores.getNome());
                                sbBetVal.setMax(50);
                                sbBetVal.setProgress(finalProgress[0] / 100);
                                tvBetVal.setText(String.valueOf(finalProgress[0]) + " m");
                                sbBetVal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                                  boolean fromUser) {
                                        // TODO Auto-generated method stub
                                        Log.e("PROGRESS", String.valueOf(progress));
                                        finalProgress[0] = 100 * progress;
                                        Log.e("FINALPROGRESS", String.valueOf(finalProgress[0]));
                                        tvBetVal.setText(String.valueOf(100 * progress) + " m");
                                    }
                                });

                                builder2.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        for (Marcadores marcadores : bdNew.buscar()) {
                                            if (marcadores.getLatitude().equals(latLng.latitude) &&
                                                    marcadores.getLongitude().equals(latLng.longitude)) {

                                                if (!etBetVal.getText().toString().isEmpty()) {
                                                    marcadores.setNome(etBetVal.getText().toString());
                                                }

                                                booleanCheckboxArray[0] = checkBoxDomingo.isChecked();
                                                booleanCheckboxArray[1] = checkBoxSegunda.isChecked();
                                                booleanCheckboxArray[2] = checkBoxTerca.isChecked();
                                                booleanCheckboxArray[3] = checkBoxQuarta.isChecked();
                                                booleanCheckboxArray[4] = checkBoxQuinta.isChecked();
                                                booleanCheckboxArray[5] = checkBoxSexta.isChecked();
                                                booleanCheckboxArray[6] = checkBoxSabado.isChecked();

                                                marcadores.toLong(ativo[0]);
                                                marcadores.setDistancia(finalProgress[0]);
                                                marcadores.setDiasDaSemana(arrayBooleanToStringNumber(booleanCheckboxArray));
                                                //bdOld.atualizar(marcadores);
                                                bdNew.atualizar(marcadores);
                                                BDNewToBDOld(bdNew);
                                                for(int i = 0; i < textoNomes.size(); i++){
                                                    if(textoLatLng.get(i).contains(marcadores.getLatitude()+", "
                                                            +marcadores.getLongitude())){
                                                        textoNomes.remove(i);
                                                        textoNomes.add(marcadores.getNome());
                                                        textoEnderecos.remove(i);
                                                        textoEnderecos.add(marcadores.getEndereco());
                                                        booleanAtivos.remove(i);
                                                        booleanAtivos.add(marcadores.getAtivo()==1?true:false);
                                                        textoLatLng.remove(i);
                                                        textoLatLng.add(marcadores.getLatitude()+", "
                                                                +marcadores.getLongitude());
                                                        break;
                                                    }
                                                }
                                                removeCircle(marker);
                                                addCircle(marcadores);
                                                Log.i("SCRIPT", "busca BDOld-->" + bdOld.buscar());
                                                Log.i("SCRIPT", "busca BDNew-->" + bdNew.buscar());
                                            }
                                        }
                                    }
                                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                    }
                                });
                                myDialog = builder2.create();
                                myDialog.show();
                            }
                        }).setNegativeButton(R.string.del, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeMarker(marker);
                        for(int i = 0; i < textoNomes.size(); i++){
                            if(textoLatLng.get(i).equals(finalMarcadores.getLatitude()+", "
                                    +finalMarcadores.getLongitude())){
                                textoNomes.remove(i);
                                textoEnderecos.remove(i);
                                booleanAtivos.remove(i);
                                textoLatLng.remove(i);
                                break;
                            }
                        }
                    }
                });
                myDialog = builder.create();
                myDialog.show();
                return true;
            }
        });

    }

    public boolean[] stringNumberToArrayBoolean(String number){
        boolean[] arrayBoolean = new boolean[number.length()];

        for( int i=0; i<number.length(); i++ ){
            if( String.valueOf(number.charAt(i)).equals("1") ){
                arrayBoolean[i] = true;
            }else{
                arrayBoolean[i] = false;
            }
        }

        return arrayBoolean;
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
    }



    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.i("mLastLocation--->", "" + mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude());
            myLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            try {
                addresses = geocoder.getFromLocation(myLatLng.latitude, myLatLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void searchingForNull () {
        for ( Marcadores m : bdNew.buscar() ) {
            if ( m != null && m.getEndereco() == null ) {
                try {
                    addresses = geocoder.getFromLocation(m.getLatitude(), m.getLongitude(), 1);

                    String endereco = addresses.get(0).getThoroughfare()
                            + ", " + addresses.get(0).getSubThoroughfare();

                    m.setEndereco(endereco);

                    bdNew.atualizar(m);

                    Toast.makeText(getApplicationContext(), "Alguns endereços nulos foram atualizados.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Falha ao tentar traduzir endereços nulos." +
                            " Verifique sua conexão com a internet.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Você não está conectado à internet.");

        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setIcon(R.drawable.ic_search_white_24dp);
        SearchView sv = (SearchView) MenuItemCompat.getActionView(searchItem);
        sv.setOnQueryTextListener(new SearchFiltro());

        return super.onCreateOptionsMenu(menu);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
}
