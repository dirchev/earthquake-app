package me.dirchev.mobile.earthquakeapp.models;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 February 21
 */
public class EarthquakesChannelImage {
    private String title;
    private String url;
    private String link;

    public EarthquakesChannelImage() {
    }

    public EarthquakesChannelImage(String title, String url, String link) {
        this.title = title;
        this.url = url;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
