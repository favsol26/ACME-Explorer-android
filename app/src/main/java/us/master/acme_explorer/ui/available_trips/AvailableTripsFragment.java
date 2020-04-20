package us.master.acme_explorer.ui.available_trips;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

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

public class AvailableTripsFragment extends Fragment {

    private static final String TAG = AvailableTripsFragment.class.getSimpleName();
    private static final int PICK_FILTER_LIST_DATA = 29;
    private RecyclerView myRecyclerView;
    private Switch mySwitch;
    private TripAdapter tripAdapter;
    private List<Trip> tripListToShow = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root;
        root = inflater.inflate(R.layout.fragment_trips_base_view, container, false);

        myRecyclerView = root.findViewById(R.id.my_trips_base_view_recyclerview);
        mySwitch = root.findViewById(R.id.my_trips_base_view_switch);
        RelativeLayout myLayout = root.findViewById(R.id.my_trips_base_view_filter);

        myLayout.setOnClickListener(getLayoutOnClickListener(container));

        Toast.makeText(container.getContext(), "here I am OnCreate", Toast.LENGTH_SHORT).show();
        this.tripListToShow.addAll(Util.tripList);
        this.tripAdapter = new TripAdapter(this.tripListToShow, TAG, mySwitch.isChecked() ? 2 : 1,
                container.getContext());
        Util.setRecyclerView(container, mySwitch, myRecyclerView, tripAdapter);
        mySwitch.setOnClickListener(
                V -> Util.setRecyclerView(container, mySwitch, myRecyclerView, tripAdapter)
        );

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mySwitch.setChecked(false);
//        TODO hold actual filter, TIP: use  share preference
    }

    private View.OnClickListener getLayoutOnClickListener(ViewGroup container) {
        return v -> {
            startActivityForResult(
                    new Intent(
                            container.getContext(),
                            FilterActivity.class
                    ),
                    PICK_FILTER_LIST_DATA
            );
            this.tripListToShow.clear();
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILTER_LIST_DATA)
            if (resultCode == RESULT_OK) {
                assert data != null;
                this.tripListToShow.addAll(verificationFilters(Util.tripList, data));
                this.tripAdapter.notifyDataSetChanged();
            }
    }

    private List<Trip> verificationFilters(List<Trip> trips, Intent data) {

        int maxPrice = Integer.parseInt(getValue(Constants.maxPrice, data));
        int minPrice = Integer.parseInt(getValue(Constants.minPrice, data));

        long dateStartToFilter = data.getLongExtra(Constants.dateStart, 0);
        long dateEndToFilter = data.getLongExtra(Constants.dateEnd, 0);

        if (maxPrice > 0 || minPrice > 0)
            trips = filterByPrice(trips, minPrice, maxPrice);

        if (dateStartToFilter > 0 || dateEndToFilter > 0)
            trips = filterByDate(trips, dateStartToFilter, dateEndToFilter);

        List<Trip> filteredTrips = trips;
        Log.d(TAG, "verificationFilters: "
                + String.format("date start = %s, date end = %s, min = %s y max = %s",
                dateStartToFilter, dateEndToFilter, minPrice, maxPrice
        ));

        return filteredTrips;
    }

    private List<Trip> filterByDate(List<Trip> trips, long dateStartToFilter, long dateEndToFilter) {
        List<Trip> filteredTrips;
        /*with only departure filter*/
        if (dateStartToFilter > 0 && dateEndToFilter == 0) {
            filteredTrips = trips.parallelStream().filter(
                    (trip) -> trip.getDepartureDate() >= dateStartToFilter
            ).collect(Collectors.toList());
        } else /*with only arrival filter*/
            if (dateStartToFilter == 0 && dateEndToFilter > 0) {
                filteredTrips = trips.parallelStream().filter(
                        (trip) -> trip.getArrivalDate() <= dateEndToFilter
                ).collect(Collectors.toList());
            } else /*with both filters*/ {
                filteredTrips = trips.parallelStream().filter(
                        (trip) -> trip.getDepartureDate() >= dateStartToFilter
                                && trip.getDepartureDate() < dateEndToFilter
                                && trip.getArrivalDate() > dateStartToFilter
                                && trip.getDepartureDate() <= dateEndToFilter
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
