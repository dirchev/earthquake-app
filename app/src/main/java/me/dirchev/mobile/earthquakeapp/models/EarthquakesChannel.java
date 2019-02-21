package me.dirchev.mobile.earthquakeapp.models;

import java.util.LinkedList;
import java.util.List;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 February 21
 */
public class EarthquakesChannel {
    private String title;
    private String link;
    private String description;
    private String language;
    private String lastBuildDate;
    private EarthquakesChannelImage image;
    private List<Earthquake> earthquakes;

    public EarthquakesChannel() {
        this.earthquakes = new LinkedList<>();
    }


    public EarthquakesChannel(String title, String link, String description, String language, String lastBuildDate, EarthquakesChannelImage image, List<Earthquake> earthquakes) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.language = language;
        this.lastBuildDate = lastBuildDate;
        this.image = image;
        this.earthquakes = earthquakes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public EarthquakesChannelImage getImage() {
        return image;
    }

    public void setImage(EarthquakesChannelImage image) {
        this.image = image;
    }

    public List<Earthquake> getEarthquakes() {
        return earthquakes;
    }

    public void setEarthquakes(List<Earthquake> earthquakes) {
        this.earthquakes = earthquakes;
    }
}
