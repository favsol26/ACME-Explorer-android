package us.master.acme_explorer.ui.maps;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import us.master.acme_explorer.R;

import static us.master.acme_explorer.common.Util.navigateTo;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = MapsFragment.class.getSimpleName();
    private FusedLocationProviderClient client;
    private GoogleMap googleMap;
    private MapView mMapView;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null || locationResult.getLastLocation() == null
                    || !locationResult.getLastLocation().hasAccuracy()) {
                return;
            }
            double latitude = locationResult.getLastLocation().getLatitude();
            double longitude = locationResult.getLastLocation().getLongitude();
            LatLng myPosition = new LatLng(latitude, longitude);
            if (googleMap == null) return;
            float zoomLevel = 16.0f; //This goes up to 21
            googleMap.setMyLocationEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, zoomLevel));
            googleMap.setOnMyLocationButtonClickListener(() -> {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, zoomLevel));
                return true;
            });
        }
    };


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
        mMapView = root.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);
        }
        return root;
    }

    private void getLocationUser() {
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setSmallestDisplacement(10);
        client.requestLocationUpdates(request, locationCallback, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Log.i(TAG, "onMapReady: now");
        getLocationUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        client.removeLocationUpdates(locationCallback);
        Log.i(TAG, "onMapReady: location disable");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
