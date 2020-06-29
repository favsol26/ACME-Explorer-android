package us.master.acme_explorer.ui.trips;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import us.master.acme_explorer.common.FirebaseStorageService;
import us.master.acme_explorer.common.PermissionsService;
import us.master.acme_explorer.entity.Trip;

import static android.app.Activity.RESULT_OK;
import static android.content.DialogInterface.OnClickListener;
import static us.master.acme_explorer.common.Util.calendarToLong;
import static us.master.acme_explorer.common.Util.dateFormatter;
import static us.master.acme_explorer.common.Util.getValue;
import static us.master.acme_explorer.common.Util.mSnackBar;
import static us.master.acme_explorer.common.Util.mTxtChdLnr;
import static us.master.acme_explorer.common.Util.navigateTo;
import static us.master.acme_explorer.common.Util.showDialogMessage;
import static us.master.acme_explorer.common.Util.showTransitionForm;


public class TripsFragment extends Fragment {
    public static final int GALLERY_PERMISSION_REQUEST = 0x512;
    public static final int PICK_PHOTO_CODE = 0x512;
    private static final String TAG = TripsFragment.class.getSimpleName();
    private String folder;
    private long mNewTripDepartureDateLong = 0, mNewTripArrivalDateLong = 0;
    private TextInputLayout mNewTripDepartureDate;
    private TextInputLayout mNewTripArrivalDate;
    private TextInputLayout mNewTripArrivalCountry;
    private TextInputLayout mNewTripArrivalCity;
    private TextInputLayout mNewTripDepartureCountry;
    private TextInputLayout mNewTripDepartureCity;
    private TextInputLayout mNewTripPrice;
    private TextInputLayout mNewTripDescription;
    private TextInputLayout mNewTripFlag;
    private TextInputLayout[] mNewTripILs;
    private TextInputEditText mNewTripArrivalCountryET;
    private TextInputEditText mNewTripArrivalCityET;
    private TextInputEditText mNewTripDepartureCountryET;
    private TextInputEditText mNewTripDepartureCityET;
    private TextInputEditText mNewTripPriceET;
    private TextInputEditText mNewTripDescriptionET;
    private TextInputEditText mNewTripDepartureDateET;
    private TextInputEditText mNewTripArrivalDateET;
    private TextInputEditText[] mNewTripIETs;
    private ImageView mNewTripFlagIV;
    private Calendar calendar = Calendar.getInstance();
    private ViewGroup container;
    private ProgressBar mProgressBar;
    private LinearLayout mFormLayout;
    private FirebaseStorageService mStorageService;
    private String from;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                OnClickListener negBut = (dialog, which) -> {
                }, posBut = (dialog, which) -> {
                    if (!TextUtils.equals(mNewTripFlagIV.getContentDescription(), getString(R.string.app_name))
                            && TextUtils.equals(from, "create")) {
                        mStorageService.deleteImageUrl(folder,
                                String.valueOf(mNewTripFlagIV.getContentDescription()));
                    }
                    navigateTo(mProgressBar,
                            R.id.action_nav_new_trips_to_nav_available_trips, null);
                };
                if (hasData(getValues()) > 0) {
                    showDialogMessage(container.getContext(),
                            null, R.string.cancel_create_trip,
                            R.string.exit, android.R.string.no, posBut, negBut);
                } else navigateTo(mProgressBar,
                        R.id.action_nav_new_trips_to_nav_available_trips, null);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private int hasData(String... values) {
        int valid = values.length;
        for (String s : values)
            if (TextUtils.equals(s, getString(R.string.app_name)) || TextUtils.equals(s, ""))
                valid--;
        return valid;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        mStorageService = new FirebaseStorageService(container.getContext());
        folder = getString(R.string.folderTrips);
        View root = inflater.inflate(R.layout.fragment_trips, container, false);
        from = requireArguments().getString(Constants.from);
        setView(root);
        mNewTripILs = new TextInputLayout[]{
                mNewTripArrivalCountry, mNewTripArrivalCity, mNewTripDepartureCountry,
                mNewTripDepartureCity, mNewTripPrice, mNewTripDescription, mNewTripDepartureDate,
                mNewTripArrivalDate, mNewTripFlag
        };

        mNewTripIETs = new TextInputEditText[]{
                mNewTripArrivalCountryET, mNewTripArrivalCityET, mNewTripDepartureCountryET,
                mNewTripDepartureCityET, mNewTripPriceET, mNewTripDescriptionET
        };
        setTextWatcher();
        root.findViewById(R.id.trips_departure_date_ib).setOnClickListener(this::pickOneDate);
        root.findViewById(R.id.trips_arrival_date_ib).setOnClickListener(this::pickOneDate);
        root.findViewById(R.id.trips_button_save).setOnClickListener(this::onClick);
        mNewTripFlagIV.setOnClickListener(v -> {
            PermissionsService service = new PermissionsService(
                    requireActivity(), requireContext(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    null,
                    new int[]{R.string.gallery_request_permission},
                    new int[]{R.string.gallery_permission_no_granted,
                            R.string.gallery_permission_no_granted_2}
            );
            service.checkPermission(mProgressBar, this::pickOnePhoto);
        });
        return root;
    }

    private void updateTrip(String string) {
        Log.d(TAG, "updateTrip: " + string);
    }

    private void setView(View root) {
        mNewTripArrivalCountry = root.findViewById(R.id.trips_destiny_country);
        mNewTripArrivalCity = root.findViewById(R.id.trips_destiny_city);
        mNewTripDepartureCity = root.findViewById(R.id.trips_origin_city);
        mNewTripDepartureCountry = root.findViewById(R.id.trips_origin_country);
        mNewTripPrice = root.findViewById(R.id.trips_price);
        mNewTripDescription = root.findViewById(R.id.trips_description);
        mNewTripFlag = root.findViewById(R.id.trips_flag);

        mNewTripArrivalCountryET = root.findViewById(R.id.trips_destiny_country_et);
        mNewTripArrivalCityET = root.findViewById(R.id.trips_destiny_city_et);
        mNewTripDepartureCityET = root.findViewById(R.id.trips_origin_city_et);
        mNewTripDepartureCountryET = root.findViewById(R.id.trips_origin_country_et);
        mNewTripPriceET = root.findViewById(R.id.trips_price_et);
        mNewTripDescriptionET = root.findViewById(R.id.trips_description_et);
        mNewTripDepartureDate = root.findViewById(R.id.trips_departure_date);
        mNewTripDepartureDateET = root.findViewById(R.id.trips_departure_date_et);
        mNewTripArrivalDate = root.findViewById(R.id.trips_arrival_date);
        mNewTripArrivalDateET = root.findViewById(R.id.trips_arrival_date_et);
        mNewTripFlagIV = root.findViewById(R.id.trips_flag_iv);
        mFormLayout = root.findViewById(R.id.trips_form_layout);
        mProgressBar = root.findViewById(R.id.trips_progress_bar);
    }

    public void pickOnePhoto() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, PICK_PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        String selectedImagePath;
        mNewTripFlag.setError(null);
        if (requestCode == PICK_PHOTO_CODE && resultCode == RESULT_OK) {
            if (resultData != null) {
                Uri uriPickedImg = resultData.getData();
                if (uriPickedImg != null) {
                    if (!TextUtils.equals(mNewTripFlagIV.getContentDescription(),
                            getString(R.string.app_name))) {
                        mStorageService.deleteImageUrl(folder,
                                String.valueOf(mNewTripFlagIV.getContentDescription()));
                    }
                    Log.i(TAG, "Uri: " + uriPickedImg.toString());
                    // Let's read picked image path using content resolver
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = container.getContext().getContentResolver()
                            .query(uriPickedImg, filePath, null,
                                    null, null);
                    try {
                        if (cursor != null && cursor.moveToFirst()) {
                            selectedImagePath =
                                    cursor.getString(cursor.getColumnIndex(filePath[0]));
                            mStorageService.getImageUrl(mNewTripFlagIV, folder, selectedImagePath);
                        }
                    } finally {// At the end remember to close the cursor or you will end with
                        // the RuntimeException!
                        assert cursor != null;
                        cursor.close();
                    }
                }
            }
        } else {
            Snackbar.make(mFormLayout, R.string.picture_no_picked, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setTextWatcher() {
        for (int i = 0; i < mNewTripIETs.length; i++) {
            mNewTripIETs[i].addTextChangedListener(mTxtChdLnr(mNewTripILs[i]));
        }
    }

    private void saveTrip() {
        String[] values = getValues();
        Log.d(TAG, "saveTrip: " + mNewTripFlagIV.getContentDescription());
        if (validateForm(values)) {
            Log.d(TAG, "saveTrip: " + mNewTripFlagIV.getContentDescription());
            showTransitionForm(false, container, mProgressBar, mFormLayout);

            OnClickListener posBut = (dialog, which) -> {
                Trip trip = getNewTrip(values[0], values[1], values[2].concat(", " + values[3]),
                        values[4], values[5]);
                saveTripInDatabase(trip);
            }, negBut = (dialog, which) ->
                    showTransitionForm(true, container, mProgressBar, mFormLayout);

            showDialogMessage(container.getContext(), null, R.string.trip_to_save,
                    android.R.string.ok, android.R.string.no, posBut, negBut);
        }
    }

    private String[] getValues() {
        return new String[]{
                mNewTripArrivalCountryET.getEditableText().toString(),//0
                mNewTripArrivalCityET.getEditableText().toString(),
                mNewTripDepartureCityET.getEditableText().toString(),//2
                mNewTripDepartureCountryET.getEditableText().toString(),
                mNewTripPriceET.getEditableText().toString(),
                mNewTripDescriptionET.getEditableText().toString(),//5
                mNewTripDepartureDateET.getEditableText().toString(),
                mNewTripArrivalDateET.getEditableText().toString(),
                mNewTripFlagIV.getContentDescription().toString()
        };
    }

    private void saveTripInDatabase(Trip trip) {
        FirebaseDatabaseService databaseService = FirebaseDatabaseService.getServiceInstance();
        databaseService.createTrip(trip, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                mSnackBar(mNewTripFlagIV, requireContext(),
                        R.string.trip_saved, Snackbar.LENGTH_LONG);

                new Handler().postDelayed(() -> navigateTo(mNewTripFlagIV,
                        R.id.action_nav_new_trips_to_nav_available_trips, null),
                        1500);
            }

            if (databaseError != null) {
                Log.d(TAG, String.format("saveTrip: %s", databaseError.getMessage()));
                mSnackBar(mNewTripFlagIV, requireContext(), R.string.trip_no_saved,
                        Snackbar.LENGTH_LONG);
                showTransitionForm(true, container, mProgressBar, mFormLayout);
            }
        });
    }

    private Trip getNewTrip(String country, String city,
                            String departurePlace, String price, String description) {
        Trip trip = new Trip();
        trip.setUserUid(FirebaseAuth.getInstance().getUid());

        trip.setCountry(country);
        trip.setArrivalPlace(city);
        trip.setDeparturePlace(departurePlace);
        trip.setPrice(Long.parseLong(getValue(price)));
        trip.setDescription(description);

        trip.setUrlImage(String.valueOf(mNewTripFlagIV.getContentDescription()));
        trip.setDepartureDate(mNewTripDepartureDateLong);
        trip.setArrivalDate(mNewTripArrivalDateLong);
        trip.setCreated(-1 * (new Date().getTime() / 1000));

        Log.d(TAG, "saveTrip: " + trip.mToString());
        return trip;
    }

    private boolean validateForm(String... dataToValidate) {
        boolean valid = true;
        for (int i = 0; i < dataToValidate.length; i++)
            if (TextUtils.isEmpty(dataToValidate[i]) ||
                    TextUtils.equals(dataToValidate[i], getString(R.string.app_name))) {
                mNewTripILs[i].setError(getString(R.string.required_field));
                valid = false;
            } else mNewTripILs[i].setError(null);
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
                    mNewTripDepartureDateET.setText(dateFormatter(calendarDate));
                    mNewTripDepartureDateLong = calendarToLong(calendarDate);
                    mNewTripArrivalDateET.setText("");
                    mNewTripArrivalDateLong = 0;
                    mNewTripDepartureDate.setError(null);
                    Log.i(TAG, String.format("pickOneDate: arrival -> %d departure -> %d",
                            mNewTripArrivalDateLong, mNewTripDepartureDateLong));
                } else {
                    mNewTripDepartureDate.setError(
                            getString(R.string.new_trip_date_error_1));
                }
            } else {
                if (mNewTripDepartureDateLong > 0) {
                    if (calendarToLong(calendarDate) > (mNewTripDepartureDateLong)) {
                        mNewTripArrivalDateET.setText(dateFormatter(calendarDate));
                        mNewTripArrivalDateLong = calendarToLong(calendarDate);
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

    private void onClick(View v) {

        if (TextUtils.equals(from, "create")) {
            saveTrip();
        } else {
            Log.d(TAG, "onClick: " + v.getId());
            updateTrip(requireArguments().getString(Constants.IntentTrip));
        }
    }
}