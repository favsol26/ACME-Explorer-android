package us.master.acme_explorer.ui.trips;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.FirebaseDatabaseService;
import us.master.acme_explorer.common.Util;
import us.master.acme_explorer.entity.Trip;

public class TripsFragment extends Fragment {
    private static final String TAG = TripsFragment.class.getSimpleName();

    private TextInputLayout mNewTripDepartureDate;
    private TextInputLayout mNewTripArrivalDate;
    private TextInputLayout[] mNewTripILs;

    private TextInputEditText mNewTripCountryET;
    private TextInputEditText mNewTripCityET;
    private TextInputEditText mNewTripDeparturePlaceET;
    private TextInputEditText mNewTripPriceET;
    private TextInputEditText mNewTripDescriptionET;
    private TextInputEditText mNewTripDepartureDateET;
    private TextInputEditText mNewTripArrivalDateET;
    private TextInputEditText[] mNewTripIETs;

    private ImageView mNewTripFlagIV;
    private Calendar calendar = Calendar.getInstance();
    private long mNewTripDepartureDateLong = 0, mNewTripArrivalDateLong = 0;
    private ViewGroup container;

    private ProgressBar mProgressBar;
    private LinearLayout mFormLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Util.navigateTo(mProgressBar,
                        R.id.action_nav_new_trips_to_nav_available_trips, null);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        View root = inflater.inflate(R.layout.fragment_trips, container, false);

