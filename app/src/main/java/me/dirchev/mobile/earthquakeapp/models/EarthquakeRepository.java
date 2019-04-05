package me.dirchev.mobile.earthquakeapp.models;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 March 01
 */
public class EarthquakeRepository {
    List<Earthquake> earthquakeList;
    List<Earthquake> filteredEarthquakes;
    Map<String, Earthquake> ewnsEarthquakes;
    Map<String, Object> filters;
    boolean showStatsOnly;

    List<EarthquakeRepositoryChangeListener> changeListeners;
    int selectedEarthquakeIndex = -1;

    public static String getStringFromEarthquakePubDate (Date date) {
        return date.getDate() + "/" + date.getMonth() + "/" + date.getYear();
    }

    public EarthquakeRepository () {
        this.earthquakeList = new LinkedList<>();
        this.changeListeners = new LinkedList<>();
        this.filters = new HashMap<>();
        this.filters = new HashMap<>();
        showStatsOnly = false;
    }

    public void toggleStats () {
        this.showStatsOnly = !this.showStatsOnly;
        processFilteredEarthquakes();
    }

    public void setFilter(String filterName, Object filterValue) {
        if (filterValue != null) {
            this.filters.put(filterName, filterValue);
        } else {
            this.filters.remove(filterName);
        }
        processFilteredEarthquakes();
        updateAllListeners();
    }

    private void processFilteredEarthquakes () {
        Date startDateFilter = (Date) filters.get("startDate");
        Date endDateFilter = (Date) filters.get("endDate");
        ewnsEarthquakes = new HashMap<>();
        String textFilter = (String) filters.get("text");
        this.filteredEarthquakes = new LinkedList<>();
        for (Earthquake current : earthquakeList) {
            if (startDateFilter != null && current.getPubDate().before(startDateFilter)) continue;
            if (endDateFilter != null && current.getPubDate().after(endDateFilter)) continue;
            if (textFilter != null && !current.getLocationName().contains(textFilter)) continue;
            // add to filtered earthquakes
            this.filteredEarthquakes.add(current);

            // check if EWNS
            if (ewnsEarthquakes.get("North") == null || current.getLocation().latitude > ewnsEarthquakes.get("North").getLocation().latitude) {
                ewnsEarthquakes.put("North", current);
            }
            if (ewnsEarthquakes.get("South") == null || current.getLocation().latitude < ewnsEarthquakes.get("South").getLocation().latitude) {
                ewnsEarthquakes.put("South", current);
            }
            if (ewnsEarthquakes.get("East") == null || current.getLocation().longitude > ewnsEarthquakes.get("East").getLocation().longitude) {
                ewnsEarthquakes.put("East", current);
            }
            if (ewnsEarthquakes.get("West") == null || current.getLocation().longitude < ewnsEarthquakes.get("West").getLocation().longitude) {
                ewnsEarthquakes.put("West", current);
            }
            if (ewnsEarthquakes.get("Shallow") == null || current.getDepth().value < ewnsEarthquakes.get("Shallow").getDepth().value) {
                ewnsEarthquakes.put("Shallow", current);
            }
            if (ewnsEarthquakes.get("Deep") == null || current.getDepth().value > ewnsEarthquakes.get("Deep").getDepth().value) {
                ewnsEarthquakes.put("Deep", current);
            }
            if (ewnsEarthquakes.get("Magnitude") == null || current.getMagnitude() > ewnsEarthquakes.get("Magnitude").getMagnitude()) {
                ewnsEarthquakes.put("Magnitude", current);
            }
        }
    }

    public void addEarthquake (Earthquake earthquake) {
        String dateString = this.getStringFromEarthquakePubDate(earthquake.getPubDate());
        this.earthquakeList.add(earthquake);
        List<Earthquake> newListForDate = new LinkedList<>();
        newListForDate.add(earthquake);
        processFilteredEarthquakes();
    }

    public Earthquake getSelectedEarthquake () {
        if (this.selectedEarthquakeIndex == -1) return null;
        return this.getVisibleEarthquakes().get(this.selectedEarthquakeIndex);
    }

    public void setSelectedEarthquake (Earthquake earthquake) {
        int index = this.filteredEarthquakes.indexOf(earthquake);
        if (index == -1) return;
        this.setSelectedEarthquakeIndex(this.filteredEarthquakes.indexOf(earthquake));
    }


    public void setSelectedEarthquakeIndex (int index) {
        this.selectedEarthquakeIndex = index;
        this.updateAllListeners();
    }

    private void updateAllListeners () {
        for (EarthquakeRepositoryChangeListener listener : changeListeners) {
            listener.onChange(this);
        }
    }

    public List<Earthquake> getVisibleEarthquakes () {
        return filteredEarthquakes;
    }

    public void subscribeToChange (EarthquakeRepositoryChangeListener listener) {
        this.changeListeners.add(listener);
    }

    public void unsubscribeToChange (EarthquakeRepositoryChangeListener listener) {
        this.changeListeners.remove(listener);
    }

    public String getFiltersInfoString () {
        LinkedList<String> resultList = new LinkedList<>();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
        if (this.filters.containsKey("text") && !((String) this.filters.get("text")).isEmpty()) {
            resultList.add("\"" + this.filters.get("text") + "\"");
        }
        if (this.filters.containsKey("startDate")) {
            resultList.add(dateFormat.format((Date) this.filters.get("startDate")));
        } else {
            resultList.add("beginning");
        }

        if (this.filters.containsKey("endDate")) {
            resultList.add(dateFormat.format((Date) this.filters.get("endDate")));
        } else {
            resultList.add("today");
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

    public String gerResultsInfoString () {
        int numberOfItems = this.getVisibleEarthquakes().size();
        if (numberOfItems == 0) {
            return "No earthquakes found for these filters";
        } else {
            return "Showing " + numberOfItems + " earthquakes.";
        }
    }

    @Override
    public String toString() {
        String result = "";
        for (Earthquake earthquake : earthquakeList) {
            result += earthquake.toString() + "\n============\n";
        }
        return result;
    }

    public Map<String, Earthquake> getEWNSMap() {
        return ewnsEarthquakes;
    }

    public int getSelectedEarthquakeIndex() {
        return selectedEarthquakeIndex;
    }

    public void setEarthquakeFilter(Earthquake earthquake, String title) {
        if (title.equals("date")) {
            Date date = earthquake.getPubDate();
            Date startDate = (Date) date.clone();
            startDate.setHours(0);
            startDate.setMinutes(0);
            startDate.setSeconds(0);
            Date endDate = (Date) startDate.clone();
            endDate.setDate(startDate.getDate() + 1);
            this.setFilter("startDate", startDate);
            this.setFilter("endDate", endDate);
        } else if (title.equals("location")) {
            this.setFilter("text", earthquake.getLocationName());
        }
    }

    public Map<String, Object> getFilters() {
        return this.filters;
    }
}
