package me.dirchev.mobile.earthquakeapp.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

import me.dirchev.mobile.earthquakeapp.models.Earthquake;
import me.dirchev.mobile.earthquakeapp.models.EarthquakeRepository;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 February 21
 */
public class EarthquakeLoader extends AsyncTask<URL, Void, EarthquakeLoaderResult> {
    EarthquakeRepository repository;
    Context c;
    public EarthquakeLoader(EarthquakeRepository repository, Context c) {
        super();
        this.repository = repository;
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        repository.setLoading(true);
    }

    private String getXML (URL url) throws IOException {
        URLConnection yc;
        BufferedReader in;
        String inputLine;
        String xmlResult = "";

        yc = url.openConnection();
        in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        while ((inputLine = in.readLine()) != null) {
            xmlResult = xmlResult + inputLine;

        }
        in.close();
        return xmlResult;
    }

    @Override
    protected EarthquakeLoaderResult doInBackground(URL... urls) {
        URL url = urls[0];
        String rawXML = null;
        try {
            rawXML = this.getXML(url);
            EarthquakeParser earthquakeParser = new EarthquakeParser(rawXML);
            return new EarthquakeLoaderResult(earthquakeParser.parse());
        } catch (XmlPullParserException e) {
            return new EarthquakeLoaderResult(e);
        } catch (IOException e) {
            return new EarthquakeLoaderResult(e);
        }
    }

    @Override
    protected void onPostExecute(EarthquakeLoaderResult result) {
        super.onPostExecute(result);
        if (result.getError() != null) {
            repository.setFetchError(true);
        } else {
            repository.refreshEarthquakes(result.getEarthquakes());
        }
    }
}
