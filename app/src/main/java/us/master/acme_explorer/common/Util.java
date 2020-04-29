package us.master.acme_explorer.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import us.master.acme_explorer.adapters.TripAdapter;
import us.master.acme_explorer.entity.Trip;

import static android.content.Context.MODE_PRIVATE;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class Util {

//    private static final String TAG = Util.class.getSimpleName();
    public static List<Trip> tripList;
    public static FirebaseAuth mAuth;

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

    public static long CalendarToLong(Calendar date) {
        return (date.getTimeInMillis() / 1000);
    }

    public static void getToast(Context context, int size, int message) {
        String text1 = String.format("Hay %s %s", size, context.getString(message));
        String text2 = String.format(" No hay %s", context.getString(message));
        Toast.makeText(context, size > 0 ? text1 : text2, Toast.LENGTH_LONG).show();
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

//        Log.d(TAG, "getSharePreferencesFilters: "
//                + String.format("%s", sharePreferenceData.toString()));

        return sharePreferenceData;
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
        myRecyclerView.setAdapter(tripAdapter);
    }

    public static void showDialogMessage(Context context, int msg, int posMsg, int negMsg,
                                         DialogInterface.OnClickListener positiveListener,
                                         DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setPositiveButton(posMsg, positiveListener)
                .setNegativeButton(negMsg, negativeListener)
                .show();
    }

}
