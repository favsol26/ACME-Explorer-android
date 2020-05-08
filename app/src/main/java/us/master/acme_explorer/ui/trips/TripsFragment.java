package us.master.acme_explorer.ui.trips;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.FirebaseDatabaseService;
import us.master.acme_explorer.entity.Trip;

import static android.app.Activity.RESULT_OK;
import static us.master.acme_explorer.common.Util.calendarToLong;
import static us.master.acme_explorer.common.Util.dateFormatter;
import static us.master.acme_explorer.common.Util.getValue;
import static us.master.acme_explorer.common.Util.mSnackBar;
import static us.master.acme_explorer.common.Util.mTxtChdLnr;
import static us.master.acme_explorer.common.Util.navigateTo;
import static us.master.acme_explorer.common.Util.showDialogMessage;
import static us.master.acme_explorer.common.Util.showTransitionForm;

public class TripsFragment extends Fragment {
    private static final String TAG = TripsFragment.class.getSimpleName();
    private static final int PICK_PHOTO = 0x512;
    private static final int GALLERY_PERMISSION_REQUEST = 0x842;

    private TextInputLayout mNewTripDepartureDate;
    private TextInputLayout mNewTripArrivalDate;
    private TextInputLayout mNewTripCountry;
    private TextInputLayout mNewTripCity;
    private TextInputLayout mNewTripDeparturePlace;
    private TextInputLayout mNewTripPrice;
    private TextInputLayout mNewTripDescription;
    private TextInputLayout mNewTripFlag;
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
    private StorageReference mStorageRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference("ACME-Explorer");

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                DialogInterface.OnClickListener posBut = (dialog, which) ->
                        navigateTo(mProgressBar,
                                R.id.action_nav_new_trips_to_nav_available_trips, null);
                DialogInterface.OnClickListener negBut = (dialog, which) -> {
                };
                showDialogMessage(container.getContext(), R.string.cancel_create_trip,
                        R.string.exit, android.R.string.no, posBut, negBut);
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
        mNewTripILs = new TextInputLayout[]{
                mNewTripCountry, mNewTripCity, mNewTripDeparturePlace, mNewTripPrice,
                mNewTripDescription, mNewTripDepartureDate, mNewTripArrivalDate, mNewTripFlag
        };

        mNewTripIETs = new TextInputEditText[]{
                mNewTripCountryET, mNewTripCityET, mNewTripDeparturePlaceET,
                mNewTripPriceET, mNewTripDescriptionET
        };
        setTextWatcher();
        root.findViewById(R.id.trips_departure_date_ib).setOnClickListener(this::pickOneDate);
        root.findViewById(R.id.trips_arrival_date_ib).setOnClickListener(this::pickOneDate);
        root.findViewById(R.id.trips_button_save).setOnClickListener(v -> saveTrip());
        mNewTripFlagIV.setOnClickListener(v -> checkPermissions());
        return root;
    }

    private void setView(View root) {
        mNewTripCountry = root.findViewById(R.id.trips_destiny_country);
        mNewTripCity = root.findViewById(R.id.trips_destiny_city);
        mNewTripDeparturePlace = root.findViewById(R.id.trips_origin_city);
        mNewTripPrice = root.findViewById(R.id.trips_price);
        mNewTripDescription = root.findViewById(R.id.trips_description);
        mNewTripFlag = root.findViewById(R.id.trips_flag);

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

    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    Snackbar.make(mFormLayout, R.string.gallery_request_permission,
                            Snackbar.LENGTH_LONG).setAction(R.string.allow_permission,
                            click -> ActivityCompat.requestPermissions(requireActivity(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    GALLERY_PERMISSION_REQUEST)).show();
                } else {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            GALLERY_PERMISSION_REQUEST);
                }
            } else {
                pickOnePhoto();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (GALLERY_PERMISSION_REQUEST == requestCode) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        pickOnePhoto();
                    } else Snackbar.make(mFormLayout,
                            R.string.gallery_permission_no_granted,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private void pickOnePhoto() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, PICK_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        String selectedImagePath;
        if (requestCode == PICK_PHOTO && resultCode == RESULT_OK) {
            if (resultData != null) {
                Uri uriPickedImg = resultData.getData();
                assert uriPickedImg != null;
                Log.i(TAG, "Uri: " + uriPickedImg.toString());

                // Let's read picked image path using content resolver
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = container.getContext()
                        .getContentResolver()
                        .query(uriPickedImg, filePath, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {

                        selectedImagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                        getImageUrl(mStorageRef, mNewTripFlagIV, "Trips", selectedImagePath);
                    }
                } finally {
                    // At the end remember to close the cursor or you will end with the RuntimeException!
                    assert cursor != null;
                    cursor.close();
                }
            } else {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*      storage files out from default app dir API 26 or higher
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());*/
    private void getImageUrl(StorageReference mStorageRef, ImageView mNewTripFlagIV, String folder, String path) {
        String fileName = "IMG_" + new Date().getTime() + path.substring(path.lastIndexOf("."));

        Uri file = Uri.fromFile(new File(path));
        UploadTask uploadTask = mStorageRef
                .child(folder).child("images").child(fileName).putFile(file);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e(TAG, String.format("getImageUrl: %d",
                        Objects.requireNonNull(task.getResult()).getTotalByteCount()));
                mStorageRef.getDownloadUrl().addOnCompleteListener(
                        task1 -> mNewTripFlagIV.setContentDescription(String.valueOf(task1.getResult()))
                );
            }
        });
        uploadTask.addOnFailureListener(e -> Log.e(TAG, "getImageUrl: " + e.getMessage()));
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

            DialogInterface.OnClickListener posBut = (dialog, which) -> {
                Trip trip = getNewTrip(values[0], values[1], values[2], values[3], values[4]);
                saveTripInDatabase(trip);
            };
            DialogInterface.OnClickListener negBut = (dialog, which) ->
                    showTransitionForm(true, container, mProgressBar, mFormLayout);

            showDialogMessage(container.getContext(), R.string.trip_to_save,
                    android.R.string.ok, android.R.string.no, posBut, negBut);
        }
    }

    private String[] getValues() {
        return new String[]{
                mNewTripCountryET.getEditableText().toString(),
                mNewTripCityET.getEditableText().toString(),
                mNewTripDeparturePlaceET.getEditableText().toString(),
                mNewTripPriceET.getEditableText().toString(),
                mNewTripDescriptionET.getEditableText().toString(),
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

    private Trip getNewTrip(String country, String city, String departurePlace, String price,
                            String description) {
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
}

