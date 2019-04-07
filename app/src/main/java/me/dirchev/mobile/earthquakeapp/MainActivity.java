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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.dirchev.mobile.earthquakeapp.UI.EarthquakesList.RAdapter;
import me.dirchev.mobile.earthquakeapp.data.EarthquakeLoader;
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

    class DateInputOnFocusChangeListener implements View.OnFocusChangeListener {
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

                // set min/max date constraints
                if (v.equals(startDateInput)) {
                    Date currentlySelectedEndDate = stringToDate(endDateInput.getText().toString());
                    Date maxDateConstraint = currentlySelectedEndDate == null || currentlySelectedEndDate.compareTo(new Date()) > 0
                            ? new Date()
                            : currentlySelectedEndDate;
                    datePickerDialog.getDatePicker().setMaxDate(maxDateConstraint.getTime());
                } else {
                    Date maxDateConstraint = new Date();
                    datePickerDialog.getDatePicker().setMaxDate(maxDateConstraint.getTime());
                    Date minDateConstraint = stringToDate(startDateInput.getText().toString());
                    if (minDateConstraint != null) {
                        datePickerDialog.getDatePicker().setMinDate(minDateConstraint.getTime());
                    }
                }
                datePickerDialog.show();
            } else {
                datePickerDialog.hide();
                datePickerDialog = null;
            }
        }
    }
    private RAdapter radapter;
    private ViewGroup searchBox;
    private ViewGroup filtersSummary;
    private RecyclerView recyclerView;
    private TextView filtersInfo;
    private TextView fetchInfoTextView;
    private Button reFetchButton;
    private TextView resultsInfo;
    private EditText startDateInput;
    private EditText endDateInput;
    private EditText searchInput;
    private Button toggleFiltersButton;
    private Button closeFiltersButton;
    private Button clearFiltersButton;
    private Button updateFiltersButton;
    private SupportMapFragment mapFragment;
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private URL url;
    EarthquakeDataStore dataStore = EarthquakeDataStore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
            url = new URL(urlSource);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not parse fetch URL", Toast.LENGTH_LONG);
        }
        setContentView(R.layout.activity_main);

        fetchInfoTextView = findViewById(R.id.fetchInfoTextView);
        searchBox = findViewById(R.id.searchBox);
        filtersSummary = findViewById(R.id.filtersSummary);
        recyclerView = findViewById(R.id.earthquake_list_recycler_view);
        filtersInfo = findViewById(R.id.filtersInfo);
        resultsInfo = findViewById(R.id.resultsInfo);
        startDateInput = findViewById(R.id.startDateInput);
        endDateInput = findViewById(R.id.endDateInput);
        searchInput = findViewById(R.id.searchInput);
        toggleFiltersButton = findViewById(R.id.toggleFiltersButton);
        closeFiltersButton = findViewById(R.id.closeFiltersButton);
        clearFiltersButton = findViewById(R.id.clearFiltersButton);
        reFetchButton = findViewById(R.id.reFetchButton);
        updateFiltersButton = findViewById(R.id.updateFiltersButton);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // set up the listing
        radapter = new RAdapter();
        recyclerView.setAdapter(radapter);

        // set up date inputs
        DateInputOnFocusChangeListener dateInputOnFocusChangeListener = new DateInputOnFocusChangeListener();
        startDateInput.setOnFocusChangeListener(dateInputOnFocusChangeListener);
        endDateInput.setOnFocusChangeListener(dateInputOnFocusChangeListener);

        reFetchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EarthquakeLoader loader = new EarthquakeLoader(dataStore.getEarthquakeRepository(), MainActivity.this);
                loader.execute(url);
            }
        });
        // set up filter buttons
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


        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment.getMapAsync(MainActivity.this);

        // fetch/process already fetched earthquakes
        processEarthquakeRepositoryChange();
        if (savedInstanceState == null) {
            startProgress();
        }
        dataStore.getEarthquakeRepository().subscribeToChange(new EarthquakeRepositoryChangeListener() {
            @Override
            public void onChange(EarthquakeRepository earthquakeRepository) {
                MainActivity.this.processEarthquakeRepositoryChange();
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
        if (endDate != null) {
            endDate.setHours(23);
            endDate.setMinutes(59);
            endDate.setSeconds(59);
        }
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
        SimpleDateFormat format = new SimpleDateFormat(getString(R.string.date_format));
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

    /**
     * Generates a result string based on the filtered
     * earthquakes
     * @return results info string
     */
    private String buildResultsInfoString(EarthquakeRepository earthquakeRepository) {
        int numberOfItems = earthquakeRepository.getFilteredEarthquakes().size();
        if (numberOfItems == 0) {
            return getString(R.string.no_earthquakes_found_message);
        } else {
            return String.format(getString(R.string.showing_number_earthquakes_message), numberOfItems);
        }
    }

    /**
     * Generates a summary text based on the filters
     * that have been applied.
     * @return summary of filters
     */
    private String buildFiltersInfoString (EarthquakeRepository earthquakeRepository) {
        LinkedList<String> resultList = new LinkedList<>();
        Map<String, Object> filters = earthquakeRepository.getFilters();
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format));
        if (filters.containsKey("text") && !((String) filters.get("text")).isEmpty()) {
            resultList.add("\"" + filters.get("text") + "\"");
        }
        if (filters.containsKey("startDate")) {
            resultList.add(dateFormat.format((Date) filters.get("startDate")));
        } else {
            resultList.add(getString(R.string.beginning));
        }

        if (filters.containsKey("endDate")) {
            resultList.add(dateFormat.format((Date) filters.get("endDate")));
        } else {
            resultList.add(getString(R.string.today));
        }

        String result = "";
        for (String item : resultList) {
            if (result.isEmpty()) {
                result += item;
            } else {
                result += " - " + item;
            }
        }
        return result;
    }

    private void processEarthquakeRepositoryChange () {
        EarthquakeRepository earthquakeRepository = dataStore.getEarthquakeRepository();
        int selectedEarthquakeIndex = earthquakeRepository.getSelectedEarthquakeIndex();
        if (selectedEarthquakeIndex != -1) {
            recyclerView.scrollToPosition(selectedEarthquakeIndex);
        }
        if (earthquakeRepository.getFetchError()) {
            fetchInfoTextView.setText(getString(R.string.earthquakes_fetch_error_message));
            reFetchButton.setVisibility(View.VISIBLE);
        } else if (earthquakeRepository.getLoading()) {
            fetchInfoTextView.setText(getString(R.string.loading_earthquakes_message));
            reFetchButton.setVisibility(View.GONE);
        } else {
            reFetchButton.setVisibility(View.VISIBLE);
            SimpleDateFormat format = new SimpleDateFormat(getString(R.string.datetime_format));
            fetchInfoTextView.setText(String.format(getString(R.string.updated_on_info_text), format.format(earthquakeRepository.getUpdatedOn())));
        }
        radapter.notifyDataSetChanged();
        filtersInfo.setText(buildFiltersInfoString(earthquakeRepository));
        resultsInfo.setText(buildResultsInfoString(earthquakeRepository));
        updateFiltersInput();
    }

    public void startProgress()
    {
        Timer timer = new Timer("earthquakes updater");
        final URL finalUrl = url;
        // schedule a task to be executed every X ms, starting now
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // run on the main thread so that the async task can
                // properly access the earthquake repository.
                // the asyncTask will again open a thread to do its
                // background taks.
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EarthquakeLoader loader = new EarthquakeLoader(dataStore.getEarthquakeRepository(), MainActivity.this);
                        loader.execute(finalUrl);
                    }
                });
            }
        }, 0, 1000 * 60 * 5); // every 5 minutes
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

        for (Earthquake current : earthquakeRepository.getFilteredEarthquakes()) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                                     .position(current.getLocation()));
            marker.setTag(current);
            if (current.equals(selectedEarthquake)) {
                marker.setZIndex(2);
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


        if (earthquakeRepository.getUpdatedOn() == null || earthquakeRepository.getFilteredEarthquakes().size() == 0) {
            final LatLng UK_COORDINATES = new LatLng(55.1719958,-6.2549709);
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(UK_COORDINATES).zoom(5).build()));
            return;
        }

        if (selectedEarthquake != null) {
            CameraPosition.Builder cameraPositionBuilder = new CameraPosition.Builder().target(selectedEarthquake.getLocation());
            if (googleMap.getCameraPosition().zoom < 10) {
                cameraPositionBuilder.zoom(10);
            } else {
                cameraPositionBuilder.zoom(googleMap.getCameraPosition().zoom);
            }
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPositionBuilder.build());
            googleMap.animateCamera(cameraUpdate);
            return;
        }

        LatLngBounds.Builder cameraBoundsBuilder;
        int cameraPadding;
        // calculate camera update, that will fit all of the filtered earthquakes
        cameraBoundsBuilder = new LatLngBounds.Builder()
            .include(earthquakeRepository.getStatisticsMap().get("East").getLocation())
            .include(earthquakeRepository.getStatisticsMap().get("West").getLocation())
            .include(earthquakeRepository.getStatisticsMap().get("North").getLocation())
            .include(earthquakeRepository.getStatisticsMap().get("South").getLocation());
        cameraPadding = 200;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(cameraBoundsBuilder.build(), cameraPadding);
        googleMap.animateCamera(cameraUpdate);
    }

    public void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                EarthquakeRepository repository = MainActivity.this.dataStore.getEarthquakeRepository();
                switch (item.getItemId()) {
                    case R.id.southest:
                        repository.setSelectedEarthquake(repository.getStatisticsMap().get("South"));
                        return true;
                    case R.id.eastest:
                        repository.setSelectedEarthquake(repository.getStatisticsMap().get("East"));
                        return true;
                    case R.id.westest:
                        repository.setSelectedEarthquake(repository.getStatisticsMap().get("West"));
                        return true;
                    case R.id.northest:
                        repository.setSelectedEarthquake(repository.getStatisticsMap().get("North"));
                        return true;
                    case R.id.shallow:
                        repository.setSelectedEarthquake(repository.getStatisticsMap().get("Shallow"));
                        return true;
                    case R.id.deepest:
                        repository.setSelectedEarthquake(repository.getStatisticsMap().get("Deep"));
                        return true;
                    case R.id.magnitude:
                        repository.setSelectedEarthquake(repository.getStatisticsMap().get("Magnitude"));
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