package me.dirchev.mobile.earthquakeapp;

import android.app.DatePickerDialog;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    private ViewGroup searchBox;
    private ViewGroup filtersSummary;
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
        searchBox = findViewById(R.id.searchBox);
        filtersSummary = findViewById(R.id.filtersSummary);
        recyclerView = findViewById(R.id.earthquake_list_recycler_view);
        filtersInfo = findViewById(R.id.filtersInfo);
        resultsInfo = findViewById(R.id.resultsInfo);
        additionalFilters = findViewById(R.id.additionalFilters);
        startDateInput = findViewById(R.id.startDateInput);
        endDateInput = findViewById(R.id.endDateInput);
        searchInput = findViewById(R.id.searchInput);
        toggleFiltersButton = findViewById(R.id.toggleFiltersButton);
        closeFiltersButton = findViewById(R.id.closeFiltersButton);
        clearFiltersButton = findViewById(R.id.clearFiltersButton);
        updateFiltersButton = findViewById(R.id.updateFiltersButton);

        DateInputOnFonusChangeListener dateInputOnFonusChangeListener = new DateInputOnFonusChangeListener();
        startDateInput.setOnFocusChangeListener(dateInputOnFonusChangeListener);
        endDateInput.setOnFocusChangeListener(dateInputOnFonusChangeListener);

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

    private void updateFiltersInput () {
        EarthquakeRepository earthquakeRepository = dataStore.getEarthquakeRepository();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
        Date startDate = (Date) earthquakeRepository.getFilters().get("startDate");
        Date endDate = (Date) earthquakeRepository.getFilters().get("endDate");
        String text = (String) earthquakeRepository.getFilters().get("text");
        startDateInput.setText(startDate == null ? "" : format.format(startDate));
        endDateInput.setText(endDate == null ? "" : format.format(endDate));
        searchInput.setText(text == null ? "" : text);
    }

    private void updateFiltersVisibility (boolean isVisible) {
        if (isVisible) {
            searchBox.setVisibility(View.VISIBLE);
            filtersSummary.setVisibility(View.GONE);
            mapFragment.getView().setVisibility(View.GONE);
        } else {
            searchBox.setVisibility(View.GONE);
            filtersSummary.setVisibility(View.VISIBLE);
            mapFragment.getView().setVisibility(View.VISIBLE);
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
                                recyclerView.scrollToPosition(earthquakeRepository.getSelectedEarthquakeIndex());
                                radapter.notifyDataSetChanged();
                                filtersInfo.setText(earthquakeRepository.getFiltersInfoString());
                                resultsInfo.setText(earthquakeRepository.getResultsInfoString());
                                updateFiltersInput();

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
                MainActivity.this.setMapLocationAndMarkers(earthquakeRepository, map);
            }
        });
        this.setMapLocationAndMarkers(earthquakeRepository, map);
    }

    private void setMapLocationAndMarkers (final EarthquakeRepository earthquakeRepository, GoogleMap googleMap) {
        Earthquake selectedEarthquake = earthquakeRepository.getSelectedEarthquake();
        googleMap.clear();

        for (Earthquake current : earthquakeRepository.getVisibleEarthquakes()) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                                     .position(current.getLocation()));
            marker.setTag(current);
            if (current.equals(selectedEarthquake)) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                earthquakeRepository.setSelectedEarthquake((Earthquake) marker.getTag());
                return true;
            }
        });

        CameraPosition.Builder cameraPositionBuilder = new CameraPosition.Builder();
        if (selectedEarthquake == null) {
            final LatLng UK_COORDINATES = new LatLng(55.1719958,-6.2549709);
            // center map
            cameraPositionBuilder.target(UK_COORDINATES).zoom(5);
        } else {
            // center on earthquake location
            cameraPositionBuilder.target(selectedEarthquake.getLocation()).zoom(8);
        }
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionBuilder.build()));
    }

    public void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                EarthquakeRepository repository = MainActivity.this.dataStore.getEarthquakeRepository();
                switch (item.getItemId()) {
                    case R.id.southest:
                        repository.setSelectedEarthquake(repository.getEWNSMap().get("South"));
                        return true;
                    case R.id.eastest:
                        repository.setSelectedEarthquake(repository.getEWNSMap().get("East"));
                        return true;
                    case R.id.westest:
                        repository.setSelectedEarthquake(repository.getEWNSMap().get("West"));
                        return true;
                    case R.id.northest:
                        repository.setSelectedEarthquake(repository.getEWNSMap().get("North"));
                        return true;
                    case R.id.shallow:
                        repository.setSelectedEarthquake(repository.getEWNSMap().get("Shallow"));
                        return true;
                    case R.id.deepest:
                        repository.setSelectedEarthquake(repository.getEWNSMap().get("Deep"));
                        return true;
                    case R.id.magnitude:
                        repository.setSelectedEarthquake(repository.getEWNSMap().get("Magnitude"));
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.inflate(R.menu.statistics_menu);
        popupMenu.show();
    }
}