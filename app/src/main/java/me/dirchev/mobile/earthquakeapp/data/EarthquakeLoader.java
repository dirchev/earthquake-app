package me.dirchev.mobile.earthquakeapp.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
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
public class EarthquakeLoader extends AsyncTask<URL, Void, LinkedList<Earthquake>> {
    EarthquakeRepository repository;
    public EarthquakeLoader(EarthquakeRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        repository.setLoading(true);
    }

    private String getXML (URL url) {
        URLConnection yc;
        BufferedReader in;
        String inputLine;
        String xmlResult = "";

        try {
            yc = url.openConnection();
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                xmlResult = xmlResult + inputLine;

            }
            in.close();
        } catch (IOException ae) {
            Log.e("MyTag", "ioexception");
        }
        return xmlResult;
    }

    @Override
    protected LinkedList<Earthquake> doInBackground(URL... urls) {
        URL url = urls[0];
        String rawXML = this.getXML(url);
        EarthquakeParser earthquakeParser = new EarthquakeParser(rawXML);
        return earthquakeParser.parse();
    }

    @Override
    protected void onPostExecute(LinkedList<Earthquake> earthquakes) {
        super.onPostExecute(earthquakes);
        repository.refreshEarthquakes(earthquakes);
    }
}
