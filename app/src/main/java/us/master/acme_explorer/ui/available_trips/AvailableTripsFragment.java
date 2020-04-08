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
import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.Util;

public class AvailableTripsFragment extends Fragment {

    private static final String TAG = AvailableTripsFragment.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trips_base_view,
                container, false);

        RelativeLayout myLayout = root.findViewById(R.id.my_trips_base_view_filter);
        myLayout.setOnClickListener(Util.getLayoutOnClickListener(container));

        RecyclerView myRecyclerView = root.findViewById(R.id.my_trips_base_view_recyclerview);
        Switch mySwitch = root.findViewById(R.id.my_trips_base_view_switch);
        myRecyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), 2));

        Util.setRecyclerView(container, mySwitch,
                Constants.tripList, myRecyclerView, TAG);
        mySwitch.setOnClickListener(
                Util.getSwitchOnClickListener(container, mySwitch,
                        myRecyclerView, Constants.tripList, TAG)
        );
        return root;
    }
}
