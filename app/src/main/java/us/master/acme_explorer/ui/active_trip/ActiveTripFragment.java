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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
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
import us.master.acme_explorer.common.Util;
import us.master.acme_explorer.entity.Trip;

import static java.util.Objects.requireNonNull;
import static us.master.acme_explorer.common.Util.currentUser;
import static us.master.acme_explorer.common.Util.dateFormatter;
import static us.master.acme_explorer.common.Util.mSnackBar;
import static us.master.acme_explorer.common.Util.navigateTo;
import static us.master.acme_explorer.common.Util.setState;
import static us.master.acme_explorer.common.Util.showDialogMessage;
import static us.master.acme_explorer.common.Util.showTransitionForm;

public class ActiveTripFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ActiveTripFragment.class.getSimpleName();
    private static Double lon;
    private static Double lat;
    private boolean control = true;
    private String weather = "";
    private String tripId;

    private Button mBtnArrivalPlacesSites;
    private FloatingActionsMenu mainMenu;
    private FloatingActionButton fabDelete;
    private ImageView mImageView;
    private ImageView mImgVwStar;
    private TextView mTextViewDepartureDate;
    private TextView mTextViewArrivalDate;
    private TextView mTextViewPrice;
    private TextView mTextViewArrivalPlace;
    private TextView mTextViewDeparturePlace;
    private TextView mTextViewDescription;

    private ViewGroup container;
    private LinearLayout mLayoutForm;
    private ProgressBar mProgressBar;

    private FirebaseDatabaseService databaseService;
    private ValueEventListener valueEventListener;
    //    private FloatingActionButton fabEdit;
    //    private FloatingActionButton fab;

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
        mBtnArrivalPlacesSites.setEnabled(false);

        databaseService = FirebaseDatabaseService.getServiceInstance();
        valueEventListener = getValueEventListener(root);
        tripId = requireArguments().getString(Constants.IntentTrip);

        databaseService.getTravelById(tripId).addValueEventListener(valueEventListener);
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

                        setOnClickListeners(dataSnapshot, trip);
                        if (control) {
                            updateUI(trip);
                            GeoLocation location = new GeoLocation();
                            location.getLocation("destiny", requireActivity(),
                                    new GeoHandler(), trip, mTextViewArrivalPlace,
                                    mBtnArrivalPlacesSites, null);
                        } else {
                            String now = mTextViewArrivalPlace.getText().toString();
                            weather = "\n" + now.substring(now.indexOf("("), now.indexOf(")") + 1);
                            updateUI(trip);
                        }
                        setState(trip, mImgVwStar);
                        control = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void setOnClickListeners(@NonNull DataSnapshot dataSnapshot, Trip trip) {
        fabDelete.setOnClickListener(view ->
                deleteTrip(dataSnapshot.getKey(), trip.getUrlImage())
        );
        mImgVwStar.setOnClickListener(v -> {
            if (mainMenu.isExpanded()) mainMenu.collapse();
            setState(trip, mImgVwStar);
            updateTrip(trip, dataSnapshot.getKey());
        });
        mBtnArrivalPlacesSites.setEnabled(false);
        mBtnArrivalPlacesSites.setOnClickListener(this);
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

    private void deleteTrip(String tripId, String urlImage) {
        showTransitionForm(false, this.container, mProgressBar, mLayoutForm);
        //TODO using removeValue method
        DialogInterface.OnClickListener posBut = (dialog, which) -> {
            databaseService.deleteTravelById(tripId)
                    .removeValue(getListener(true, true));
            FirebaseStorageService storageService =
                    new FirebaseStorageService(container.getContext());
            storageService.deleteImageUrl(getString(R.string.folderTrips), urlImage);
        };

        DialogInterface.OnClickListener negBut = (dialog, which) ->
                showTransitionForm(true, this.container, mProgressBar, mLayoutForm);

        showDialogMessage(requireContext(), null, R.string.confirmation_delete_trip,
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
        mainMenu = root.findViewById(R.id.my_active_trip_fab_menu);
        fabDelete = root.findViewById(R.id.my_active_trip_fab_delete);
        mBtnArrivalPlacesSites = root.findViewById(R.id.my_active_trip_sites_btn);

        root.findViewById(R.id.my_active_trip_form).setOnClickListener(v -> {
            if (mainMenu.isExpanded()) mainMenu.collapse();
        });
    }

    private void updateUI(Trip trip) {
        mTextViewArrivalPlace.setText(String.format("%s %s \n(%s)",
                trip.getArrivalPlace(), weather, trip.getCountry()));
        mTextViewArrivalPlace.setTypeface(mTextViewArrivalDate.getTypeface(), Typeface.BOLD);
        mTextViewPrice.setText(String.valueOf(trip.getPrice()).concat(" $"));
        mTextViewDepartureDate.setText(dateFormatter(trip.getDepartureDate()));
        mTextViewArrivalDate.setText(dateFormatter(trip.getArrivalDate()));
        mTextViewDeparturePlace.setText(trip.getDeparturePlace());
        mTextViewDescription.setText(String.format("%s \n%s %s.", trip.getDescription(),
                getString(R.string.description_date), dateFormatter(-1 * trip.getCreated())));

        String uid = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        mainMenu.setVisibility(trip.getUserUid().equals(uid) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, String.format("showPlaces: %s from long: %s lat: %s",
                ((Button) v).getText().toString(), lon, lat));
        Bundle bundle = new Bundle();
        bundle.putDouble(Constants.longitude, lon);
        bundle.putDouble(Constants.latitude, lat);
        bundle.putString(Constants.maps, tripId);
        Util.navigateTo(v, R.id.action_nav_active_trip_to_nav_maps, bundle);
        control = true;
    }

    public static class GeoHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
//            String address;
            double latitude = 0D;
            double longitude = 0D;
            if (msg.what == 1) {
                Bundle bundle = msg.getData();
//                address = bundle.getString("Address");
                latitude = bundle.getDouble("latitude");
                longitude = bundle.getDouble("longitude");
            }
//            else {
//                address = null;
//            }
//            Log.i(TAG, String.format("handleMessage: address = %s latitude -> %s longitude -> %s",
//                    address, latitude, longitude));
            lat = (latitude);
            lon = (longitude);
        }
    }
}
