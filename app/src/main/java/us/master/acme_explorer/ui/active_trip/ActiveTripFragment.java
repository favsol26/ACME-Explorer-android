package us.master.acme_explorer.ui.active_trip;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.FirebaseDatabaseService;
import us.master.acme_explorer.common.FirebaseStorageService;
import us.master.acme_explorer.common.GeoLocation;
import us.master.acme_explorer.entity.Trip;

import static java.util.Objects.requireNonNull;
import static us.master.acme_explorer.common.Util.currentUser;
import static us.master.acme_explorer.common.Util.dateFormatter;
import static us.master.acme_explorer.common.Util.mSnackBar;
import static us.master.acme_explorer.common.Util.navigateTo;
import static us.master.acme_explorer.common.Util.setState;
import static us.master.acme_explorer.common.Util.showDialogMessage;
import static us.master.acme_explorer.common.Util.showTransitionForm;

public class ActiveTripFragment extends Fragment {

    private static final String TAG = ActiveTripFragment.class.getSimpleName();
    private FloatingActionButton fab;
    private TextView mTextViewDescription;
    private ImageView mImageView;
    private TextView mTextViewPrice;
    private TextView mTextViewArrivalPlace;
    private TextView mTextViewDepartureDate;
    private TextView mTextViewArrivalDate;
    private TextView mTextViewDeparturePlace;
    private ImageView mImgVwStar;
    private FirebaseDatabaseService databaseService;
    private LinearLayout mLayoutForm;
    private ProgressBar mProgressBar;
    private ViewGroup container;
    private ValueEventListener valueEventListener;
    private boolean control = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                String from = requireArguments().getString(Constants.from);
                navigateTo(mImageView, !Objects.equals(from, "SelectedTripsFragment")
                                ? R.id.action_nav_active_trip_fragment_to_nav_available_trips
                                : R.id.action_nav_active_trip_fragment_to_nav_selected_trips,
                        null);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (databaseService != null && valueEventListener != null) {
            String tripId = requireArguments().getString(Constants.IntentTrip);
            databaseService.getTravelById(tripId).removeEventListener(valueEventListener);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.container = container;
        View root = inflater.inflate(R.layout.fragment_active_trip, container, false);
        setView(root);
        databaseService = FirebaseDatabaseService.getServiceInstance();

        valueEventListener = getValueEventListener(root);

        databaseService.getTravelById(requireArguments().getString(Constants.IntentTrip))
                .addValueEventListener(valueEventListener);
        return root;
    }

