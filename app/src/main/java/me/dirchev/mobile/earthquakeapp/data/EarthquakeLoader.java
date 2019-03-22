package me.dirchev.mobile.earthquakeapp.data;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import me.dirchev.mobile.earthquakeapp.models.Earthquake;
import me.dirchev.mobile.earthquakeapp.models.EarthquakeRepository;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 February 21
 */
public class EarthquakeLoader implements Runnable {
    private String url;
    private EarthquakeParsedEventListener onReady;
    private EarthquakeRepository repository;

    public EarthquakeLoader(String url, EarthquakeRepository repository, EarthquakeParsedEventListener onReady) {
        this.url = url;
        this.onReady = onReady;
        this.repository = repository;
    }

    private String getXML () {
        URL aurl;
        URLConnection yc;
        BufferedReader in = null;
        String inputLine = "";
        String xmlResult = "";

        try
        {
            aurl = new URL(url);
            yc = aurl.openConnection();
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            while ((inputLine = in.readLine()) != null)
            {
                xmlResult = xmlResult + inputLine;

            }
            in.close();
        }
        catch (IOException ae)
        {
            Log.e("MyTag", "ioexception");
        }
        return xmlResult;
    }

    private void parseXML (String xml, EarthquakeRepository repository) {
        EarthquakeParser parser = new EarthquakeParser(xml, repository);
        parser.parse();
    }

    @Override
    public void run() {
        String xmlResult = this.getXML();
        this.parseXML(xmlResult, repository);
        onReady.run(repository);
    }
}
