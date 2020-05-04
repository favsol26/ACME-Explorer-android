package us.master.acme_explorer.ui.trips;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Util;

public class TripsFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trips, container, false);

        TextInputEditText mEditText = root.findViewById(R.id.trips_arrival_date_et);
        mEditText.setText(Util.dateFormatter(1588566233));
        return root;
    }
}

