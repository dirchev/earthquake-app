package me.dirchev.mobile.earthquakeapp.models;

import android.content.Context;

import com.github.javafaker.Faker;
import com.google.android.gms.maps.model.LatLng;

import org.apache.tools.ant.taskdefs.Ear;
import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 April 07
 */
public class EarthquakeRepositoryTest {

    /**
     * Generates a list of earthquakes with the specified size.
     * Each earthquake is randomly generated using Faker
     * @param size size of the list to be generated
     * @return list of randomly generated earthquakes
     */
    private LinkedList<Earthquake> getFakeEarthquakes (int size) {
        Faker faker = new Faker();

        LinkedList<Earthquake> earthquakes = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            Earthquake aEarthquake = new Earthquake();
            aEarthquake.setCategory(faker.lorem().word());
            aEarthquake.setLink(faker.internet().url());
            aEarthquake.setTitle(faker.lorem().sentence(5));
            aEarthquake.setPubDate(faker.date().past(10, TimeUnit.DAYS));
            aEarthquake.setDepth(new Earthquake.Depth(faker.number().numberBetween(1, 10), "km"));
            aEarthquake.setLocation(new LatLng(faker.number().numberBetween(0, 20), faker.number().numberBetween(0, 20)));
            aEarthquake.setLocationName(faker.address().cityName() + "," + faker.address().country());
            aEarthquake.setMagnitude(faker.number().randomDouble(2, 0, 7));
            earthquakes.add(aEarthquake);
        }
        earthquakes.sort(new Comparator<Earthquake>() {
            @Override
            public int compare(Earthquake o1, Earthquake o2) {
                return o2.getPubDate().compareTo(o1.getPubDate());
            }
        });
        return earthquakes;
    }

    @Test
    public void setSelectedEarthquake() {
        EarthquakeRepository earthquakeRepository = new EarthquakeRepository();
        LinkedList<Earthquake> earthquakes = this.getFakeEarthquakes(10);
        earthquakeRepository.refreshEarthquakes(earthquakes);
        earthquakeRepository.setSelectedEarthquake(earthquakes.get(4));
        Assert.assertEquals(earthquakeRepository.getSelectedEarthquake(), earthquakes.get(4));
        Assert.assertEquals(earthquakeRepository.getSelectedEarthquakeIndex(), 4);
    }

    @Test
    public void setSelectedEarthquakeIndex() {
        EarthquakeRepository earthquakeRepository = new EarthquakeRepository();
        LinkedList<Earthquake> earthquakes = this.getFakeEarthquakes(10);
        earthquakeRepository.refreshEarthquakes(earthquakes);
        earthquakeRepository.setSelectedEarthquakeIndex(5);
        Assert.assertEquals(earthquakeRepository.getSelectedEarthquake(), earthquakes.get(5));
        Assert.assertEquals(earthquakeRepository.getSelectedEarthquakeIndex(), 5);
    }

    @Test
    public void getFilteredEarthquakes() {
        EarthquakeRepository earthquakeRepository = new EarthquakeRepository();
        LinkedList<Earthquake> earthquakes = this.getFakeEarthquakes(10);
        earthquakeRepository.refreshEarthquakes(earthquakes);
        Assert.assertEquals(earthquakeRepository.getFilteredEarthquakes(), earthquakes);
        earthquakeRepository.setEarthquakeFilter(earthquakes.get(5), "location");
        for (Earthquake current : earthquakeRepository.getFilteredEarthquakes()) {
            Assert.assertEquals(current.getLocationName(), earthquakes.get(5).getLocationName());
        }
    }

    @Test
    public void subscribeToChangeNotifiedOnEarthquakesRefresh() {
        final boolean[] listenerNotified = {false};
        EarthquakeRepository earthquakeRepository = new EarthquakeRepository();
        LinkedList<Earthquake> earthquakes = this.getFakeEarthquakes(10);
        earthquakeRepository.subscribeToChange(new EarthquakeRepositoryChangeListener() {
            @Override
            public void onChange(EarthquakeRepository earthquakeRepository) {
                listenerNotified[0] = true;
            }
        });
        earthquakeRepository.refreshEarthquakes(earthquakes);
        Assert.assertEquals(listenerNotified[0], true);
    }

    @Test
    public void subscribeToChangeNotifiedOnFiltersUpdate() {
        final boolean[] listenerNotified = {false};
        EarthquakeRepository earthquakeRepository = new EarthquakeRepository();
        LinkedList<Earthquake> earthquakes = this.getFakeEarthquakes(10);
        earthquakeRepository.refreshEarthquakes(earthquakes);
        earthquakeRepository.subscribeToChange(new EarthquakeRepositoryChangeListener() {
            @Override
            public void onChange(EarthquakeRepository earthquakeRepository) {
                listenerNotified[0] = true;
            }
        });
        earthquakeRepository.setFilter("text","test");
        Assert.assertEquals(listenerNotified[0], true);
    }

    @Test
    public void subscribeToChangeNotifiedOnSelectedEarthquakeChange() {
        final boolean[] listenerNotified = {false};
        EarthquakeRepository earthquakeRepository = new EarthquakeRepository();
        LinkedList<Earthquake> earthquakes = this.getFakeEarthquakes(10);
        earthquakeRepository.refreshEarthquakes(earthquakes);
        earthquakeRepository.subscribeToChange(new EarthquakeRepositoryChangeListener() {
            @Override
            public void onChange(EarthquakeRepository earthquakeRepository) {
                listenerNotified[0] = true;
            }
        });
        earthquakeRepository.setSelectedEarthquake(earthquakes.get(1));
        Assert.assertEquals(listenerNotified[0], true);
    }

    @Test
    public void subscribeToChangeNotifiedOnLoadingStateChange() {
        final boolean[] listenerNotified = {false};
        EarthquakeRepository earthquakeRepository = new EarthquakeRepository();
        LinkedList<Earthquake> earthquakes = this.getFakeEarthquakes(10);
        earthquakeRepository.refreshEarthquakes(earthquakes);
        earthquakeRepository.subscribeToChange(new EarthquakeRepositoryChangeListener() {
            @Override
            public void onChange(EarthquakeRepository earthquakeRepository) {
                listenerNotified[0] = true;
            }
        });
        earthquakeRepository.setLoading(false);
        Assert.assertEquals(listenerNotified[0], true);
    }

    @Test
    public void unsubscribeToChange() {
        final int[] notifiedTimes = {0};
        EarthquakeRepository earthquakeRepository = new EarthquakeRepository();
        EarthquakeRepositoryChangeListener listener = new EarthquakeRepositoryChangeListener() {
            @Override
            public void onChange(EarthquakeRepository earthquakeRepository) {
                notifiedTimes[0]++;
            }
        };
        earthquakeRepository.subscribeToChange(listener);
        earthquakeRepository.setLoading(false);
        earthquakeRepository.unsubscribeToChange(listener);
        earthquakeRepository.setLoading(true);
        Assert.assertEquals(notifiedTimes[0], 1);
    }

    @Test
    public void getStatisticsMap() {
        EarthquakeRepository earthquakeRepository = new EarthquakeRepository();
        LinkedList<Earthquake> earthquakes = this.getFakeEarthquakes(4);

        Earthquake shallowestEarthquake = earthquakes.get(0);
        shallowestEarthquake.getDepth().value = 0; // the minimum depth from the faker is 1

        Earthquake deepestEarthquake = earthquakes.get(1);
        deepestEarthquake.getDepth().value = 100; // the maximum depth from the faker is 10

        Earthquake largestMagnitudeEarthquake = earthquakes.get(2);
        largestMagnitudeEarthquake.setMagnitude(100); // the max is 7 from the faker

        // generated lng and lat is 0-20,0-20
        Earthquake mostNortherlyEarthquake = earthquakes.get(0);
        mostNortherlyEarthquake.setLocation(new LatLng(150, 0));

        Earthquake mostSoutherlyEarthquake = earthquakes.get(1);
        mostSoutherlyEarthquake.setLocation(new LatLng(-150, 0));

        Earthquake mostEasterlyEarthquake = earthquakes.get(2);
        mostEasterlyEarthquake.setLocation(new LatLng(0, 150));

        Earthquake mostWesterlyEarthquake = earthquakes.get(3);
        mostWesterlyEarthquake.setLocation(new LatLng(0, -150));

        earthquakeRepository.refreshEarthquakes(earthquakes);

        Map<String, Earthquake> statistics = earthquakeRepository.getStatisticsMap();
        Assert.assertEquals(statistics.get("Deep"), deepestEarthquake);
        Assert.assertEquals(statistics.get("Shallow"), shallowestEarthquake);
        Assert.assertEquals(statistics.get("Magnitude"), largestMagnitudeEarthquake);
        Assert.assertEquals(statistics.get("East"), mostEasterlyEarthquake);
        Assert.assertEquals(statistics.get("West"), mostWesterlyEarthquake);
        Assert.assertEquals(statistics.get("North"), mostNortherlyEarthquake);
        Assert.assertEquals(statistics.get("South"), mostSoutherlyEarthquake);
    }

    @Test
    public void refreshEarthquakes() {
        EarthquakeRepository earthquakeRepository = new EarthquakeRepository();
        Assert.assertEquals(earthquakeRepository.getLoading(), true);
        Assert.assertNull(earthquakeRepository.getUpdatedOn());
        LinkedList<Earthquake> earthquakes = this.getFakeEarthquakes(10);
        earthquakeRepository.refreshEarthquakes(earthquakes);
        Assert.assertEquals(earthquakeRepository.getLoading(), false);
        Assert.assertNotNull(earthquakeRepository.getUpdatedOn());
    }

    @Test
    public void setFilter() {
        EarthquakeRepository earthquakeRepository = new EarthquakeRepository();
        LinkedList<Earthquake> earthquakes = this.getFakeEarthquakes(10);
        earthquakeRepository.refreshEarthquakes(earthquakes);
        earthquakeRepository.setEarthquakeFilter(earthquakes.get(1), "date");

        // inspect start date
        Date expectedStartDate = (Date) earthquakes.get(1).getPubDate().clone();
        expectedStartDate.setHours(0);
        expectedStartDate.setMinutes(0);
        expectedStartDate.setSeconds(0);
        Assert.assertEquals(earthquakeRepository.getFilters().get("startDate"), expectedStartDate);

        // inspect end date
        Date expectedEndDate = (Date) expectedStartDate.clone();
        expectedEndDate.setDate(expectedEndDate.getDate() + 1);
        Assert.assertEquals(earthquakeRepository.getFilters().get("endDate"), expectedEndDate);
    }

    @Test
    public void setEarthquakeFilter() {
    }

    @Test
    public void getFilters() {
    }

    @Test
    public void setLoading() {
    }

    @Test
    public void getLoading() {
    }

    @Test
    public void getUpdatedOn() {
    }
}