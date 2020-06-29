package us.master.acme_explorer.common;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import us.master.acme_explorer.R;
import us.master.acme_explorer.adapters.TripRecyclerViewAdapter;
import us.master.acme_explorer.entity.Trip;
import us.master.acme_explorer.web.VolleySingleton;

import static us.master.acme_explorer.common.Util.jsonArrayResults;

public class GeoLocation {
    private static final String TAG = GeoLocation.class.getSimpleName();
    private Context context;
    private TextView mTextViewArrivalPlace;
    private Button mBtnArrivalPlacesSites;
    private FusedLocationProviderClient client;

    public void getLocation(String from, Activity activity, Handler handler, Trip trip,
                            @Nullable TextView txtVwArrivalPlaces, @Nullable Button btnPlaces,
                            @Nullable TripRecyclerViewAdapter adapter) {
        this.mBtnArrivalPlacesSites = btnPlaces;
        this.context = activity.getApplicationContext();
        this.mTextViewArrivalPlace = txtVwArrivalPlaces;
        this.client = LocationServices.getFusedLocationProviderClient(activity);

        String location = TextUtils.equals(from, "origin")
                ? trip.getDeparturePlace()
                : trip.getArrivalPlace().concat(", " + trip.getCountry());
        Thread thread = new Thread() {


            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null, radius = "1500", type = "restaurant";
                double latitude = 0D;
                double longitude = 0D;
                try {
                    List<Address> addressList;
                    int i = 0;
                    do {
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);
                        } catch (Exception e) {
                            addressList = geocoder.getFromLocationName(location, 1);
                            e.printStackTrace();
                        }
                        if (addressList != null && addressList.size() > 0) {
                            Address address = addressList.get(0);
                            StringBuilder builder = new StringBuilder();
                            builder.append(address.getAddressLine(0)).append("\n");
                            builder.append(address.getLatitude()).append("\n");
                            builder.append(address.getLongitude()).append("\n");
                            latitude = address.getLatitude();
                            longitude = address.getLongitude();
                            result = builder.substring(0);
                        }
                        i++;
                    } while (addressList == null || i > 3);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = String.format("address  : %s ->lat & log %s", location, result);
                        bundle.putString("Address", result);
                        bundle.putDouble("latitude", latitude);
                        bundle.putDouble("longitude", longitude);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                    if (TextUtils.equals(from, "destiny")
                            && txtVwArrivalPlaces != null && adapter == null) {
                        Log.i(TAG, "run: " + result);
                        requestPlaces(latitude, longitude, radius, type);
                        getTemp(latitude, longitude, trip);
                    } else {
                        getDistance(latitude, longitude, trip, adapter);
                    }
                }
            }

        };
        thread.start();

    }

    private void processingResponse(JSONObject response, Trip trip) {
        try {
            JSONArray weather = new JSONArray(response.getString("weather"));
            JSONObject climate = new JSONObject(weather.get(0).toString());
            JSONObject temp = new JSONObject(response.getString(context.getString(R.string.main)));
            this.mTextViewArrivalPlace.setText(
                    String.format("%s \n(%s) \n(%s)",
                            trip.getArrivalPlace(),
                            temp.getString("temp").concat("°, ")
                                    .concat(climate.getString("description")),
                            trip.getCountry()
                    ));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processResponse(JSONObject response) {
        try {
            //Get attribute "status"
            String msg;
            String status = response.getString("status");

            switch (status) {
                case "OK": // SUCCESS
                    try {
                        jsonArrayResults = response.getJSONArray("results");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mBtnArrivalPlacesSites.setEnabled(true);
                    break;

                case "ZERO_RESULTS": // FAILED
                    msg = context.getString(R.string.zero_results_msg);
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    mBtnArrivalPlacesSites.setEnabled(false);
                    break;

                case "OVER_QUERY_LIMIT": // FAILED
                    msg = context.getString(R.string.over_query_limit_msg);
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    mBtnArrivalPlacesSites.setEnabled(false);
                    break;
                case "REQUEST_DENIED": // FAILED
                    msg = response.getString("error_message");
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    mBtnArrivalPlacesSites.setEnabled(false);
                    break;
                case "INVALID_REQUEST": // FAILED
                    msg = context.getString(R.string.invalid_request_msg);
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    mBtnArrivalPlacesSites.setEnabled(false);
                    break;
                case "UNKNOWN_ERROR": // FAILED
                    msg = context.getString(R.string.unknown_error_msg);
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    mBtnArrivalPlacesSites.setEnabled(false);
                    break;
                default:
                    Log.e(TAG, "Unexpected value: " + status);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void processingError(Trip trip) {
        Toast.makeText(context,
                context.getString(R.string.error_message),
                Toast.LENGTH_LONG).show();
        mTextViewArrivalPlace.setText(String.format("%s (%s) \n(%s)",
                trip.getArrivalPlace(), "N/A", trip.getCountry()));
    }

    private void getTemp(double lat, double lon, Trip trip) {
        String lat_lon = "lat=" + lat + "&lon=" + lon;
        String mURL = String.format("%s%s%s",
                this.context.getString(R.string.base_url), lat_lon,
                this.context.getString(R.string.query_api_id_api_key));

        Response.Listener<JSONObject> onSuccess = (JSONObject response) -> {
            try {
                //  processing the Response Json
                processingResponse(response, trip);
//                Log.d(TAG, "Success JSON code: " + response.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Response.ErrorListener onError = (VolleyError error) -> {
            Log.d(TAG, "Error Volley : " + error.getMessage());
            processingError(trip);
        };

        JsonObjectRequest requestQueue =
                new JsonObjectRequest(Request.Method.GET, mURL, null, onSuccess, onError);

        VolleySingleton.getInstance(this.context).addToRequestQueue(requestQueue);
    }

    private void requestPlaces(double lat, double lon, String radius, String type) {
        StringBuilder mURL = new StringBuilder(context.getString(R.string.places_api_url));
        mURL.append("?location=").append(lat).append(",").append(lon)
                .append("&radius=").append(radius)
                .append("&type=").append(type)
                .append("&key=").append(context.getString(R.string.google_api_key));

        Log.d(TAG, "requestPlaces() returned: " + mURL);
        Response.Listener<JSONObject> onSuccess = (JSONObject response) -> {
            try {
                //  processing the Response Json
//                Log.d(TAG, "Success JSON code: " + response.toString());
                processResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Response.ErrorListener onError = (VolleyError error) -> {
            Log.d(TAG, "Error Volley : " + error.getMessage());
            Toast.makeText(context, R.string.error_places_request_msg,
                    Toast.LENGTH_SHORT).show();
        };

        JsonObjectRequest requestQueue =
                new JsonObjectRequest(Request.Method.GET,
                        mURL.toString(), null, onSuccess, onError);

        VolleySingleton.getInstance(this.context).addToRequestQueue(requestQueue);
    }

    private void getDistance(double latitude, double longitude,
                             Trip trip, TripRecyclerViewAdapter tripAdapter) {
        int permissionGranted = PackageManager.PERMISSION_GRANTED;
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != permissionGranted &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != permissionGranted) {
            return;
        }
        this.client.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location locDepPlace = new Location("departurePlace");
                locDepPlace.setLatitude(latitude);
                locDepPlace.setLongitude(longitude);
                if (trip.getDistance() == null) {
                    float distance = task.getResult().distanceTo(locDepPlace);
                    Log.d(TAG, "getDistance: " + distance);
                    String unit = distance > 1000 ? "km" : "mt";
                    distance = distance > 1000 ? distance / 1000 : distance;
                    trip.setDistance(String.format("%s %s", formatter(round(distance)), unit));
                    tripAdapter.updateItem(trip);
                }
            }
        });
    }

    private String formatter(double number) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();

        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", decimalFormatSymbols);

        return decimalFormat.format(number);
    }

    private double round(double d) {
        BigDecimal bigDecimal = new BigDecimal(Double.toString(d));
        bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.doubleValue();
    }

}