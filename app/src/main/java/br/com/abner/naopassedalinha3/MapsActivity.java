package br.com.abner.naopassedalinha3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener{

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
    private Vibrator vibrator;
    private AudioManager audioManager;
    private int statusAudio;
    private SubActionButton bnt3;
    private List<Marcadores> currentMarcadores;
    private List<Circle> circles;
    private Circle circleMarker;
    private Intent intent;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private CoordinatorLayout coordinatorLayout;
    protected static final String TAG = "LifeCycle";
    private boolean menuIsOpen = false;
    private List<String> textoNomes = new ArrayList<>();
    private List<String> textoEnderecos = new ArrayList<>();
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        mMap.setMyLocationEnabled(true);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        statusAudio = audioManager.getRingerMode();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        geocoder = new Geocoder(this);
        bdNew = new BDNew(this);
        bdOld = new BDOld(this);
        marcadores = new Marcadores();
        currentMarcadores = bdNew.buscar();
        circles = new ArrayList();

        mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                add(latLng);
            }
        });

        clickMarker(geocoder);
        buildGoogleApiClient();

        for (Marcadores m : bdNew.buscar()) {
            if ( m != null ) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(m.getLatitude(), m.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(215.f)));
                addCircle(m);
                textoNomes.add(m.getNome());
                textoEnderecos.add(m.getEndereco());
            }
        }

        myAdapter = new MyAdapter(MapsActivity.this, textoNomes, textoEnderecos);
        myAdapter.notifyDataSetChanged();

        FloatingActionButton fb = (FloatingActionButton) findViewById(R.id.fab);
        fb.setBackgroundTintList(getResources().getColorStateList(R.color.blue));
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.titulo_alerta_lista_de_marcadores, null);
                builder.setCustomTitle(view)
                        .setAdapter(myAdapter, new DialogInterface.OnClickListener() {
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


        final FloatingActionButton minfb1 = (FloatingActionButton) findViewById(R.id.minfab1);
        minfb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "TESTE", Toast.LENGTH_LONG).show();
            }
        });

        final FloatingActionButton minfb2 = (FloatingActionButton) findViewById(R.id.minfab2);
        final FloatingActionButton minfb3 = (FloatingActionButton) findViewById(R.id.minfab3);

        final FloatingActionButton fb1 = (FloatingActionButton) findViewById(R.id.fab1);
        fb1.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        fb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuIsOpen) {
                    fb1.animate().rotation(90);
                    /*minfb1.setVisibility(View.GONE);
                    minfb2.setVisibility(View.GONE);
                    minfb3.setVisibility(View.GONE);*/
                    menuIsOpen = false;
                } else {
                    fb1.animate().rotation(45);
                    /*minfb1.setVisibility(View.VISIBLE);
                    minfb2.setVisibility(View.VISIBLE);
                    minfb3.setVisibility(View.VISIBLE);*/
                    menuIsOpen = true;
                }
            }
        });



        intent = new Intent(this, ServiceBackground.class);
        intent.setAction("null");
        startService(intent);

        Log.i(TAG, getClassName() + ".onCreate() chamado");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        Log.i(TAG, getClassName() + ".onResume() chamado");
        try {
            LatLngBounds.Builder b = new LatLngBounds.Builder();
            b.include(myLatLng);
            LatLngBounds myLatLngBounds = b.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(myLatLngBounds, 0));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15.0f));
        }catch (Exception e){
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, getClassName() + ".onStart() chamado");
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

    private void floatingButton() {
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        SubActionButton bnt1 = itemBuilder.setLayoutParams(new FrameLayout.LayoutParams(50, 50))
                .setBackgroundDrawable(getDrawable(R.drawable.info))
                .build();
        bnt3 = itemBuilder.setLayoutParams(new FrameLayout.LayoutParams(50, 50))
                .build();
        if(statusAudio == 1){
            bnt3.setBackgroundDrawable(getDrawable(R.drawable.ic_vibration_white_48dp));
        }else if(statusAudio == 2){
            bnt3.setBackgroundDrawable(getDrawable(R.drawable.music));
        }else if(statusAudio == 3){
            bnt3.setBackgroundDrawable(getDrawable(R.drawable.no_audio));
        }
        SubActionButton bnt2 = itemBuilder.setLayoutParams(new FrameLayout.LayoutParams(50, 50))
                .setBackgroundDrawable(getDrawable(R.drawable.icon3))
                .build();

        bnt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(myLatLng);
            }
        });

        SubActionButton bnt4 = itemBuilder.setLayoutParams(new FrameLayout.LayoutParams(50, 50))
                .setBackgroundDrawable(getDrawable(R.drawable.map))
                .build();

        bnt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(MapsActivity.this);
                myDialog = builder.setIcon(R.drawable.info)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.about)
                        .setNegativeButton(R.string.submit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(R.string.go_play_store, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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

        bnt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusAudio == 1){
                    bnt3.setBackgroundDrawable(getDrawable(R.drawable.music));
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    statusAudio = audioManager.getRingerMode();
                }else if(statusAudio == 2){
                    bnt3.setBackgroundDrawable(getDrawable(R.drawable.ic_vibration_white_48dp));
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    vibrator.vibrate(500);
                    statusAudio = audioManager.getRingerMode();
                }else if(statusAudio == 3){
                    bnt3.setBackgroundDrawable(getDrawable(R.drawable.no_audio));
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    statusAudio = audioManager.getRingerMode();
                }
            }
        });

        bnt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!boolMapType) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    boolMapType = true;
                } else if (boolMapType) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    boolMapType = false;
                }
            }
        });

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(bnt1)
                .addSubActionView(bnt2)
                .addSubActionView(bnt3)
                .addSubActionView(bnt4)
                .attachTo(findViewById(R.id.fab))
                .build();
    }
