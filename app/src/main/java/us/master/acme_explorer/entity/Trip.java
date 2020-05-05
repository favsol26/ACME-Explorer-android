package us.master.acme_explorer.entity;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.Util;

import static java.util.Objects.hash;

public class Trip {
    private static final String TAG = Trip.class.getSimpleName();
    private boolean selected;
    private int id;
    private String userUid;
    private long departureDate, arrivalDate, price;
    private String arrivalPlace;
    private String departurePlace;
    private String description;
    private String country;
    private String urlImage;

    public Trip() {
    }

    private Trip(boolean selected, int id, long departureDate, long arrivalDate, long price,
                 String arrivalPlace, String departurePlace, String description, String country,
                 String urlImage, String userUid) {
        this.selected = selected;
        this.id = id;
        this.userUid = userUid;
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
                    Calendar.DAY_OF_MONTH, (int) (
                            ThreadLocalRandom
                                    .current()
                                    .nextLong(min % 10, (max * 2 / 80) - 15)
                    ));

            arrivalDate = (Calendar) departureDate.clone();
            arrivalDate.add(
                    Calendar.DAY_OF_MONTH,
                    1 + (int) (ThreadLocalRandom.current().nextLong(min, max) % min)
            );

            String arrivalPlace = Constants.cities[
                    (int) ThreadLocalRandom.current().nextLong(min, max) % Constants.cities.length];

            arrivalPlace = arrivalPlace.substring(0, arrivalPlace.indexOf("-"));

            String departurePlace = Constants.departurePlace[
                    (int) ThreadLocalRandom.current().nextLong(min, max)
                            % Constants.departurePlace.length];
            trips.add(
                    new Trip(
                            false, i, Util.calendarToLong(departureDate),
                            Util.calendarToLong(arrivalDate),
                            ThreadLocalRandom.current().nextLong(min, max),
                            arrivalPlace, departurePlace,
                            "V" + "iAje preCiOso por ".toLowerCase().concat(arrivalPlace),
                            Constants.citiesToCountry.get(arrivalPlace),
                            Constants.flagsLinks.get(Constants.citiesToCountry.get(arrivalPlace)),
                            "requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()"
                    )
            );
        }
        Log.d(TAG, min + " generate " + max + " Trips: " + amount);
        return trips;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
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

    public void setDepartureDate(long departureDate) {
        this.departureDate = departureDate;
    }

    public long getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(long arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getArrivalPlace() {
        return arrivalPlace;
    }

    public void setArrivalPlace(String arrivalPlace) {
        this.arrivalPlace = arrivalPlace;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDeparturePlace() {
        return departurePlace;
    }

    public void setDeparturePlace(String departurePlace) {
        this.departurePlace = departurePlace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return selected == trip.selected &&
                id == trip.id &&
                departureDate == trip.departureDate &&
                arrivalDate == trip.arrivalDate &&
                price == trip.price &&
                Objects.equals(arrivalPlace, trip.arrivalPlace) &&
                Objects.equals(departurePlace, trip.departurePlace) &&
                Objects.equals(description, trip.description) &&
                Objects.equals(country, trip.country) &&
                Objects.equals(urlImage, trip.urlImage);
    }

    @Override
    public int hashCode() {
        return hash(selected, id, departureDate, arrivalDate, price, arrivalPlace,
                departurePlace, description, country, urlImage);
    }

    public String mToString() {
        return "Trip{" +
                "selected=" + selected +
                ", id=" + id +
                ", userUid='" + userUid + '\'' +
                ", departureDate=" + Util.dateFormatter(departureDate) +
                ", arrivalDate=" + Util.dateFormatter(arrivalDate) +
                ", price=" + price +
                ", arrivalPlace='" + arrivalPlace + '\'' +
                ", departurePlace='" + departurePlace + '\'' +
                ", description='" + description + '\'' +
                ", country='" + country + '\'' +
                ", urlImage='" + urlImage + '\'' +
                '}';
    }
}