        setView(root);
        setTextWatcher();
        root.findViewById(R.id.trips_departure_date_ib).setOnClickListener(this::pickOneDate);
        root.findViewById(R.id.trips_arrival_date_ib).setOnClickListener(this::pickOneDate);
        root.findViewById(R.id.trips_button_save).setOnClickListener(v -> saveTrip());
        return root;
    }

    private void setView(View root) {
        TextInputLayout mNewTripCountry = root.findViewById(R.id.trips_destiny_country);
        TextInputLayout mNewTripCity = root.findViewById(R.id.trips_destiny_city);
        TextInputLayout mNewTripDeparturePlace = root.findViewById(R.id.trips_origin_city);
        TextInputLayout mNewTripPrice = root.findViewById(R.id.trips_price);
        TextInputLayout mNewTripDescription = root.findViewById(R.id.trips_description);
        TextInputLayout mNewTripFlag = root.findViewById(R.id.trips_flag);

        mNewTripCountryET = root.findViewById(R.id.trips_destiny_country_et);
        mNewTripCityET = root.findViewById(R.id.trips_destiny_city_et);
        mNewTripDeparturePlaceET = root.findViewById(R.id.trips_origin_city_et);
        mNewTripPriceET = root.findViewById(R.id.trips_price_et);
        mNewTripDescriptionET = root.findViewById(R.id.trips_description_et);
        mNewTripDepartureDate = root.findViewById(R.id.trips_departure_date);
        mNewTripDepartureDateET = root.findViewById(R.id.trips_departure_date_et);
        mNewTripArrivalDate = root.findViewById(R.id.trips_arrival_date);
        mNewTripArrivalDateET = root.findViewById(R.id.trips_arrival_date_et);
        mNewTripFlagIV = root.findViewById(R.id.trips_flag_iv);

        mFormLayout = root.findViewById(R.id.trips_form_layout);
        mProgressBar = root.findViewById(R.id.trips_progress_bar);

        mNewTripILs = new TextInputLayout[]{
                mNewTripCountry, mNewTripCity, mNewTripDeparturePlace, mNewTripPrice,
                mNewTripDescription, mNewTripDepartureDate, mNewTripArrivalDate, mNewTripFlag};

        mNewTripIETs = new TextInputEditText[]{
                mNewTripCountryET, mNewTripCityET, mNewTripDeparturePlaceET,
                mNewTripPriceET, mNewTripDescriptionET};
    }

    private void setTextWatcher() {
        for (int i = 0; i < mNewTripIETs.length; i++) {
            mNewTripIETs[i].addTextChangedListener(Util.mTxtChdLnr(mNewTripILs[i]));
        }
    }

    private void saveTrip() {
        String country = mNewTripCountryET.getEditableText().toString(),
                city = mNewTripCityET.getEditableText().toString(),
                departurePlace = mNewTripDeparturePlaceET.getEditableText().toString(),
                price = mNewTripPriceET.getEditableText().toString(),
                description = mNewTripDescriptionET.getEditableText().toString(),
                departureDate = mNewTripDepartureDateET.getEditableText().toString(),
                arrivalDate = mNewTripArrivalDateET.getEditableText().toString(),
                flag = mNewTripFlagIV.getContentDescription().toString();

        if (validateForm(country, city, departurePlace, price,
                description, departureDate, arrivalDate, flag)) {

            Log.d(TAG, "saveTrip: " + mNewTripFlagIV.getContentDescription());
            Util.showLoginForm(false, container, mProgressBar, mFormLayout);
            Trip trip = new Trip();
            trip.setUserUid(FirebaseAuth.getInstance().getUid());
            trip.setCountry(country);
            trip.setArrivalPlace(city);
            trip.setDeparturePlace(departurePlace);
            trip.setPrice(Long.parseLong(Util.getValue(price)));
            trip.setDescription(description);
            trip.setUrlImage(Constants.urlImages[0]);
            trip.setDepartureDate(mNewTripDepartureDateLong);
            trip.setArrivalDate(mNewTripArrivalDateLong);

            Log.d(TAG, "saveTrip: " + trip.mToString());

            FirebaseDatabaseService databaseService = FirebaseDatabaseService.getServiceInstance();
            databaseService.saveTrip(trip, (databaseError, databaseReference) -> {
                if (databaseError == null) {

                    Util.mSnackBar(mNewTripFlagIV, requireContext(), R.string.trip_saved,
                            Snackbar.LENGTH_LONG);
                    Util.navigateTo(mNewTripFlagIV,
                            R.id.action_nav_new_trips_to_nav_available_trips, null);
                }

                if (databaseError != null) {
                    Log.d(TAG, String.format("saveTrip: %s", databaseError.getMessage()));
                    Util.mSnackBar(mNewTripFlagIV, requireContext(), R.string.trip_no_saved,
                            Snackbar.LENGTH_LONG);
                }
                Util.showLoginForm(true, container, mProgressBar, mFormLayout);
            });
        }
    }

    private boolean validateForm(String... dataToValidate) {
        boolean valid = true;
        for (int i = 0; i < dataToValidate.length; i++) {
            if (TextUtils.isEmpty(dataToValidate[i])) {
                mNewTripILs[i].setError(getString(R.string.required_field));
                valid = false;
            } else mNewTripILs[i].setError(null);
        }
        return valid;
    }

    private void pickOneDate(View view) {
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(), (datePicker, year, month, day) -> {
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.set(Calendar.YEAR, year);
            calendarDate.set(Calendar.MONTH, month);
            calendarDate.set(Calendar.DAY_OF_MONTH, day);

            if (view.getId() == R.id.trips_departure_date_ib) {
                if (calendarDate.getTimeInMillis() > (new Date().getTime())) {
                    mNewTripDepartureDateET.setText(Util.dateFormatter(calendarDate));
                    mNewTripDepartureDateLong = Util.calendarToLong(calendarDate);
                    mNewTripArrivalDateET.setText("");
                    mNewTripArrivalDateLong = 0;
                    mNewTripDepartureDate.setError(null);
                } else {
                    mNewTripDepartureDate.setError(
                            getString(R.string.new_trip_date_error_1));
                }
            } else {
                if (mNewTripDepartureDateLong > 0) {
                    if (Util.calendarToLong(calendarDate) > (mNewTripDepartureDateLong)) {
                        mNewTripArrivalDateET.setText(Util.dateFormatter(calendarDate));
                        mNewTripArrivalDateLong = Util.calendarToLong(calendarDate);
                        mNewTripArrivalDate.setError(null);
                    } else
                        mNewTripArrivalDate.setError(
                                getString(R.string.new_trip_date_error_2));
                } else
                    mNewTripArrivalDate.setError(getString(R.string.new_trip_date_error_3));
            }

        }, yy, mm, dd);
        dialog.show();
    }
}

