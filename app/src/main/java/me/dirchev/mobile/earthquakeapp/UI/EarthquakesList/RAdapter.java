package me.dirchev.mobile.earthquakeapp.UI.EarthquakesList;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.dirchev.mobile.earthquakeapp.R;
import me.dirchev.mobile.earthquakeapp.models.Earthquake;
import me.dirchev.mobile.earthquakeapp.models.EarthquakeDataStore;
import me.dirchev.mobile.earthquakeapp.models.EarthquakeRepository;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 March 01
 */
public class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView row;
        public TextView magnitudeTextView;
        public TextView depthTextView;
        public TextView locationNameTextView;
        public Button showOnMapButton;

        public ViewHolder(View itemView) {
            super(itemView);

            row = itemView.findViewById(R.id.a_row);
            magnitudeTextView = itemView.findViewById(R.id.magnitude_text);
            depthTextView = itemView.findViewById(R.id.depth_text);
            locationNameTextView = itemView.findViewById(R.id.location_name_text);
            showOnMapButton = itemView.findViewById(R.id.show_on_map_button);
        }
    }

    EarthquakeRepository earthquakeRepository;

    public RAdapter(Context c) {
        earthquakeRepository = EarthquakeDataStore.getInstance().getEarthquakeRepository();
    }

    @Override
    public void onBindViewHolder(RAdapter.ViewHolder viewHolder, int i) {
        final int index = i;
        Earthquake earthquake = earthquakeRepository.getEarthquakeByIndex(i);
        TextView magnitudeTextView = viewHolder.magnitudeTextView;
        TextView depthTextView = viewHolder.depthTextView;
        TextView locationNameTextView = viewHolder.locationNameTextView;
        magnitudeTextView.setText(Double.toString(earthquake.getMagnitude()));
        depthTextView.setText(earthquake.getDepth().toString());
        locationNameTextView.setText(earthquake.getLocationName());
        Button showOnMapButton = viewHolder.showOnMapButton;
        showOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                earthquakeRepository.setSelectedEarthquakeIndex(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return earthquakeRepository.size();
    }


    @Override
    public RAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.earthquake_listing_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
}
