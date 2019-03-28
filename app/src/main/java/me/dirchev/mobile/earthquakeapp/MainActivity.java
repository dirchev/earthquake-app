package me.dirchev.mobile.earthquakeapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.Date;
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
    private DatePickerDialog datePickerDialog;
    class DateInputOnFonusChangeListener implements View.OnFocusChangeListener {
        public void onFocusChange(final View v, boolean hasFocus) {
            final EditText currentDateInput = (EditText) v;
            int mYear, mMonth, mDay;

            if (hasFocus) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                currentDateInput.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            } else {
                datePickerDialog.hide();
                datePickerDialog = null;
            }
        }
    }
    private ViewGroup filtersButtonsContainer;
    private RecyclerView recyclerView;
    private TextView filtersInfo;
    private TextView resultsInfo;
    private EditText startDateInput;
    private EditText endDateInput;
    private EditText searchInput;
    private Button toggleFiltersButton;
    private Button closeFiltersButton;
    private Button clearFiltersButton;
    private Button updateFiltersButton;
    private SupportMapFragment mapFragment;
    private ViewGroup additionalFilters;
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    EarthquakeDataStore dataStore = EarthquakeDataStore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startProgress();
        recyclerView = (RecyclerView) findViewById(R.id.earthquake_list_recycler_view);
        filtersInfo = findViewById(R.id.filtersInfo);
        resultsInfo = findViewById(R.id.resultsInfo);
        additionalFilters = findViewById(R.id.additionalFilters);
        startDateInput = findViewById(R.id.startDateInput);
        endDateInput = findViewById(R.id.endDateInput);
        searchInput = findViewById(R.id.searchInput);
        DateInputOnFonusChangeListener dateInputOnFonusChangeListener = new DateInputOnFonusChangeListener();
        startDateInput.setOnFocusChangeListener(dateInputOnFonusChangeListener);
        endDateInput.setOnFocusChangeListener(dateInputOnFonusChangeListener);
        toggleFiltersButton = findViewById(R.id.toggleFiltersButton);
        closeFiltersButton = findViewById(R.id.closeFiltersButton);
        clearFiltersButton = findViewById(R.id.clearFiltersButton);
        updateFiltersButton = findViewById(R.id.updateFiltersButton);
        filtersButtonsContainer = findViewById(R.id.filtersButtonsContainer);

        closeFiltersButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFiltersVisibility(false);
            }
        });
        clearFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilters();
            }
        });
        updateFiltersButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                processFilters();
                updateFiltersVisibility(false);
            }
        });
        toggleFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFiltersVisibility(true);
            }
        });
    }

    private Date stringToDate (String dateString) {
        if (dateString.isEmpty()) return null;
        String[] dateStringParts = dateString.split("-");
        if (dateStringParts.length != 3) return null;
        Calendar cal = Calendar.getInstance();
        try {
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateStringParts[0]));
            cal.set(Calendar.MONTH, Integer.parseInt(dateStringParts[1]) - 1);
            cal.set(Calendar.YEAR, Integer.parseInt(dateStringParts[2]));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } catch (ParseException e) {
            return null;
        }

        return cal.getTime();
    }

    private void clearFilters () {
        startDateInput.setText(null);
        endDateInput.setText(null);
        searchInput.setText(null);
    }

    private void processFilters () {
        Date startDate = startDateInput.getText() == null
              ? null
              : stringToDate(startDateInput.getText().toString());
        Date endDate = endDateInput.getText() == null
              ? null
              : stringToDate(endDateInput.getText().toString());
        String searchTerm = searchInput.getText() == null
              ? null
              : searchInput.getText().toString();
        EarthquakeRepository earthquakeRepository = dataStore.getEarthquakeRepository();
        earthquakeRepository.setFilter("startDate", startDate);
        earthquakeRepository.setFilter("endDate", endDate);
        earthquakeRepository.setFilter("text", searchTerm);
    }

    private void updateFiltersVisibility (boolean isVisible) {
        if (isVisible) {
            toggleFiltersButton.setVisibility(View.GONE);
            mapFragment.getView().setVisibility(View.GONE);
            additionalFilters.setVisibility(View.VISIBLE);
            filtersInfo.setVisibility(View.GONE);
            searchInput.setVisibility(View.VISIBLE);
            filtersButtonsContainer.setVisibility(View.VISIBLE);
        } else {
            toggleFiltersButton.setVisibility(View.VISIBLE);
            mapFragment.getView().setVisibility(View.VISIBLE);
            additionalFilters.setVisibility(View.GONE);
            filtersInfo.setVisibility(View.VISIBLE);
            searchInput.setVisibility(View.GONE);
            filtersButtonsContainer.setVisibility(View.GONE);
        }
    }

    public void startProgress()
    {
        EarthquakeLoader loader;
        loader = new EarthquakeLoader(urlSource, dataStore.getEarthquakeRepository(), new EarthquakeParsedEventListener() {
            @Override
            public void run(final EarthquakeRepository earthquakeRepository) {
                MainActivity.this.runOnUiThread(new Runnable()
                {
                    public void run() {
                        final RAdapter radapter = new RAdapter(MainActivity.this);
                        recyclerView.setAdapter(radapter);
                        earthquakeRepository.subscribeToChange(new EarthquakeRepositoryChangeListener() {
                            @Override
                            public void onChange(EarthquakeRepository earthquakeRepository) {
                                radapter.notifyDataSetChanged();
                                filtersInfo.setText(earthquakeRepository.getFiltersInfoString());
                                resultsInfo.setText(earthquakeRepository.gerResultsInfoString());

                            }
                        });
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
        googleMap.clear();
        if (earthquake == null) return;
        LatLng place = earthquake.getLocation();
//        googleMap.addMarker(new MarkerOptions().position(place).title("Here it is"));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(earthquake.getLocation())
                .zoom(10)
                .build();
        CircleOptions circleOptions = new CircleOptions()
                .center(earthquake.getLocation())
                .radius(Math.abs(earthquake.getMagnitude()) * 1000);
        Circle circle = googleMap.addCircle(circleOptions);
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}