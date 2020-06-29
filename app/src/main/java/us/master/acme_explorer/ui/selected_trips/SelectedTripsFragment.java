package us.master.acme_explorer.ui.selected_trips;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import us.master.acme_explorer.R;
import us.master.acme_explorer.adapters.TripRecyclerViewAdapter;
import us.master.acme_explorer.common.FirebaseDatabaseService;
import us.master.acme_explorer.common.PermissionsService;
import us.master.acme_explorer.entity.Trip;

import static us.master.acme_explorer.common.Util.currentUser;
import static us.master.acme_explorer.common.Util.locationEnabled;

public class SelectedTripsFragment extends Fragment {

    private static final String TAG = SelectedTripsFragment.class.getSimpleName();
    private TripRecyclerViewAdapter tripRecyclerViewAdapter;
    private RelativeLayout myLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_trips_base_view,
                container, false);

        myLayout = root.findViewById(R.id.myFilterSettings);
        myLayout.setVisibility(View.GONE);

        RecyclerView myRecyclerView = root.findViewById(R.id.my_trips_base_view_recyclerview);

        myRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        tripRecyclerViewAdapter = new TripRecyclerViewAdapter(TAG, requireActivity());
        myRecyclerView.setAdapter(tripRecyclerViewAdapter);
        return root;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (locationEnabled) loadDatabase();
        else requestLocation();
    }

    private void loadDatabase() {
        FirebaseDatabaseService databaseService = FirebaseDatabaseService.getServiceInstance();
        databaseService.getTrips().orderByChild("created").addChildEventListener(getListener());
    }

    private ChildEventListener getListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    if (trip != null && trip.getSelectedBy() != null) {
                        if (trip.getSelectedBy().contains(currentUser.getUid())) {

                            trip.setId(dataSnapshot.getKey());
                            tripRecyclerViewAdapter.addItem(trip);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                if (trip != null) {
                    trip.setId(dataSnapshot.getKey());
                    if (trip.getSelectedBy() != null && trip.getSelectedBy().contains(currentUser.getUid())) {
                        tripRecyclerViewAdapter.addItem(trip);
                    } else tripRecyclerViewAdapter.removeItem(trip);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                if (trip != null) {
                    trip.setId(dataSnapshot.getKey());
                    tripRecyclerViewAdapter.removeItem(trip);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void requestLocation() {
        PermissionsService permissionsService = new PermissionsService(
                requireActivity(),
                requireContext(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                null,
                new int[]{R.string.location_rationale_2},
                new int[]{R.string.location_permission_no_granted_2,
                        R.string.location_permission_no_granted_3}
        );
        permissionsService.checkPermission(myLayout, this::enableLocation);
    }

    private void enableLocation() {
        locationEnabled = true;
        loadDatabase();
    }
}
