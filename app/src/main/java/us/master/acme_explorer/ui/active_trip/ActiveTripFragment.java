package us.master.acme_explorer.ui.active_trip;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.database.ValueEventListener;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.FirebaseDatabaseService;
import us.master.acme_explorer.entity.Trip;

import static java.util.Objects.requireNonNull;
import static us.master.acme_explorer.common.Util.dateFormatter;
import static us.master.acme_explorer.common.Util.mSnackBar;
import static us.master.acme_explorer.common.Util.navigateTo;
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
    private ImageView mSelectedImageView;
    private FirebaseDatabaseService databaseService;
    private LinearLayout mLayoutForm;
    private ProgressBar mProgressBar;
    private ViewGroup container;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateTo(mImageView,
                        R.id.action_nav_active_trip_fragment_to_nav_available_trips, null);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.container = container;
        View root = inflater.inflate(R.layout.fragment_active_trip, container, false);
        setView(root);

        databaseService = FirebaseDatabaseService.getServiceInstance();
        loadDatabase(databaseService, root);
        return root;
    }

    private void loadDatabase(FirebaseDatabaseService databaseService, View root) {

        databaseService.getTravelById(requireArguments().getString(Constants.IntentTravel))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                            Trip trip = dataSnapshot.getValue(Trip.class);

                            if (trip != null) {
                                updateUI(root, trip);

                                fab.setOnClickListener(view -> deleteTrip());

                                mSelectedImageView.setOnClickListener(v -> {
                                    trip.setSelected(!trip.isSelected());
                                    setState(trip, mSelectedImageView);
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void deleteTrip() {
        showTransitionForm(false, this.container, mProgressBar, mLayoutForm);
        DialogInterface.OnClickListener
                posBut = (dialog, which) ->
                databaseService
                        .deleteTravelById(requireArguments().getString(Constants.IntentTravel))
                        //TODO using removeValue method
                        .removeValue((databaseError, databaseReference) -> {
                            if (databaseError == null) {
                                mSnackBar(mImageView, requireContext(),
                                        R.string.delete_trip_msg, Snackbar.LENGTH_SHORT);
                                new Handler().postDelayed(() ->
                                        requireActivity().onBackPressed(), 1000);
                            } else {
                                mSnackBar(mImageView, requireContext(),
                                        R.string.delete_trip_error, Snackbar.LENGTH_SHORT);
                                Log.e(TAG, "deleteTrip: " + databaseError.getMessage());
                            }
                        }),
                negBut = (dialog, which) ->
                        showTransitionForm(true, this.container, mProgressBar, mLayoutForm);

        showDialogMessage(requireContext(), R.string.confirmation_delete_trip,
                android.R.string.ok, android.R.string.no, posBut, negBut);
    }

    private void setView(View root) {
        mImageView = root.findViewById(R.id.my_trip_flag_i_v);
        mTextViewPrice = root.findViewById(R.id.my_price_trip_t_v);
        mTextViewArrivalPlace = root.findViewById(R.id.my_arrival_place_trip_t_v);
        mTextViewDepartureDate = root.findViewById(R.id.my_departure_date_trip_t_v);
        mTextViewArrivalDate = root.findViewById(R.id.my_arrival_date_trip_t_v);
        mTextViewDeparturePlace = root.findViewById(R.id.my_departure_place_trip_t_v);
        mSelectedImageView = root.findViewById(R.id.my_active_selected_trip_i_v);
        mTextViewDescription = root.findViewById(R.id.my_description_trip_t_v);
        mProgressBar = root.findViewById(R.id.my_active_trip_progress_bar);
        mLayoutForm = root.findViewById(R.id.my_active_trip_form);
        fab = root.findViewById(R.id.my_active_trip_fab);
    }

    private void updateUI(View root, Trip trip) {
        Glide.with(root)
                .load(trip.getUrlImage())
                .fitCenter()
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.ic_menu_myplaces)
                .into(mImageView);

        setState(trip, mSelectedImageView);

        mTextViewArrivalPlace.setText(String.format("%s \n(%s)", trip.getArrivalPlace(), trip.getCountry()));
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

    private void setState(Trip trip, ImageView imv) {
        imv.setImageResource(
                trip.isSelected()
                        ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off
        );
    }
}
