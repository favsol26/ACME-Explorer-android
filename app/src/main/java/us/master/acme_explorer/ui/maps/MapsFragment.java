package us.master.acme_explorer.ui.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Constants;

import static us.master.acme_explorer.common.Util.jsonArrayResults;
import static us.master.acme_explorer.common.Util.navigateTo;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = MapsFragment.class.getSimpleName();
    String param;
    private FusedLocationProviderClient client;
    private GoogleMap googleMap;
    private MapView mMapView;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
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
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 17.7f));
                return true;
            });
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String latitude = requireArguments().getString(Constants.latitude, "0");
        String longitude = requireArguments().getString(Constants.longitude, "0");
        Log.d(TAG, String.format("onCreate: lat -> %s long -> %s  %s", latitude, longitude, requireContext().toString()));
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle data = new Bundle();
                data.putString(Constants.IntentTrip, param);
                navigateTo(requireView(), !TextUtils.equals(param, "")
                        ? R.id.action_nav_maps_to_nav_active_trip
                        : R.id.action_nav_maps_to_nav_main_menu, data);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        param = requireArguments().getString(Constants.maps);
        if (TextUtils.equals(param, "")) {
            getLocationUser();
            Log.i(TAG, "onMapReady: now ");
        } else {
            Double latitude = requireArguments().getDouble(Constants.latitude, 0);
            Double longitude = requireArguments().getDouble(Constants.longitude, 0);
            Log.d(TAG, String.format("onMapReady: lat -> %s long -> %s", latitude, longitude));
            googleMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(latitude, longitude), 13.9f));

            showPlaces(latitude, longitude);
        }
        Log.d(TAG, "onMapReady: " + param);
    }

    private void showPlaces(Double ltt, Double lon) {
        BitmapDescriptor icon
                = BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_myplaces);

        googleMap.addMarker(new MarkerOptions().icon(icon).position(new LatLng(ltt, lon))
                .title(getString(R.string.arrival_place)));
        try {
            for (int i = 0; i < jsonArrayResults.length(); i++) {
                JSONObject object = jsonArrayResults.getJSONObject(i);
                JSONObject locationObj = object.getJSONObject("geometry").getJSONObject("location");

                String name = object.getString("name"),
                        lat = locationObj.getString("lat"),
                        lng = locationObj.getString("lng");

                LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(name);
                markerOptions.position(latLng);
                googleMap.addMarker(markerOptions);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getLocationUser() {
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setSmallestDisplacement(10);
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.requestLocationUpdates(request, locationCallback, null);
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
        if (client != null) {
            client.removeLocationUpdates(locationCallback);
            Log.i(TAG, "onMapStop: location disable");
        }
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
        if (mMapView != null) mMapView.onDestroy();
    }
}
