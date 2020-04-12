package us.master.acme_explorer.entity;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import us.master.acme_explorer.common.Constants;

public class Trip {
    private static final String TAG = Trip.class.getSimpleName();
    private boolean selected;
    private int id;
    private long departureDate, arrivalDate, price;
    private String arrivalPlace;
    private String departurePlace;
    private String description;
    private String urlImage;
    private String country;

    private Trip(int id, long price, boolean selected, long departureDate, long arrivalDate,
                 String arrivalPlace, String departurePlace, String urlImage, String country, String description) {
        this.id = id;
        this.price = price;
        this.selected = selected;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.arrivalPlace = arrivalPlace;
        this.departurePlace = departurePlace;
        this.description = description;
        this.urlImage = urlImage;
        this.country = country;
    }

    public static List<Trip> generateTrips(long amount, long min, long max) {
        Log.d(TAG, min + " generate " + max + " Trips: " + amount);
        List<Trip> trips = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < amount; i++) {
            //TODO make dynamic the trips's creation

            String arrivalCity = Constants.cities[
                    (int) ThreadLocalRandom.current().nextLong(min, max) % Constants.cities.length
                    ];
            arrivalCity = arrivalCity.substring(0, arrivalCity.indexOf("-"));

            String departurePlace = Constants.departurePlace[
                    (int) ThreadLocalRandom.current().nextLong(min, max) % Constants.departurePlace.length
                    ];

            trips.add(
                    new Trip(i, ThreadLocalRandom.current().nextLong(min, max), false,
                            i * 1000011,
                            i * 111000,
                            arrivalCity, departurePlace,
                            Constants.flagsLinks.get(Constants.citiesToCountry.get(arrivalCity)),
                            Constants.citiesToCountry.get(arrivalCity),
                            "V" + "iAje preCiOso por ".toLowerCase().concat(arrivalCity)
                    )
            );
        }
        return trips;
    }

    /*public static List<Trip> generaViajes(int numViajes) {
        List<Trip> trips = new ArrayList<>();
        int min = 75, max = 2050, aleatorio, precio;
        String lugarSalida, lugarDestino, descripcion, url;
        Calendar fechaSalida, fechaLlegada, fechaActual = Calendar.getInstance();
        long fsal, flle;
        boolean seleccionado;
        for (int i = 0; i < numViajes; i++) {
            aleatorio = ThreadLocalRandom.current().nextInt(min, max);

            lugarSalida = Constantes.lugarSalida[aleatorio % Constantes.lugarSalida.length];
            lugarDestino = Constantes.ciudades[aleatorio % Constantes.ciudades.length];
            url = Constantes.urlImagenes[aleatorio % Constantes.urlImagenes.length];
            descripcion = "Viaje precioso por " + lugarDestino;
            fechaSalida = (Calendar) fechaActual.clone();
            fechaSalida.add(Calendar.DAY_OF_MONTH, aleatorio % 10);
            fsal = fechaSalida.getTimeInMillis() / 1000;
            fechaLlegada = (Calendar) fechaSalida.clone();
            fechaLlegada.add(Calendar.DAY_OF_MONTH, 3 + aleatorio % 2);
            flle = fechaLlegada.getTimeInMillis() / 1000;
            precio = aleatorio;
            seleccionado = aleatorio % 2 == 0 ? true : false;
            trips.add(new Trip(lugarSalida, lugarDestino, descripcion, fsal, flle, precio, url, seleccionado));
        }
        return trips;
    }*/

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
