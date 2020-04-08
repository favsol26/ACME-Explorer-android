package us.master.acme_explorer.common;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import us.master.acme_explorer.adapters.TripAdapter;
import us.master.acme_explorer.entity.Trip;

public class Util {

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

    public static long Calendar2long(Calendar date) {
        return (date.getTimeInMillis() / 1000);
    }

    public static void setRecyclerView(ViewGroup container, Switch mySwitch,
                                       List<Trip> Trips, RecyclerView myRecyclerView, String aClass) {
        myRecyclerView.setLayoutManager(
                new GridLayoutManager(container.getContext(),
                        (mySwitch.isChecked()) ? 2 : 1));

        myRecyclerView.setAdapter(
                new TripAdapter(Trips,
                        aClass,
                        (mySwitch.isChecked()) ? 2 : 1));
    }

    public static View.OnClickListener getLayoutOnClickListener(ViewGroup container) {
        return v -> {
            Log.d("onCreateView: ", "");
            Toast.makeText(container.getContext(), "filter", Toast.LENGTH_SHORT).show();
        };
    }

    public static View.OnClickListener getSwitchOnClickListener(ViewGroup container, Switch mySwitch,
                                                          RecyclerView myRecyclerView,
                                                          List<Trip> selectedTrips, String aClass) {
        return V -> setRecyclerView(container, mySwitch, selectedTrips, myRecyclerView, aClass);
    }
}
