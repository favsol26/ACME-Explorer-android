package us.master.acme_explorer.entity;

import java.util.List;
import java.util.Objects;

import us.master.acme_explorer.common.Util;

import static java.util.Objects.hash;

public class Trip {
    //    private static final String TAG = Trip.class.getSimpleName();
    private String id;
    private String userUid;
    private long departureDate, arrivalDate, price, created;
    private String arrivalPlace;
    private String departurePlace;
    private String description;
    private String distance;
    private String country;
    private String urlImage;
    private List<String> selectedBy;

    public Trip() {
    }

    private Trip(long departureDate, long arrivalDate, long price, long created,
                 String arrivalPlace, String departurePlace, String description, String country,
                 String urlImage, String userUid ) {
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
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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
