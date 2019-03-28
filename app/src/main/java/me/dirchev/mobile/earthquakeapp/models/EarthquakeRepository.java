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
    Map<String, List<Earthquake>> earthquakesByDateList;
    Map<String, Object> filters;

    List<EarthquakeRepositoryChangeListener> changeListeners;
    int selectedEarthquakeIndex = -1;

    public static String getStringFromEarthquakePubDate (Date date) {
        return date.getDate() + "/" + date.getMonth() + "/" + date.getYear();
    }

    public EarthquakeRepository () {
        this.earthquakeList = new LinkedList<>();
        this.changeListeners = new LinkedList<>();
        this.earthquakesByDateList = new HashMap<>();
        this.filters = new HashMap<>();
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
        String textFilter = (String) filters.get("text");
        this.filteredEarthquakes = new LinkedList<>();
        for (Earthquake current : earthquakeList) {
            if (startDateFilter != null && current.getPubDate().before(startDateFilter)) continue;
            if (endDateFilter != null && current.getPubDate().after(endDateFilter)) continue;
            if (textFilter != null && !current.getLocationName().contains(textFilter)) continue;
            this.filteredEarthquakes.add(current);
        }
    }

    public void addEarthquake (Earthquake earthquake) {
        String dateString = this.getStringFromEarthquakePubDate(earthquake.getPubDate());
        this.earthquakeList.add(earthquake);
        if (earthquakesByDateList.containsKey(dateString)) {
            earthquakesByDateList.get(dateString).add(earthquake);
        } else {
            List<Earthquake> newListForDate = new LinkedList<>();
            newListForDate.add(earthquake);
            earthquakesByDateList.put(dateString, newListForDate);
            Log.e("MyTag/ new date", dateString);
        }
        processFilteredEarthquakes();
    }

    public Earthquake getSelectedEarthquake () {
        if (this.selectedEarthquakeIndex == -1) return null;
        return this.getVisibleEarthquakes().get(this.selectedEarthquakeIndex);
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
}
