package us.master.acme_explorer.ui.selected_trips;

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
import us.master.acme_explorer.adapters.TripAdapter;
import us.master.acme_explorer.common.FirebaseDatabaseService;
import us.master.acme_explorer.entity.Trip;

import static us.master.acme_explorer.common.Util.currentUser;

public class SelectedTripsFragment extends Fragment {

    private static final String TAG = SelectedTripsFragment.class.getSimpleName();
    private TripAdapter tripAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_trips_base_view,
                container, false);

        RelativeLayout myLayout = root.findViewById(R.id.myFilterSettings);
        myLayout.setVisibility(View.GONE);

        RecyclerView myRecyclerView = root.findViewById(R.id.my_trips_base_view_recyclerview);

        myRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        tripAdapter = new TripAdapter(TAG, requireActivity());
        myRecyclerView.setAdapter(tripAdapter);
        return root;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        loadDatabase();
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
                            tripAdapter.addItem(trip);
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
                        tripAdapter.addItem(trip);
                    } else tripAdapter.removeItem(trip);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                if (trip != null) {
                    trip.setId(dataSnapshot.getKey());
                    tripAdapter.removeItem(trip);
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

}
