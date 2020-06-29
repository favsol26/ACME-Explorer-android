package us.master.acme_explorer.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
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

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import us.master.acme_explorer.adapters.TripRecyclerViewAdapter;
import us.master.acme_explorer.entity.Trip;

import static android.content.Context.MODE_PRIVATE;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class Util {
    public static boolean controlFilter = false;
    public static boolean locationEnabled = false;
    public static FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    public static GoogleSignInOptions googleSignInOptions;
    public static JSONArray jsonArrayResults;

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

    public static HashMap<String, Long> getSharedPreferenceFilters(Context context) {
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
                                       RecyclerView myRecyclerView, TripRecyclerViewAdapter tripRecyclerViewAdapter) {
        int ort = context.getResources().getConfiguration().orientation;
        int column = mySwitch.isChecked()
                ? ort == ORIENTATION_LANDSCAPE ? 3 : 2
                : ort != ORIENTATION_LANDSCAPE ? 1 : 2;

        String text = mySwitch.isChecked()
                ? ort == ORIENTATION_LANDSCAPE ? "\n" : "\n"
                : ort != ORIENTATION_LANDSCAPE ? " " : " ";

        int size = mySwitch.isChecked()
                ? ort == ORIENTATION_LANDSCAPE ? 1 : 1
                : ort != ORIENTATION_LANDSCAPE ? 2 : 2;

        tripRecyclerViewAdapter.setSize(size);
        tripRecyclerViewAdapter.setFormatText(text);
        GridLayoutManager layoutManager = new GridLayoutManager(context, column);
        myRecyclerView.setLayoutManager(layoutManager);
    }

    public static void showDialogMessage(Context context, @Nullable Integer tittle, int msg, int posMsg, int negMsg,
                                         DialogInterface.OnClickListener positiveListener,
                                         DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (tittle != null) builder.setTitle(tittle);
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
