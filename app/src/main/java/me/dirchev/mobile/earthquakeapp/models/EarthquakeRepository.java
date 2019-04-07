package me.dirchev.mobile.earthquakeapp.models;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 March 01
 */
public class EarthquakeRepository {
    Map<String, Earthquake> earthquakeMap;
    List<Earthquake> earthquakeList;
    List<Earthquake> filteredEarthquakes;
    Map<String, Earthquake> statisticsEarthquakes;
    Map<String, Object> filters;
    List<EarthquakeRepositoryChangeListener> changeListeners;
    int selectedEarthquakeIndex = -1;
    private boolean loading = true;
    private Date updatedOn;
    private boolean fetchError;

    public EarthquakeRepository () {
        this.earthquakeMap = new HashMap<>();
        this.earthquakeList = new LinkedList<>();
        this.filteredEarthquakes = new LinkedList<>();
        this.statisticsEarthquakes = new HashMap<>();
        this.filters = new HashMap<>();
        this.changeListeners = new LinkedList<>();
    }

    /**
     * Takes the filters that are already set in this.filters
     * and the full list of earthquakes from this.earthquakeList
     * and processes them.
     * At the end filteredEarthquakes contains only earthquakes
     * that match the set filters and statisticsEarthquakes are
     * populated according to the filteredEarthquakes list.
     *
     * If the selected earthquake exists in the new filteredEarthquakes
     * list - it is saved. Otherwise, it is reset.
     */
    private void processFilteredEarthquakes () {
        Date startDateFilter = (Date) filters.get("startDate");
        Date endDateFilter = (Date) filters.get("endDate");
        statisticsEarthquakes = new HashMap<>();
        String textFilter = (String) filters.get("text");
        Earthquake previouslySelectedEarthquake = this.getSelectedEarthquake();
        this.filteredEarthquakes = new LinkedList<>();
        for (Earthquake current : earthquakeList) {
            if (startDateFilter != null && current.getPubDate().before(startDateFilter)) continue;
            if (endDateFilter != null && current.getPubDate().after(endDateFilter)) continue;
            if (textFilter != null && !current.getLocationName().contains(textFilter)) continue;
            // add to filtered earthquakes
            this.filteredEarthquakes.add(current);

            // check if the earthquake can be set to statisticsEarthquakes
            if (statisticsEarthquakes.get("North") == null || current.getLocation().latitude > statisticsEarthquakes.get("North").getLocation().latitude) {
                statisticsEarthquakes.put("North", current);
            }
            if (statisticsEarthquakes.get("South") == null || current.getLocation().latitude < statisticsEarthquakes.get("South").getLocation().latitude) {
                statisticsEarthquakes.put("South", current);
            }
            if (statisticsEarthquakes.get("East") == null || current.getLocation().longitude > statisticsEarthquakes.get("East").getLocation().longitude) {
                statisticsEarthquakes.put("East", current);
            }
            if (statisticsEarthquakes.get("West") == null || current.getLocation().longitude < statisticsEarthquakes.get("West").getLocation().longitude) {
                statisticsEarthquakes.put("West", current);
            }
            if (statisticsEarthquakes.get("Shallow") == null || current.getDepth().value < statisticsEarthquakes.get("Shallow").getDepth().value) {
                statisticsEarthquakes.put("Shallow", current);
            }
            if (statisticsEarthquakes.get("Deep") == null || current.getDepth().value > statisticsEarthquakes.get("Deep").getDepth().value) {
                statisticsEarthquakes.put("Deep", current);
            }
            if (statisticsEarthquakes.get("Magnitude") == null || current.getMagnitude() > statisticsEarthquakes.get("Magnitude").getMagnitude()) {
                statisticsEarthquakes.put("Magnitude", current);
            }
        }
        // try to restore the selected earthquake
        this.setSelectedEarthquake(previouslySelectedEarthquake);
    }

    /**
     * Sets the given earthquake as the selected earthquake.
     * @param earthquake earthquake to be saved as the selected earthquake
     */
    public void setSelectedEarthquake (Earthquake earthquake) {
        this.setSelectedEarthquakeIndex(this.filteredEarthquakes.indexOf(earthquake));
    }

    /**
     * Sets the earthquake on the specified index from the fitleredEarthquakes list as
     * the selected earthquake.
     * @param index the index of the earthquake from the filteredEarthquakes
     */
    public void setSelectedEarthquakeIndex (int index) {
        this.selectedEarthquakeIndex = index;
        this.updateAllListeners();
    }