    private ValueEventListener getValueEventListener(View root) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    if (trip != null) {
                        Glide.with(root)
                                .load(trip.getUrlImage())
                                .fitCenter()
                                .placeholder(android.R.drawable.ic_menu_myplaces)
                                .error(android.R.drawable.ic_menu_myplaces)
                                .into(mImageView);
                        fab.setOnClickListener(view -> {
                            try {
                                Log.i(TAG, "onDataChange: " + trip.getUrlImage());
                                String fileName = trip.getUrlImage()
                                        .substring(trip.getUrlImage().lastIndexOf("IMG"),
                                                trip.getUrlImage().lastIndexOf("?"));
                                deleteTrip(dataSnapshot.getKey(), fileName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        setState(trip, mImgVwStar);
                        mImgVwStar.setOnClickListener(v -> {
                            setState(trip, mImgVwStar);
                            updateTrip(trip, dataSnapshot.getKey());
                        });
                        if (control) {
                            updateUI(trip);
                            GeoLocation location = new GeoLocation();
                            location.getLocation(
                                    trip.getArrivalPlace().concat(", " + trip.getCountry()),
                                    "destiny", container.getContext(), new GeoHandler(),
                                    trip, mTextViewArrivalPlace);
                        }
                        control = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void updateTrip(Trip trip, String tripId) {
        List<String> users = new ArrayList<>();
        boolean selectedBy = false;
        if (trip.getSelectedBy() != null) {
            users.addAll(trip.getSelectedBy());
            if (users.contains(currentUser.getUid())) {
                users.remove(currentUser.getUid());
            } else users.add(currentUser.getUid());
        } else
            users.add(currentUser.getUid());
        trip.setSelectedBy(users);
        if (trip.getSelectedBy() != null)
            selectedBy = trip.getSelectedBy().contains(currentUser.getUid());

        //TODO using set value method
        databaseService.updateTravelById(tripId)
                .setValue(trip, getListener(false, selectedBy));
    }

    private void deleteTrip(String tripId, String fileName) {
        showTransitionForm(false, this.container, mProgressBar, mLayoutForm);
        //TODO using removeValue method
        DialogInterface.OnClickListener posBut = (dialog, which) -> {
            databaseService.deleteTravelById(tripId)
                    .removeValue(getListener(true, true));
            FirebaseStorageService storageService =
                    new FirebaseStorageService(container.getContext());
            storageService.deleteImageUrl(getString(R.string.folderTrips), fileName);

        };

        DialogInterface.OnClickListener negBut = (dialog, which) ->
                showTransitionForm(true, this.container, mProgressBar, mLayoutForm);

        showDialogMessage(requireContext(), R.string.confirmation_delete_trip,
                android.R.string.ok, android.R.string.no, posBut, negBut);
    }

    private DatabaseReference.CompletionListener getListener(boolean back, Boolean selected) {
        return (databaseError, databaseReference) -> {
            if (databaseError == null) {
                mSnackBar(mImageView, this.container.getContext(),
                        back ? R.string.delete_trip_msg :
                                selected ? R.string.selected_trip : R.string.discarded_trip,
                        Snackbar.LENGTH_SHORT);
                if (back) new Handler().postDelayed(() ->
                        requireActivity().onBackPressed(), 1000);
            } else {
                if (back) mSnackBar(mImageView, this.container.getContext(),
                        R.string.delete_trip_error, Snackbar.LENGTH_SHORT);
                Log.e(TAG, "deleteTrip: " + databaseError.getMessage());
            }
        };
    }

    private void setView(View root) {
        mImageView = root.findViewById(R.id.my_trip_flag_i_v);
        mTextViewPrice = root.findViewById(R.id.my_price_trip_t_v);
        mTextViewArrivalPlace = root.findViewById(R.id.my_arrival_place_trip_t_v);
        mTextViewDepartureDate = root.findViewById(R.id.my_departure_date_trip_t_v);
        mTextViewArrivalDate = root.findViewById(R.id.my_arrival_date_trip_t_v);
        mTextViewDeparturePlace = root.findViewById(R.id.my_departure_place_trip_t_v);
        mImgVwStar = root.findViewById(R.id.my_active_selected_trip_i_v);
        mTextViewDescription = root.findViewById(R.id.my_description_trip_t_v);
        mProgressBar = root.findViewById(R.id.my_active_trip_progress_bar);
        mLayoutForm = root.findViewById(R.id.my_active_trip_form);
        fab = root.findViewById(R.id.my_active_trip_fab);
    }

    private void updateUI(Trip trip) {
        mTextViewArrivalPlace.setText(String.format("%s (%s) \n(%s)",
                trip.getArrivalPlace(), "", trip.getCountry()));
        mTextViewArrivalPlace.setTypeface(mTextViewArrivalDate.getTypeface(), Typeface.BOLD);
        mTextViewPrice.setText(String.valueOf(trip.getPrice()).concat(" $"));
        mTextViewDepartureDate.setText(dateFormatter(trip.getDepartureDate()));
        mTextViewArrivalDate.setText(dateFormatter(trip.getArrivalDate()));
        mTextViewDeparturePlace.setText(trip.getDeparturePlace());
        mTextViewDescription.setText(String.format("%s, %s %s.", trip.getDescription(),
                getString(R.string.description_date), dateFormatter(-1 * trip.getCreated())));

        String uid = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        fab.setVisibility(trip.getUserUid().equals(uid) ? View.VISIBLE : View.GONE);
    }

    private static class GeoHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String address;
            double latitude = 0D;
            double longitude = 0D;
            if (msg.what == 1) {
                Bundle bundle = msg.getData();
                address = bundle.getString("Address");
                latitude = bundle.getDouble("latitude");
                longitude = bundle.getDouble("longitude");
            } else {
                address = null;
            }
            Log.i(TAG, String.format("handleMessage: address = %s latitude -> %s longitude -> %s",
                    address, latitude, longitude));
        }
    }
}
