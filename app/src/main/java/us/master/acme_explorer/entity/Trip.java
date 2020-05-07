package us.master.acme_explorer.entity;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.Util;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public class Trip {
    private static final String TAG = Trip.class.getSimpleName();
    private String id;
    private String userUid;
    private long departureDate, arrivalDate, price, created;
    private String arrivalPlace;
    private String departurePlace;
    private String description;
    private String country;
    private String urlImage;
    private List<String> selectedBy;

    public Trip() {
    }

    private Trip(long departureDate, long arrivalDate, long price,
                 long created, String arrivalPlace, String departurePlace, String description, String country,
                 String urlImage, String userUid, List<String> selectedBy) {
        this.userUid = userUid;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.price = price;
        this.created = created;
        this.arrivalPlace = arrivalPlace;
        this.departurePlace = departurePlace;
        this.description = description;
        this.country = country;
        this.urlImage = urlImage;
        this.selectedBy = selectedBy;
    }

  /*  private Trip(boolean selected, long departureDate, long arrivalDate, long price,
                 long created, String arrivalPlace, String departurePlace, String description, String country,
                 String urlImage, String userUid) {
        this.selected = selected;
        this.created = created;
        this.userUid = userUid;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.price = price;
        this.arrivalPlace = arrivalPlace;
        this.departurePlace = departurePlace;
        this.description = description;
        this.country = country;
        this.urlImage = urlImage;
    }*/

    public static Trip generateTrip(long amount, long min, long max) {
//        List<Trip> trips = new ArrayList<>();
        Calendar departureDate, arrivalDate, calendarBase = Calendar.getInstance();

        //        for (int i = 0; i < amount; i++) {
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

        Trip t1 = new Trip(
                Util.calendarToLong(departureDate),
                Util.calendarToLong(arrivalDate),
                ThreadLocalRandom.current().nextLong(min, max), -1 * (new Date().getTime() / 1000),
                arrivalPlace,
                departurePlace,
                "V" + "iAje preCiOso por ".toLowerCase().concat(arrivalPlace),
                Constants.citiesToCountry.get(arrivalPlace),
                Constants.flagsLinks.get(Constants.citiesToCountry.get(arrivalPlace)),
                requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), new ArrayList<>());
//            trips.add(t1);
//        }
        Log.d(TAG, t1.toString() + " generate " + max + " Trips: " + amount);
        return t1;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public List<String> getSelectedBy() {
        return selectedBy;
    }

    public void setSelectedBy(List<String> selectedBy) {
        this.selectedBy = selectedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return departureDate == trip.departureDate &&
                arrivalDate == trip.arrivalDate &&
                price == trip.price &&
                created == trip.created &&
                Objects.equals(id, trip.id) &&
                Objects.equals(userUid, trip.userUid) &&
                Objects.equals(arrivalPlace, trip.arrivalPlace) &&
                Objects.equals(departurePlace, trip.departurePlace) &&
                Objects.equals(description, trip.description) &&
                Objects.equals(country, trip.country) &&
                Objects.equals(urlImage, trip.urlImage) &&
                Objects.equals(selectedBy, trip.selectedBy);
    }

    @Override
    public int hashCode() {
        return hash(id, userUid, departureDate, arrivalDate, price, created, arrivalPlace,
                departurePlace, description, country, urlImage, selectedBy);
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id='" + id + '\'' +
                ", userUid='" + userUid + '\'' +
                ", departureDate=" + departureDate +
                ", arrivalDate=" + arrivalDate +
                ", price=" + price +
                ", created=" + created +
                ", arrivalPlace='" + arrivalPlace + '\'' +
                ", departurePlace='" + departurePlace + '\'' +
                ", description='" + description + '\'' +
                ", country='" + country + '\'' +
                ", urlImage='" + urlImage + '\'' +
                ", selectedBy=" + selectedBy +
                '}';
    }

    public String mToString() {
        return "Trip{" +
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
                ", selectedBy=" + selectedBy +
                '}';
    }
}
