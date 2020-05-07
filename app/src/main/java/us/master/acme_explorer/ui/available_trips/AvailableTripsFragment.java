package us.master.acme_explorer.ui.available_trips;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;

import us.master.acme_explorer.FilterActivity;
import us.master.acme_explorer.R;
import us.master.acme_explorer.adapters.TripAdapter;
import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.FirebaseDatabaseService;
import us.master.acme_explorer.common.Util;
import us.master.acme_explorer.entity.Trip;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static us.master.acme_explorer.common.Util.getSharedPreferenceFilters;
import static us.master.acme_explorer.common.Util.navigateTo;
import static us.master.acme_explorer.common.Util.setRecyclerView;

public class AvailableTripsFragment extends Fragment {

    private static final String TAG = AvailableTripsFragment.class.getSimpleName();
    private static final int PICK_FILTERS = 0x1d;
    private RecyclerView myRecyclerView;
    private Switch mySwitch;
    private TripAdapter tripAdapter;
    private RelativeLayout myLayout;
    private Context context;
    private FloatingActionButton mNewTripFAB;
    private boolean subscribed = false;
    private boolean controlFilter = Util.controlFilter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = requireContext();
        View root = inflater.inflate(R.layout.fragment_trips_base_view, container, false);
        setView(root);
        loadTrips(savedInstanceState);
//        getToast(this.context, this.tripListToShow.size(), R.string.menu_available_trips);

        //TODO optimize recycler layout manager state
        setRecyclerView(context, mySwitch, myRecyclerView, this.tripAdapter);

        myRecyclerView.setAdapter(tripAdapter);
        setOnClicksListeners();
        return root;
    }

    private void loadDatabase(@Nullable Intent data) {
        FirebaseDatabaseService databaseService = FirebaseDatabaseService.getServiceInstance();
        databaseService.getTrips().orderByChild("created").addChildEventListener(getListener(data));
        subscribed = true;
    }

    private ChildEventListener getListener(@Nullable Intent data) {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    if (trip != null) {
                        showItems(dataSnapshot, trip);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                if (trip != null) {
                    trip.setId(dataSnapshot.getKey());
                    tripAdapter.updateItem(trip);
                    showItems(dataSnapshot, trip);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                if (trip != null) {
                    trip.setId(dataSnapshot.getKey());
                    tripAdapter.removeItem(trip);
                }
            }

            private void showItems(@NonNull DataSnapshot dataSnapshot, Trip trip) {
                if (controlFilter) {
                    if (data != null) {
                        if (verificationFilters(trip, data)) {
                            trip.setId(dataSnapshot.getKey());
                            tripAdapter.addItem(trip);
                        } else {
                            trip.setId(dataSnapshot.getKey());
                            tripAdapter.removeItem(trip);
                        }
                    } else {
                        if (verificationFilters(trip)) {
                            trip.setId(dataSnapshot.getKey());
                            tripAdapter.addItem(trip);
                        } else {
                            trip.setId(dataSnapshot.getKey());
                            tripAdapter.removeItem(trip);
                        }
                    }
                } else {
                    trip.setId(dataSnapshot.getKey());
                    tripAdapter.addItem(trip);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void setView(View root) {
        mNewTripFAB = root.findViewById(R.id.my_new_trip_fab);
        myLayout = root.findViewById(R.id.my_trips_base_view_filter);
        mySwitch = root.findViewById(R.id.my_trips_base_view_switch);
        myRecyclerView = root.findViewById(R.id.my_trips_base_view_recyclerview);

        mNewTripFAB.setVisibility(View.VISIBLE);
    }

    private void loadTrips(Bundle savedInstanceState) {
        if (savedInstanceState != null)
            this.controlFilter =
                    savedInstanceState.getBoolean(Constants.controlFilter, false);

        if (!subscribed)
            loadDatabase(null);
        this.tripAdapter = new TripAdapter(TAG, mySwitch.isChecked() ? 2 : 1, this.context);

    }

    private void setOnClicksListeners() {
        myLayout.setOnClickListener(v -> startActivityForResult(
                new Intent(this.context, FilterActivity.class), PICK_FILTERS));

        mySwitch.setOnClickListener(v -> setRecyclerView(
                this.context, mySwitch, myRecyclerView, this.tripAdapter));

        mNewTripFAB.setOnClickListener(v ->
                navigateTo(v, R.id.action_nav_available_trips_to_nav_trips, null)
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILTERS)
            if (resultCode == RESULT_OK) {
                assert data != null;
                this.controlFilter = true;
                Util.controlFilter = true;
                tripAdapter.clearLists();
                loadDatabase(data);
            }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.controlFilter, this.controlFilter);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mySwitch.setChecked(false);
        subscribed = false;
        tripAdapter.clearLists();
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

    private boolean verificationFilters(Trip trip, Intent data) {
        boolean valid = true;
        int minPrice = data.getIntExtra(Constants.minPrice, 0);
        int maxPrice = data.getIntExtra(Constants.maxPrice, 0);
        long dateStartToFilter = data.getLongExtra(Constants.dateStart, 0);
        long dateEndToFilter = data.getLongExtra(Constants.dateEnd, 0);

        saveSharePreferFilters(minPrice, maxPrice, dateStartToFilter, dateEndToFilter);

        if (minPrice > 0 || maxPrice > 0)
            valid = filterByPrice(trip, minPrice, maxPrice);

        if (dateStartToFilter > 0 || dateEndToFilter > 0)
            valid = filterByDate(trip, dateStartToFilter, dateEndToFilter);

        return valid;
    }

    private boolean verificationFilters(Trip trip) {
        boolean valid = true;
        HashMap filterSaved = getSharedPreferenceFilters(context);

        if ((long) filterSaved.get(Constants.minPrice) > 0 || (long) filterSaved.get(Constants.maxPrice) > 0)
            valid = filterByPrice(trip,
                    (int) (long) filterSaved.get(Constants.minPrice),
                    (int) (long) filterSaved.get(Constants.maxPrice));

        if ((long) filterSaved.get(Constants.dateStart) > 0 || (long) filterSaved.get(Constants.dateEnd) > 0)
            valid = filterByDate(trip,
                    (long) filterSaved.get(Constants.dateStart),
                    (long) filterSaved.get(Constants.dateEnd));

        return valid;
    }

    private boolean filterByDate(Trip trip, long dateDepartureFilter, long dateArrivalFilter) {
        boolean valid;
        /*with only departure filter*/
        if ((dateDepartureFilter > 0) && (dateArrivalFilter == 0))
            valid = (trip.getDepartureDate() >= dateDepartureFilter);
            /*with only arrival filter*/
        else if (dateDepartureFilter == 0 && dateArrivalFilter > 0)
            valid = (trip.getArrivalDate() <= dateArrivalFilter);
            /*with both filters*/
        else valid = (trip.getDepartureDate() >= dateDepartureFilter
                    && trip.getDepartureDate() < dateArrivalFilter
                    && trip.getArrivalDate() > dateDepartureFilter
                    && trip.getArrivalDate() <= dateArrivalFilter);
        return valid;
    }

    private boolean filterByPrice(Trip trip, int minPrice, int maxPrice) {
        boolean valid;

        if (minPrice > 0 && maxPrice == 0) valid = trip.getPrice() > minPrice;
        else if (maxPrice > 0 && minPrice == 0) valid = trip.getPrice() < maxPrice;
        else valid = (trip.getPrice() > minPrice) && (trip.getPrice() < maxPrice);

        return valid;
    }
}
