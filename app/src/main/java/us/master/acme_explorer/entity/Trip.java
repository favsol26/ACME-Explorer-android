package us.master.acme_explorer.entity;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.Util;

public class Trip {
    private static final String TAG = Trip.class.getSimpleName();
    private boolean selected;
    private int id;
    private long departureDate, arrivalDate, price;
    private String arrivalPlace;
    private String departurePlace;
    private String description;
    private String country;
    private String urlImage;

    private Trip(boolean selected, int id, long departureDate, long arrivalDate, long price,
                 String arrivalPlace, String departurePlace, String description, String country,
                 String urlImage) {
        this.selected = selected;
        this.id = id;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.price = price;
        this.arrivalPlace = arrivalPlace;
        this.departurePlace = departurePlace;
        this.description = description;
        this.country = country;
        this.urlImage = urlImage;
    }

    public static List<Trip> generateTrips(long amount, long min, long max) {
        List<Trip> trips = new ArrayList<>();
        Calendar departureDate, arrivalDate, calendarBase = Calendar.getInstance();

        for (int i = 0; i < amount; i++) {
            departureDate = (Calendar) calendarBase.clone();
            departureDate.add(
                    Calendar.DAY_OF_MONTH,
                    (int) (ThreadLocalRandom.current().nextLong(min, max) % max / 30)
            );

            arrivalDate = (Calendar) departureDate.clone();
            arrivalDate.add(
                    Calendar.DAY_OF_MONTH,
                    3 + (int) (ThreadLocalRandom.current().nextLong(min, max) % min)
            );

            String arrivalPlace = Constants.cities[
                    (int) ThreadLocalRandom.current().nextLong(min, max) % Constants.cities.length];

            arrivalPlace = arrivalPlace.substring(0, arrivalPlace.indexOf("-"));

            String departurePlace = Constants.departurePlace[
                    (int) ThreadLocalRandom.current().nextLong(min, max) % Constants.departurePlace.length
                    ];

            trips.add(
                    new Trip(
                            false, i, Util.Calendar2long(departureDate),
                            Util.Calendar2long(arrivalDate),
                            ThreadLocalRandom.current().nextLong(min, max),
                            arrivalPlace, departurePlace,
                            "V" + "iAje preCiOso por ".toLowerCase().concat(arrivalPlace),
                            Constants.citiesToCountry.get(arrivalPlace),
                            Constants.flagsLinks.get(Constants.citiesToCountry.get(arrivalPlace))
                    )
            );
        }
        Log.d(TAG, min + " generate " + max + " Trips: " + amount);
        return trips;
    }

    public long getPrice() {
        return price;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getDepartureDate() {
        return departureDate;
    }

    public long getArrivalDate() {
        return arrivalDate;
    }

    public String getArrivalPlace() {
        return arrivalPlace;
    }

    public String getCountry() {
        return country;
    }

    public String getDeparturePlace() {
        return departurePlace;
    }

    public String getDescription() {
        return description;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public int getId() {
        return id;
    }
}
