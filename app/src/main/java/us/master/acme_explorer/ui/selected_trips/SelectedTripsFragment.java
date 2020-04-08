package us.master.acme_explorer.ui.selected_trips;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.Util;
import us.master.acme_explorer.entity.Trip;

public class SelectedTripsFragment extends Fragment {

    private static final String TAG = SelectedTripsFragment.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_trips_base_view,
                container, false);

        RelativeLayout myLayout = root.findViewById(R.id.my_trips_base_view_filter);
        myLayout.setOnClickListener(Util.getLayoutOnClickListener(container));

        RecyclerView myRecyclerView = root.findViewById(R.id.my_trips_base_view_recyclerview);
        Switch mySwitch = root.findViewById(R.id.my_trips_base_view_switch);

        List<Trip> selectedTrips = new ArrayList<>();
        for (Trip trip : Constants.tripList) {
            if (trip.isSelected())
                selectedTrips.add(trip);
        }
        Util.setRecyclerView(container, mySwitch,
                selectedTrips, myRecyclerView, TAG);

        mySwitch.setOnClickListener(
                Util.getSwitchOnClickListener(container, mySwitch,
                        myRecyclerView, selectedTrips, TAG)
        );
        return root;
    }
}
