package us.master.acme_explorer.ui.available_trips;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import us.master.acme_explorer.FilterActivity;
import us.master.acme_explorer.R;
import us.master.acme_explorer.adapters.TripAdapter;
import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.Util;
import us.master.acme_explorer.entity.Trip;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class AvailableTripsFragment extends Fragment {

    private static final String TAG = AvailableTripsFragment.class.getSimpleName();
    private static final int PICK_FILTERS = 29;
    private SharedPreferences sharedPreferences;
    private RecyclerView myRecyclerView;
    private Switch mySwitch;
    private TripAdapter tripAdapter;
    private List<Trip> tripListToShow = new ArrayList<>();
    private boolean controlFilter = false;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = container.getContext();
        View root = inflater.inflate(R.layout.fragment_trips_base_view, container, false);
        RelativeLayout myLayout = root.findViewById(R.id.my_trips_base_view_filter);
        myRecyclerView = root.findViewById(R.id.my_trips_base_view_recyclerview);
        mySwitch = root.findViewById(R.id.my_trips_base_view_switch);
        if (savedInstanceState != null)
            this.controlFilter = savedInstanceState
                    .getBoolean(Constants.controlFilter, false);
        if (!(this.tripListToShow.size() > 0))
            this.tripListToShow.addAll(controlFilter ? verificationFilters(Util.tripList)
                    : Util.tripList);
        this.tripAdapter = new TripAdapter(this.tripListToShow, TAG, mySwitch.isChecked() ? 2 : 1,
                this.context);
        Util.getToast(this.context, this.tripListToShow.size(), R.string.menu_gallery_trips);
        Util.setRecyclerView(container, mySwitch, myRecyclerView, this.tripAdapter);
        myLayout.setOnClickListener(v -> {
            startActivityForResult(new Intent(this.context, FilterActivity.class), PICK_FILTERS);
            this.tripListToShow.clear();
        });
        mySwitch.setOnClickListener(v -> Util.setRecyclerView(container, mySwitch, myRecyclerView,
                this.tripAdapter));
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILTERS)
            if (resultCode == RESULT_OK) {
                assert data != null;
                this.tripListToShow.addAll(verificationFilters(Util.tripList, data));
                this.tripAdapter.notifyDataSetChanged();
                Util.getToast(this.context, this.tripListToShow.size(), R.string.filter_message);
                this.controlFilter = true;
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        mySwitch.setChecked(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.controlFilter, controlFilter);
    }

    private List<Trip> verificationFilters(List<Trip> tripList, Intent data) {
        int maxPrice = Integer.parseInt(getValue(Constants.maxPrice, data));
        int minPrice = Integer.parseInt(getValue(Constants.minPrice, data));
        long dateStartToFilter = data.getLongExtra(Constants.dateStart, 0);
        long dateEndToFilter = data.getLongExtra(Constants.dateEnd, 0);

        savePreferencesFilters(minPrice, maxPrice, dateStartToFilter, dateEndToFilter);

        if (minPrice > 0 || maxPrice > 0)
            tripList = filterByPrice(tripList, minPrice, maxPrice);

        if (dateStartToFilter > 0 || dateEndToFilter > 0)
            tripList = filterByDate(tripList, dateStartToFilter, dateEndToFilter);

        Log.d(TAG, "verificationFilters: "
                + String.format("date start = %s, date end = %s, min = %s y max = %s",
                dateStartToFilter, dateEndToFilter, minPrice, maxPrice
        ));

        return tripList;
    }

    private List<Trip> verificationFilters(List<Trip> tripList) {
        sharedPreferences = context.getSharedPreferences(Constants.filterPreferences, MODE_PRIVATE);

        int maxPrice = sharedPreferences.getInt(Constants.maxPrice, 0);
        int minPrice = sharedPreferences.getInt(Constants.minPrice, 0);
        long dateStartToFilter = sharedPreferences.getLong(Constants.dateStart, 0);
        long dateEndToFilter = sharedPreferences.getLong(Constants.dateEnd, 0);

        if (minPrice > 0 || maxPrice > 0)
            tripList = filterByPrice(tripList, minPrice, maxPrice);

        if (dateStartToFilter > 0 || dateEndToFilter > 0)
            tripList = filterByDate(tripList, dateStartToFilter, dateEndToFilter);

        Log.d(TAG, "verificationSharePreferencesFilters: "
                + String.format("date start = %s, date end = %s, min = %s y max = %s",
                dateStartToFilter, dateEndToFilter, minPrice, maxPrice
        ));
        return tripList;
    }

    private void savePreferencesFilters(int minPrice, int maxPrice, long dateStart, long dateEnd) {
        sharedPreferences = context.getSharedPreferences(Constants.filterPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.minPrice, minPrice);
        editor.putInt(Constants.maxPrice, maxPrice);
        editor.putLong(Constants.dateStart, dateStart);
        editor.putLong(Constants.dateStart, dateEnd);
        editor.apply();
    }

    private List<Trip> filterByDate(List<Trip> trips, long dateStartFilter, long dateEndFilter) {
        List<Trip> filteredTrips;
        /*with only departure filter*/
        if (dateStartFilter > 0 && dateEndFilter == 0) {
            filteredTrips = trips.parallelStream().filter(
                    (trip) -> trip.getDepartureDate() >= dateStartFilter
            ).collect(Collectors.toList());
        } else /*with only arrival filter*/
            if (dateStartFilter == 0 && dateEndFilter > 0) {
                filteredTrips = trips.parallelStream().filter(
                        (trip) -> trip.getArrivalDate() <= dateEndFilter
                ).collect(Collectors.toList());
            } else /*with both filters*/ {
                filteredTrips = trips.parallelStream().filter(
                        (trip) -> trip.getDepartureDate() >= dateStartFilter
                                && trip.getDepartureDate() < dateEndFilter
                                && trip.getArrivalDate() > dateStartFilter
                                && trip.getDepartureDate() <= dateEndFilter
                ).collect(Collectors.toList());
            }
        return filteredTrips;
    }

    private List<Trip> filterByPrice(List<Trip> trips, int minPrice, int maxPrice) {
        List<Trip> filteredTrips = new ArrayList<>();
        if (minPrice > 0 && maxPrice == 0) {
            for (Trip trip : trips)
                if (trip.getPrice() > minPrice)
                    filteredTrips.add(trip);
        } else if (maxPrice > 0 && minPrice == 0) {
            for (Trip trip : trips)
                if (trip.getPrice() < maxPrice)
                    filteredTrips.add(trip);
        } else for (Trip trip : trips)
            if (trip.getPrice() > minPrice && trip.getPrice() < maxPrice)
                filteredTrips.add(trip);
        return filteredTrips;
    }

    private String getValue(String tag, Intent data) {
        return !Objects.equals(data.getStringExtra(tag), "") ? data.getStringExtra(tag) : "0";
    }
}
