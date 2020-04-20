package us.master.acme_explorer.ui.active_trip;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.Objects;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.Util;
import us.master.acme_explorer.entity.Trip;

public class ActiveTripFragment extends Fragment {

    private static final String TAG = ActiveTripFragment.class.getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_active_trip, container, false);
        Trip trip = Util.tripList.get(
                Objects.requireNonNull(getArguments()).getInt(Constants.IntentTravel)
        );

        ImageView mImageView = root.findViewById(R.id.my_trip_flag_i_v);
        TextView mTextViewPrice = root.findViewById(R.id.my_price_trip_t_v);
        TextView mTextViewArrivalPlace = root.findViewById(R.id.my_arrival_place_trip_t_v);
        TextView mTextViewDepartureDate = root.findViewById(R.id.my_departure_date_trip_t_v);
        TextView mTextViewArrivalDate = root.findViewById(R.id.my_arrival_date_trip_t_v);
        TextView mTextViewDeparturePlace = root.findViewById(R.id.my_departure_place_trip_t_v);
        ImageView mSelectedImageView = root.findViewById(R.id.my_active_selected_trip_i_v);
        TextView mTextViewDescription = root.findViewById(R.id.my_description_trip_t_v);

        Glide.with(root)
                .load(trip.getUrlImage())
                .fitCenter()
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.ic_menu_myplaces)
                .into(mImageView);

        setState(trip, mSelectedImageView);

        mSelectedImageView.setOnClickListener(v -> {
            trip.setSelected(!trip.isSelected());
            setState(trip, mSelectedImageView);
        });

        mTextViewArrivalPlace.setText(String.format("%s \n(%s)", trip.getArrivalPlace(), trip.getCountry()));
        mTextViewArrivalPlace.setTypeface(mTextViewArrivalDate.getTypeface(), Typeface.BOLD);
        mTextViewPrice.setText(String.valueOf(trip.getPrice()).concat(" $"));
        mTextViewDepartureDate.setText(Util.dateFormatter(trip.getDepartureDate()));
        mTextViewArrivalDate.setText(Util.dateFormatter(trip.getArrivalDate()));
        mTextViewDeparturePlace.setText(trip.getDeparturePlace());
        mTextViewDescription.setText(trip.getDescription());
        Log.d(TAG, "onCreateView: " + trip.getId());
        return root;
    }

    private void setState(Trip trip, ImageView imv) {
        imv.setImageResource(
                trip.isSelected()
                        ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off
        );
    }
}