//    public void lastAddrees() {
//        BDNewCore bdNewCore = new BDNewCore(getApplicationContext());
//        SQLiteDatabase sqLiteDatabase = bdNewCore.getWritableDatabase();
//        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM NewTable WHERE _id = " +
//                "(SELECT MAX(_id) FROM NewTable)", null);
//        sv.setSuggestionsAdapter(new TodoCursorAdapter(this,cursor));
//    }

    private void add ( final LatLng latLng ){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] endereco = new String[1];
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
            endereco[0] = "null";
        }

        final int distanceInt;
        final int[] distanceProgress = new int[1];
        final String distanceString;

        distanceInt = (int) SphericalUtil.computeDistanceBetween(myLatLng, latLng);
        distanceString = " (à " + String.valueOf(distanceInt) + " metros)";

        Snackbar snackbar = Snackbar.make(coordinatorLayout, endereco[0], Snackbar.LENGTH_LONG)
                .setDuration(5000)
                .setAction(R.string.add, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.setIcon(R.drawable.map_marker);

                        if (endereco[0] != "null") {
                            builder.setTitle(endereco[0] + distanceString);
                        } else {
                            builder.setTitle(latLng.latitude + ", " + latLng.longitude + distanceString);
                        }

                        final CharSequence[] alarme = new CharSequence[]{"Ativar alarme"};
                        final boolean[] ativo = new boolean[alarme.length];
                        builder.setMultiChoiceItems(alarme, ativo, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                ativo[which] = isChecked;
                            }
                        });

                        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                        v = inflater.inflate(R.layout.seekbar, null);

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
                                Toast.makeText(getApplicationContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                            }
                        }).setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mMap.addMarker(new MarkerOptions().position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(215.f)));
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
                                    marcadores.setNome(marcadores.getNome());
                                }
                                marcadores.setEndereco(endereco[0]);
                                marcadores.setLatitude(latLng.latitude);
                                marcadores.setLongitude(latLng.longitude);
                                marcadores.toLong(ativo[0]);
                                bdOld.inserir(marcadores);
                                BDOldToBDNew(bdOld);
                                ;
                                currentMarcadores.clear();
                                currentMarcadores = bdNew.buscar();
                                textoNomes.add(marcadores.getNome());
                                textoEnderecos.add(marcadores.getEndereco());
                                Log.i("SCRIPT", "busca BDOld-->" + bdOld.buscar());
                                Log.i("SCRIPT", "busca BDNew-->" + bdNew.buscar());
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

    private class SearchFiltro implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            try {
                testeAddresses = geocoder.getFromLocationName(query, 1);
                add(new LatLng(testeAddresses.get(0).getLatitude(), testeAddresses.get(0).getLongitude()));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), R.string.local_not_found, Toast.LENGTH_SHORT).show();
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
        System.out.println("qtdCircles: " + circles.size());
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
        Log.i("SCRIPT", "busca BDOld-->" + bdOld.buscar());
        Log.i("SCRIPT", "busca BDNew-->" + bdNew.buscar());
    }

    private void clickMarker(final Geocoder geocoder) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public int distance;
            final CharSequence[] alarme = new CharSequence[]{"Ativo"};
            public boolean[] ativo = new boolean[alarme.length];


            @Override
            public boolean onMarkerClick(final Marker marker) {
                final LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                int progress = 1;
                final String[] endereco = new String[1];
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
                if( endereco[0].equals("null") ){
                    try {
                        addressMarker = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        endereco[0] = "" + addressMarker.get(0).getThoroughfare() + ", nº " + addressMarker.get(0)
                                .getSubThoroughfare();
                        marcadores.setEndereco(endereco[0]);
                        bdNew.atualizar(marcadores);
                        bdOld.atualizar(marcadores);
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
                                                marcadores.toLong(ativo[0]);
                                                marcadores.setDistancia(finalProgress[0]);
                                                bdOld.atualizar(marcadores);
                                                bdNew.atualizar(marcadores);
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
                                    if(textoEnderecos.get(i).equals(finalMarcadores.getEndereco())){
                                        textoNomes.remove(i);
                                        textoEnderecos.remove(i);
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
