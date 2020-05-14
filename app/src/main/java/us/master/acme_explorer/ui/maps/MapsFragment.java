package us.master.acme_explorer.ui.maps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import us.master.acme_explorer.R;

import static us.master.acme_explorer.common.Util.navigateTo;

public class MapsFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateTo(requireView(), R.id.action_nav_maps_to_nav_main_menu, null);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_maps, container, false);

        FusedLocationProviderClient services
                = LocationServices.getFusedLocationProviderClient(requireActivity());
        services.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                double latitude = task.getResult().getLatitude();
                double longitude = task.getResult().getLongitude();
            }
        });
        return root;
    }
}
