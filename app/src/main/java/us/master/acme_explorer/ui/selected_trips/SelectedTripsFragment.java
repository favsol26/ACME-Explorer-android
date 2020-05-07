package us.master.acme_explorer.ui.selected_trips;

import android.os.Bundle;
import android.util.Log;
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

//        List<Trip> selectedTrips = new ArrayList<>();
       /*  for (Trip trip : Util.tripList) {
            if (trip.isSelected())
                selectedTrips.add(trip);
        }*/
//        Util.getToast(container.getContext(), selectedTrips.size(), R.string.menu_selected_trips);

        myRecyclerView.setLayoutManager(
                new LinearLayoutManager(container.getContext()));

        tripAdapter = new TripAdapter(TAG, 1, container.getContext());
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
                    Log.d(TAG, "onChildAdded: " + dataSnapshot.getValue());
                    if (trip != null && trip.isSelected()) {
                        trip.setId(dataSnapshot.getKey());
                        tripAdapter.addItem(trip);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Trip trip = dataSnapshot.getValue(Trip.class);
//                Log.d(TAG, "onChildChanged: " + dataSnapshot.getValue());
                if (trip != null) {
                    trip.setId(dataSnapshot.getKey());
                    if (trip.isSelected()) {
                        tripAdapter.addItem(trip);
                    } else tripAdapter.removeItem(trip);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Trip trip = dataSnapshot.getValue(Trip.class);
//                Log.d(TAG, "onChildRemoved: " + dataSnapshot.getValue());
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
