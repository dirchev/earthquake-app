package me.dirchev.mobile.earthquakeapp.UI.EarthquakesList;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import me.dirchev.mobile.earthquakeapp.MainActivity;
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
        public ViewGroup row;
        public TextView magnitudeTextView;
        public TextView dateTextView;
        public TextView depthTextView;
        public TextView locationNameTextView;

        public ViewHolder(final View itemView) {
            super(itemView);

            row = itemView.findViewById(R.id.a_row);
            magnitudeTextView = itemView.findViewById(R.id.magnitude_text);
            depthTextView = itemView.findViewById(R.id.depth_text);
            locationNameTextView = itemView.findViewById(R.id.location_name_text);
            dateTextView = itemView.findViewById(R.id.date_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Earthquake earthquake = (Earthquake) v.getTag();
                    earthquakeRepository.setSelectedEarthquake(earthquake);
                }
            });
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenu.ContextMenuInfo menuInfo) {
                    MenuItem filterLocation = menu.add("location");
                    MenuItem filterDate = menu.add("date");
                    MenuItem.OnMenuItemClickListener listener = (new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Earthquake earthquake = (Earthquake) v.getTag();
                            earthquakeRepository.setEarthquakeFilter(earthquake, item.getTitle().toString());
                            return true;
                        }
                    });
                    filterLocation.setOnMenuItemClickListener(listener);
                    filterDate.setOnMenuItemClickListener(listener);
                    menu.setHeaderTitle("Filter by...");
                }
            });
        }
    }

    EarthquakeRepository earthquakeRepository;

    public RAdapter(Context c) {
        earthquakeRepository = EarthquakeDataStore.getInstance().getEarthquakeRepository();
    }

    @Override
    public void onBindViewHolder(final RAdapter.ViewHolder viewHolder, int i) {
        final int index = i;
        Earthquake earthquake = earthquakeRepository.getVisibleEarthquakes().get(i);
        viewHolder.row.setTag(earthquake);
        boolean isSelected = earthquake.equals(earthquakeRepository.getSelectedEarthquake());
        TextView magnitudeTextView = viewHolder.magnitudeTextView;
        TextView depthTextView = viewHolder.depthTextView;
        TextView locationNameTextView = viewHolder.locationNameTextView;
        TextView dateTextView = viewHolder.dateTextView;
        CardView cardView = (CardView) viewHolder.row;
        magnitudeTextView.setText("Magnitude: " + Double.toString(earthquake.getMagnitude()));
        depthTextView.setText("Depth: " + earthquake.getDepth().toString());
        SimpleDateFormat format = new SimpleDateFormat("dd MMM YYYY, hh:mm");
        dateTextView.setText("Date: " + format.format(earthquake.getPubDate()));
        if (isSelected) {
            cardView.setCardBackgroundColor(Color.LTGRAY);
        } else {
            cardView.setCardBackgroundColor(Color.WHITE);
        }
        locationNameTextView.setText(earthquake.getLocationName());
    }

    @Override
    public int getItemCount() {
        return earthquakeRepository.getVisibleEarthquakes().size();
    }


    @Override
    public RAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.earthquake_listing_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
}
