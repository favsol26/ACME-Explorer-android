package us.master.acme_explorer.ui.available_trips;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        Util.getToast(this.context, this.tripListToShow.size(), R.string.menu_available_trips);
        //TODO optimize recycler layout manager state
        Util.setRecyclerView(context, mySwitch, myRecyclerView, this.tripAdapter);
        myLayout.setOnClickListener(v -> {
            startActivityForResult(new Intent(this.context, FilterActivity.class), PICK_FILTERS);
            this.tripListToShow.clear();
        });
        mySwitch.setOnClickListener(v -> Util.setRecyclerView(this.context, mySwitch, myRecyclerView,
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.controlFilter, controlFilter);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mySwitch.setChecked(false);
    }

    private void saveSharePreferFilters(int minPrice, int maxPrice, long dateStart, long dateEnd) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constants.filterPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.minPrice, minPrice);
        editor.putInt(Constants.maxPrice, maxPrice);
        editor.putLong(Constants.dateStart, dateStart);
        editor.putLong(Constants.dateEnd, dateEnd);
        editor.apply();
    }

    private List<Trip> verificationFilters(List<Trip> tripList, Intent data) {
        int minPrice = data.getIntExtra(Constants.minPrice, 0);
        int maxPrice = data.getIntExtra(Constants.maxPrice, 0);
        long dateStartToFilter = data.getLongExtra(Constants.dateStart, 0);
        long dateEndToFilter = data.getLongExtra(Constants.dateEnd, 0);

        saveSharePreferFilters(minPrice, maxPrice, dateStartToFilter, dateEndToFilter);

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
        HashMap filterSaved = Util.getSharedPreferenceFilters(context);

        if ((long) filterSaved.get(Constants.minPrice) > 0 || (long) filterSaved.get(Constants.maxPrice) > 0)
            tripList = filterByPrice(tripList,
                    (int) (long) filterSaved.get(Constants.minPrice),
                    (int) (long) filterSaved.get(Constants.maxPrice));

        if ((long) filterSaved.get(Constants.dateStart) > 0 || (long) filterSaved.get(Constants.dateEnd) > 0)
            tripList = filterByDate(tripList,
                    (long) filterSaved.get(Constants.dateStart),
                    (long) filterSaved.get(Constants.dateEnd));

        return tripList;
    }

    private List<Trip> filterByDate(List<Trip> trips, long dateStartFilter, long dateEndFilter) {
        List<Trip> filteredTrips = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            /*with only departure filter*/
            if ((dateStartFilter > 0) && (dateEndFilter == 0)) for (Trip trip : trips) {
                if (trip.getDepartureDate() >= dateStartFilter) filteredTrips.add(trip);
            }
            else /*with only arrival filter*/
                if (dateStartFilter == 0 && dateEndFilter > 0) for (Trip trip : trips) {
                    if (trip.getArrivalDate() <= dateEndFilter) filteredTrips.add(trip);
                }
                else /*with both filters*/ for (Trip trip : trips) {
                    if (trip.getDepartureDate() >= dateStartFilter
                            && trip.getDepartureDate() < dateEndFilter
                            && trip.getArrivalDate() > dateStartFilter
                            && trip.getArrivalDate() <= dateEndFilter) filteredTrips.add(trip);
                }
        } else {
            filteredTrips = filterByDateStream(trips, dateStartFilter, dateEndFilter);
        }
        return filteredTrips;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Trip> filterByDateStream(List<Trip> trips, long dateStartFilter, long dateEndFilter) {
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
                                && trip.getArrivalDate() <= dateEndFilter
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
}
