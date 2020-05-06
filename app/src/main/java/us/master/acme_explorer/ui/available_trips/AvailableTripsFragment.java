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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import us.master.acme_explorer.FilterActivity;
import us.master.acme_explorer.R;
import us.master.acme_explorer.adapters.TripAdapter;
import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.FirebaseDatabaseService;
import us.master.acme_explorer.entity.Trip;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static us.master.acme_explorer.common.Util.getSharedPreferenceFilters;
import static us.master.acme_explorer.common.Util.getToast;
import static us.master.acme_explorer.common.Util.navigateTo;
import static us.master.acme_explorer.common.Util.setRecyclerView;

public class AvailableTripsFragment extends Fragment {

    private static final String TAG = AvailableTripsFragment.class.getSimpleName();
    private static final int PICK_FILTERS = 0x1d;
    private RecyclerView myRecyclerView;
    private Switch mySwitch;
    private TripAdapter tripAdapter;
    private List<Trip> tripListToShow = new ArrayList<>();
    private RelativeLayout myLayout;
    private boolean controlFilter = false;
    private Context context;
    private FloatingActionButton mNewTripFAB;
    private boolean subscribed = false;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = requireContext();
        View root = inflater.inflate(R.layout.fragment_trips_base_view, container, false);
        setView(root);
        loadTrips(savedInstanceState);
        getToast(this.context, this.tripListToShow.size(), R.string.menu_available_trips);

        //TODO optimize recycler layout manager state
        setRecyclerView(context, mySwitch, myRecyclerView, this.tripAdapter);

        myRecyclerView.setAdapter(tripAdapter);
        if (!subscribed)
            loadDatabase();
        setOnClicksListeners();
        return root;
    }

    private void loadDatabase() {
        Log.d(TAG, "loadDatabase: " + "am here");
        FirebaseDatabaseService databaseService = FirebaseDatabaseService.getServiceInstance();
        databaseService.getTrips().orderByChild("created").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    Log.d(TAG, "onChildAdded: " + dataSnapshot.getValue());
                    if (trip != null) {
                        trip.setId(dataSnapshot.getKey());
                        tripAdapter.addItem(trip);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Trip trip = dataSnapshot.getValue(Trip.class);
//                Log.d(TAG, "onChildChanged: " + dataSnapshot.getValue());
                if (trip != null) {
                    trip.setId(dataSnapshot.getKey());
                    tripAdapter.updateItem(trip);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Trip trip = dataSnapshot.getValue(Trip.class);
//                Log.d(TAG, "onChildRemoved: " + dataSnapshot.getValue());
                if (trip != null) {
                    trip.setId(dataSnapshot.getKey());
                    tripAdapter.removeItem(trip);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        subscribed = true;
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

      /*  if (!(this.tripListToShow.size() > 0))
            this.tripListToShow.addAll(controlFilter
                    ? verificationFilters(tripList)
                    : tripList);
*/
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
                this.tripListToShow.clear();
//                this.tripListToShow.addAll(verificationFilters(tripList, data));
                this.tripAdapter.notifyDataSetChanged();
                getToast(this.context, this.tripListToShow.size(), R.string.filter_message);
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
        subscribed = false;
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

//        Log.d(TAG, "verificationFilters: "
//                + String.format("date start = %s, date end = %s, min = %s y max = %s",
//                dateStartToFilter, dateEndToFilter, minPrice, maxPrice
//        ));
        return tripList;
    }

    private List<Trip> verificationFilters(List<Trip> tripList) {
        HashMap filterSaved = getSharedPreferenceFilters(context);

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