    /**
     * Gets the selected earthquake from the filtered earthquakes list.
     * If the earthquake is not found in the list or if no earthquake was
     * selected - null is returned.
     * @return the selected earthquake or null
     */
    public Earthquake getSelectedEarthquake () {
        if (this.selectedEarthquakeIndex == -1) return null;
        return this.getFilteredEarthquakes().get(this.selectedEarthquakeIndex);
    }

    /**
     * Get the index of the selected earthquake from the filteredEarthquakes
     * list.
     * @return index of the earthquake. -1 if no earthquake is selected
     */
    public int getSelectedEarthquakeIndex() {
        return selectedEarthquakeIndex;
    }

    /**
     * Notifies all change listeners that the filters, selected earthquake or
     * earthquakes list has changed.
     */
    private void updateAllListeners () {
        for (EarthquakeRepositoryChangeListener listener : changeListeners) {
            listener.onChange(this);
        }
    }

    /**
     * Get filtered earthquakes
     * @return a list containing all earthquakes that match the filters
     */
    public List<Earthquake> getFilteredEarthquakes() {
        return filteredEarthquakes;
    }

    /**
     * Subscribe to changes in the filters, filteredEarthquakes or selected
     * earthquake.
     * @param listener listener to be invoked every time a change has occurred
     */
    public void subscribeToChange (EarthquakeRepositoryChangeListener listener) {
        this.changeListeners.add(listener);
    }

    /**
     * Unsubscribe from changes.
     * @param listener listener to be unsubscribed
     */
    public void unsubscribeToChange (EarthquakeRepositoryChangeListener listener) {
        this.changeListeners.remove(listener);
}

    /**
     * Get a map of the statistics earthquake.
     * The statistics are based on the filtered earthquakes list.
     * @return
     */
    public Map<String, Earthquake> getStatisticsMap() {
        return statisticsEarthquakes;
    }

    /**
     * Set a filter value.
     * The new filters are applied and the filteredEarthquakes list
     * is changed as well as the statisticsEartquakes map.
     * @param filterName name of the filter to be set
     * @param filterValue value of the filter to be set
     */
    public void setFilter(String filterName, Object filterValue) {
        if (filterValue != null) {
            this.filters.put(filterName, filterValue);
        } else {
            this.filters.remove(filterName);
        }
        processFilteredEarthquakes();
        updateAllListeners();
    }

    /**
     * Set a filter with the data from the given earthquake.
     * If the title is "date" - the start and end date will be
     * set to the earthquake's pubDate.
     * If the title is "location" - the text will be set to
     * the earthquake's location name
     * @param earthquake earthquake to get the information from
     * @param title the filter title to be applied
     */
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

    /**
     * Get the currently applied filters
     * @return map with the currently applied filters
     */
    public Map<String, Object> getFilters() {
        return this.filters;
    }

    /**
     * Refresh all earthquakes with the provided new earthquakes list.
     * Used when the data is re-fetched and the repository initial data has
     * to be updated.
     * This method sets the loading flag to false, updates the updatedOn date
     * and processes the earthquakes with the currently set filters.
     * @param fetchedEarthquakes
     */
    public void refreshEarthquakes(LinkedList<Earthquake> fetchedEarthquakes) {
        // iterate over the new earthquakes and check if the earthquake already
        // exists in the list. We want to have this check in order to keep the
        // already existing references and save the same selected earthquake
        boolean receivedNewEarthquakes = false;
        for (Earthquake earthquake : fetchedEarthquakes) {
            if (!this.earthquakeMap.containsKey(earthquake.getTitle())) {
                // earthquake is new, add it to the list
                this.earthquakeMap.put(earthquake.getTitle(), earthquake);
                this.earthquakeList.add(earthquake);
                receivedNewEarthquakes = true;
            }
        }
        this.fetchError = false;
        this.loading = false;
        this.updatedOn = new Date();
        // sort by pubDate and process filters again
        // only if new earthquakes have been added
        if (receivedNewEarthquakes) {
            Collections.sort(this.earthquakeList, new Comparator<Earthquake>() {
                @Override
                public int compare(Earthquake o1, Earthquake o2) {
                    return -1 * o1.getPubDate().compareTo(o2.getPubDate());
                }
            });
            this.processFilteredEarthquakes();
        } else {
            this.updateAllListeners();
        }
    }

    /**
     * Set the repository's loading state.
     * @param loading
     */
    public void setLoading(boolean loading) {
        this.loading = loading;
        this.updateAllListeners();
    }

    /**
     * Get the repository's loading state
     * @return
     */
    public boolean getLoading() {
        return loading;
    }

    /**
     * Get the latest date and time on which the full
     * earthquakes list has been updated.
     * @return
     */
    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setFetchError(boolean fetchError) {
        this.fetchError = fetchError;
        this.setLoading(false);
    }

    public boolean getFetchError() {
        return fetchError;
    }
}
