package me.dirchev.mobile.earthquakeapp.UI.EarthquakesList;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;

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
            // Select the earthquake when it is clicked
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Earthquake earthquake = (Earthquake) v.getTag();
                    earthquakeRepository.setSelectedEarthquake(earthquake);
                }
            });
            // Context menu initialisation
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenu.ContextMenuInfo menuInfo) {
                    // add menu options. They will be shown in the menu
                    MenuItem filterLocation = menu.add(itemView.getContext().getString(R.string.filter_by_context_menu_location_text));
                    MenuItem filterDate = menu.add(itemView.getContext().getString(R.string.filter_by_context_menu_date_text));
                    // define a listener as a lambda expression
                    MenuItem.OnMenuItemClickListener listener = (new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Earthquake earthquake = (Earthquake) v.getTag();
                            earthquakeRepository.setEarthquakeFilter(earthquake, item.getTitle().toString());
                            return true;
                        }
                    });
                    // set the listener to both menu items
                    filterLocation.setOnMenuItemClickListener(listener);
                    filterDate.setOnMenuItemClickListener(listener);
                    menu.setHeaderTitle(itemView.getContext().getString(R.string.filter_by_context_menu_heading));
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
        // get the earthquake on position (i)
        Earthquake earthquake = earthquakeRepository.getVisibleEarthquakes().get(i);
        // set the earthquake as a tag to the row. This is later used in the context menu
        // tags can be used to store associated metadata to each View
        viewHolder.row.setTag(earthquake);
        // initialise all needed Views
        TextView magnitudeTextView = viewHolder.magnitudeTextView;
        TextView depthTextView = viewHolder.depthTextView;
        TextView locationNameTextView = viewHolder.locationNameTextView;
        TextView dateTextView = viewHolder.dateTextView;
        CardView cardView = (CardView) viewHolder.row;

        Context c = viewHolder.row.getContext();

        // set text to the TextView elements
        magnitudeTextView.setText(String.format(c.getString(R.string.magnitude_info), earthquake.getMagnitude()));
        depthTextView.setText(String.format(c.getString(R.string.depth_info), earthquake.getDepth().getValue(), earthquake.getDepth().getMeasure()));
        SimpleDateFormat format = new SimpleDateFormat(c.getString(R.string.datetime_format));
        dateTextView.setText(String.format(c.getString(R.string.date_info), format.format(earthquake.getPubDate())));
        locationNameTextView.setText(earthquake.getLocationName());

        // check if earthquake is selected.
        boolean isSelected = earthquake.equals(earthquakeRepository.getSelectedEarthquake());
        if (isSelected) {
            cardView.setCardBackgroundColor(Color.LTGRAY);
        } else {
            // we need this as the item with LTGRAY colour might be reused (we are in RecyclerView)
            // so we need to implicitly specify the background colour (even if WHITE is the default)
            cardView.setCardBackgroundColor(Color.WHITE);
        }
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
