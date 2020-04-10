package us.master.acme_explorer.entity;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import us.master.acme_explorer.common.Constants;

public class Trip {
    private static final String TAG = Trip.class.getSimpleName();
    private int price, id;
    private boolean selected;
    private long departureDate, arrivalDate;
    private String arrivalPlace, departurePlace, description, urlImage;

    private Trip(int id, int price, boolean selected, long departureDate, long arrivalDate,
                 String arrivalPlace, String departurePlace, String description, String urlImage) {
        this.id = id;
        this.price = price;
        this.selected = selected;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.arrivalPlace = arrivalPlace;
        this.departurePlace = departurePlace;
        this.description = description;
        this.urlImage = urlImage;
    }

    public static List<Trip> generateTrips(long amount) {
        Log.d(TAG, "generateTrips: " + amount);
        List<Trip> trips = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            //TODO make dynamic the trips's creation
            trips.add(
                    new Trip(i, 10000, i % 2 == 0, i * 1000011,
                            i * 111000, Constants.cities[i % Constants.cities.length],
                            "departure", "description",
                            Constants.urlImages[i % Constants.urlImages.length])
            );
        }
        return trips;
    }

    public int getPrice() {
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
