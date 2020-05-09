package us.master.acme_explorer.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import us.master.acme_explorer.adapters.TripAdapter;
import us.master.acme_explorer.entity.Trip;

import static android.content.Context.MODE_PRIVATE;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class Util {
    private static final String TAG = Util.class.getSimpleName();
    public static FirebaseAuth mAuth;
    public static GoogleSignInOptions googleSignInOptions;
    public static FirebaseUser currentUser;
    public static boolean controlFilter = false;
    private static String fileName;

    public static void getImageUrl(StorageReference mStorageRef,
                                   ImageView photoImgVw, String folder, String path) {
        fileName = "IMG_" + new Date().getTime() + path.substring(path.lastIndexOf("."));
        Uri file = Uri.fromFile(new File(path));
        UploadTask uploadTask = mStorageRef.child(folder).child("images")
                .child(fileName).putFile(file);

        uploadTask.addOnFailureListener(e -> Log.e(TAG, "getImageUrl: " + e.getMessage()));
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "getImageUrl: image saved");
                mStorageRef.child(folder).child("images").child(fileName)
                        .getDownloadUrl().addOnCompleteListener(task1 -> {
                    String url = (String.valueOf(task1.getResult()));
                    photoImgVw.setContentDescription(url);
                    Glide.with(photoImgVw)
                            .load(url)
                            .placeholder(android.R.drawable.ic_menu_myplaces)
                            .into(photoImgVw);
                    Log.i(TAG, "getImageUrl: image url confirmed ->" + url);
                });
            }
        });

    }

    public static void deleteImageUrl(StorageReference mStorageRef, String folder) {
        Task<Void> task1 = mStorageRef.child(folder).child("images").child(fileName).delete();
        task1.addOnCompleteListener(command -> {
            if (command.isSuccessful()) {
                Log.d(TAG, "deleteImageUrl: image deleted");
            }
        });
        task1.addOnFailureListener(e -> Log.e(TAG, "deleteImageUrl: error " + e.getMessage()));
    }

    public static String dateFormatter(Calendar calendar) {
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
        calendar.setTimeInMillis(0);
        calendar.set(yy, mm, dd, 0, 0, 0);
        Date chosenDate = calendar.getTime();
        return (dateFormat.format(chosenDate));
    }

    public static String dateFormatter(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date * 1000);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
        Date chosenDate = calendar.getTime();
        return (dateFormat.format(chosenDate));
    }
//    public static void getToast(Context context, int size, int message) {
//        String text1 = String.format("Hay %s %s", size, context.getString(message));
//        String text2 = String.format(" No hay %s", context.getString(message));
//        Toast.makeText(context, size > 0 ? text1 : text2, Toast.LENGTH_LONG).show();

//    }

    public static long calendarToLong(Calendar date) {
        return (date.getTimeInMillis() / 1000);
    }

    public static void checkInstance() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
    }

    public static void mSnackBar(View v, Context context, int text, int length) {
        Snackbar.make(v, context.getString(text), length).show();
    }

    public static HashMap getSharedPreferenceFilters(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constants.filterPreferences, MODE_PRIVATE);

        int vMinPrice = sharedPreferences.getInt(Constants.minPrice, 0);
        int vMaxPrice = sharedPreferences.getInt(Constants.maxPrice, 0);
        long vDateStartToFilter = sharedPreferences.getLong(Constants.dateStart, 0);
        long vDateEndToFilter = sharedPreferences.getLong(Constants.dateEnd, 0);

        HashMap<String, Long> sharePreferenceData = new HashMap<>();
        sharePreferenceData.put(Constants.minPrice, (long) vMinPrice);
        sharePreferenceData.put(Constants.maxPrice, (long) vMaxPrice);
        sharePreferenceData.put(Constants.dateStart, vDateStartToFilter);
        sharePreferenceData.put(Constants.dateEnd, vDateEndToFilter);

        return sharePreferenceData;
    }

    public static String getValue(String data) {
        return !Objects.equals(data, "") ? data : "0";
    }

    public static void navigateTo(View V, int direction, @Nullable Bundle bundle) {
        if (bundle == null) Navigation.findNavController(V).navigate(direction);
        else Navigation.findNavController(V).navigate(direction, bundle);
    }

    public static void setRecyclerView(Context context, Switch mySwitch,
                                       RecyclerView myRecyclerView, TripAdapter tripAdapter) {
        int ort = context.getResources().getConfiguration().orientation;
        int column = mySwitch.isChecked()
                ? ort == ORIENTATION_LANDSCAPE ? 3 : 2
                : ort != ORIENTATION_LANDSCAPE ? 1 : 2;

        String text = mySwitch.isChecked()
                ? ort == ORIENTATION_LANDSCAPE ? "\n" : "\n"
                : ort != ORIENTATION_LANDSCAPE ? " " : " ";
        tripAdapter.setColumn(column);
        tripAdapter.setFormatText(text);
        GridLayoutManager layoutManager = new GridLayoutManager(context, column);
        myRecyclerView.setLayoutManager(layoutManager);
    }

    public static void showDialogMessage(Context context, int msg, int posMsg, int negMsg,
                                         DialogInterface.OnClickListener positiveListener,
                                         DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setPositiveButton(posMsg, positiveListener)
                .setNegativeButton(negMsg, negativeListener)
                .setCancelable(false)
                .show();
    }

    public static TextWatcher mTxtChdLnr(TextInputLayout inputLayout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public static void showTransitionForm(boolean show, ViewGroup container,
                                          ProgressBar mProgressBar, LinearLayout mLayoutForm) {
        TransitionSet transitionSet = new TransitionSet();
        Transition layoutFade = new AutoTransition();
        layoutFade.setDuration(500);
        transitionSet.addTransition(layoutFade);
        TransitionManager.beginDelayedTransition(container, transitionSet);

        mProgressBar.setVisibility(show ? View.GONE : View.VISIBLE);
        mLayoutForm.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public static void setState(Trip trip, ImageView imv) {
        boolean hasIt = false;
        if (trip.getSelectedBy() != null) {
            hasIt = trip.getSelectedBy().contains(currentUser.getUid());
        }
        imv.setImageResource(hasIt
                ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off
        );
    }
}
