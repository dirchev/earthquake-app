package me.dirchev.mobile.earthquakeapp;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import me.dirchev.mobile.earthquakeapp.UI.EarthquakesList.RAdapter;
import me.dirchev.mobile.earthquakeapp.data.EarthquakeLoader;
import me.dirchev.mobile.earthquakeapp.data.EarthquakeParsedEventListener;
import me.dirchev.mobile.earthquakeapp.models.Earthquake;
import me.dirchev.mobile.earthquakeapp.models.EarthquakeDataStore;
import me.dirchev.mobile.earthquakeapp.models.EarthquakeRepository;
import me.dirchev.mobile.earthquakeapp.models.EarthquakeRepositoryChangeListener;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 February 14
 */
public class MainActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;

    private TextView rawDataDisplay;
    private RecyclerView recyclerView;
    private Button startButton;
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startProgress();
        recyclerView = (RecyclerView) findViewById(R.id.earthquake_list_recycler_view);
    }

    public void startProgress()
    {
        EarthquakeLoader loader;
        loader = new EarthquakeLoader(urlSource, new EarthquakeParsedEventListener() {
            @Override
            public void run(final EarthquakeRepository earthquakeRepository) {
                MainActivity.this.runOnUiThread(new Runnable()
                {
                    public void run() {
                        EarthquakeDataStore dataStore = EarthquakeDataStore.getInstance();
                        dataStore.setRepository(earthquakeRepository);
                        RAdapter radapter = new RAdapter(MainActivity.this);
                        recyclerView.setAdapter(radapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(MainActivity.this);
                    }
                });
            }
        });
        new Thread(loader).start();
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap map = googleMap;
        EarthquakeDataStore dataStore = EarthquakeDataStore.getInstance();
        EarthquakeRepository earthquakeRepository = dataStore.getEarthquakeRepository();
        earthquakeRepository.subscribeToChange(new EarthquakeRepositoryChangeListener() {
            @Override
            public void onChange(EarthquakeRepository earthquakeRepository) {
                MainActivity.this.setMapLocationAndMarker(earthquakeRepository.getSelectedEarthquake(), map);
            }
        });
        this.setMapLocationAndMarker(earthquakeRepository.getSelectedEarthquake(), map);
    }

    private void setMapLocationAndMarker (Earthquake earthquake, GoogleMap googleMap) {
        LatLng place = earthquake.getLocation();
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(place).title("Here it is"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(earthquake.getLocation()));
    }
}