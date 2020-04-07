package us.master.acme_explorer.ui.available_trips;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import us.master.acme_explorer.R;
import us.master.acme_explorer.adapters.TripAdapter;
import us.master.acme_explorer.common.Constants;

public class AvailableTripsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trips_base_view, container, false);

        Switch mySwitch = root.findViewById(R.id.my_trips_base_view_switch);
        RelativeLayout myLayout = root.findViewById(R.id.my_trips_base_view_filter);
        RecyclerView myRecyclerView = root.findViewById(R.id.my_trips_base_view_recyclerview);
        myRecyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), 2));
        myRecyclerView.setAdapter(
                new TripAdapter(
                        Constants.tripList,
                        AvailableTripsFragment.class.getSimpleName(),
                        2
                )
        );

        return root;
    }
}
