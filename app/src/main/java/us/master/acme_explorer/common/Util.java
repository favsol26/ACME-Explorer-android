package us.master.acme_explorer.common;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

}
