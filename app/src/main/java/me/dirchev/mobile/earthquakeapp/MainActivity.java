package me.dirchev.mobile.earthquakeapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import me.dirchev.mobile.earthquakeapp.models.EarthquakesChannel;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 February 14
 */
public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private TextView rawDataDisplay;
    private Button startButton;
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        // More Code goes here
    }

    public void onClick(View aview)
    {
        startProgress();
    }

    public void startProgress()
    {
        EarthquakeLoader loader = new EarthquakeLoader(urlSource, new EarthquakeParsedEventListener() {
            @Override
            public void run(final List<EarthquakesChannel> earthquakesChannelList) {
                MainActivity.this.runOnUiThread(new Runnable()
                {
                    public void run() {
                        Log.d("UI thread", "I am the UI thread");
                        Log.d("PARSED", earthquakesChannelList.toString());
                    }
                });
            }
        });
        new Thread(loader).start();
    }
}