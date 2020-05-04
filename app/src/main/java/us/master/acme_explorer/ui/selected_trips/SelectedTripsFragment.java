package us.master.acme_explorer.ui.selected_trips;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import us.master.acme_explorer.R;
import us.master.acme_explorer.adapters.TripAdapter;
import us.master.acme_explorer.common.Util;
import us.master.acme_explorer.entity.Trip;

public class SelectedTripsFragment extends Fragment {

    private static final String TAG = SelectedTripsFragment.class.getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_trips_base_view,
                container, false);

        RelativeLayout myLayout = root.findViewById(R.id.myFilterSettings);
        myLayout.setVisibility(View.GONE);

        RecyclerView myRecyclerView = root.findViewById(R.id.my_trips_base_view_recyclerview);

        List<Trip> selectedTrips = new ArrayList<>();
        for (Trip trip : Util.tripList) {
            if (trip.isSelected())
                selectedTrips.add(trip);
        }
        Util.getToast(container.getContext(), selectedTrips.size(), R.string.menu_selected_trips);
        myRecyclerView.setLayoutManager(
                new LinearLayoutManager(container.getContext()));
        myRecyclerView.setAdapter(
                new TripAdapter(selectedTrips, TAG, 1, container.getContext()));
        return root;
    }
}
