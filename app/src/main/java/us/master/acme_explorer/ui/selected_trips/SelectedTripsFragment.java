package us.master.acme_explorer.ui.selected_trips;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import us.master.acme_explorer.R;

public class SelectedTripsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(
                R.layout.fragment_trips_base_view,
                container,
                false
        );

        RecyclerView myRecyclerView = root.findViewById(R.id.my_trips_base_view_recyclerview);

        return root;
    }
}
