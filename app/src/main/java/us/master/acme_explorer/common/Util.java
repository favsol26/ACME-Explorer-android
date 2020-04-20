package us.master.acme_explorer.common;

import android.view.ViewGroup;
import android.widget.Switch;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import us.master.acme_explorer.adapters.TripAdapter;
import us.master.acme_explorer.entity.Trip;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class Util {

    public static List<Trip> tripList;

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

    public static void setRecyclerView(ViewGroup container, Switch mySwitch,
                                       RecyclerView myRecyclerView, TripAdapter tripAdapter) {
        int ort = container.getResources().getConfiguration().orientation;
        int column = mySwitch.isChecked()
                ? ort == ORIENTATION_LANDSCAPE ? 4 : 2
                : ort == ORIENTATION_LANDSCAPE ? 3 : 1;
        tripAdapter.setColumn(column);
        myRecyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), column));
        myRecyclerView.setAdapter(tripAdapter);
    }

}
