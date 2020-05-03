package us.master.acme_explorer.ui.active_trip;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.Util;
import us.master.acme_explorer.entity.Trip;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_active_trip, container, false);
        Trip trip = Util.tripList.get(requireArguments().getInt(Constants.IntentTravel));
        setView(root);
        updateUI(root, trip);

        fab.setOnClickListener(view -> {
            Toast.makeText(requireContext(), TAG + " click", Toast.LENGTH_SHORT).show();
            Toast.makeText(requireContext(), TAG + " click", Toast.LENGTH_SHORT).show();
        });

        mSelectedImageView.setOnClickListener(v -> {
            trip.setSelected(!trip.isSelected());
            setState(trip, mSelectedImageView);
        });
        Log.d(TAG, String.format("onCreateView: id %d userId %s", trip.getId(), trip.getUserUid()));
        return root;
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
        mTextViewDepartureDate.setText(Util.dateFormatter(trip.getDepartureDate()));
        mTextViewArrivalDate.setText(Util.dateFormatter(trip.getArrivalDate()));
        mTextViewDeparturePlace.setText(trip.getDeparturePlace());
        mTextViewDescription.setText(trip.getDescription());
//        fab.setVisibility(trip.getUserUid()==);
    }

    private void setState(Trip trip, ImageView imv) {
        imv.setImageResource(
                trip.isSelected()
                        ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off
        );
    }
}
