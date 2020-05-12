package us.master.acme_explorer.common;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import us.master.acme_explorer.R;
import us.master.acme_explorer.entity.Trip;
import us.master.acme_explorer.web.VolleySingleton;

import static com.android.volley.VolleyLog.TAG;


public class GeoLocation {
    private Context context;
    private TextView mTextViewArrivalPlace;

    public void getLocation(String location, String from,
                            Context context, Handler handler, Trip trip, TextView txtVw) {
        this.context = context;
        this.mTextViewArrivalPlace = txtVw;
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                double latitude = 0D;
                double longitude = 0D;
                try {
                    List<Address> addressList;
                    int i = 0;
                    do {
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);
                            i++;
                        } catch (Exception e) {
                            e.printStackTrace();
                            addressList = geocoder.getFromLocationName(location, 1);
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
                    if (TextUtils.equals(from, "destiny")) {
                        Log.i(TAG, "run: " + result);
                        getTemp(latitude, longitude, trip);
                    } else {
                        getDistance();
                    }
                }
            }

        };
        thread.start();
    }

    private void processingError(Trip trip) {
        Toast.makeText(context,
                context.getString(R.string.error_message),
                Toast.LENGTH_LONG).show();
        mTextViewArrivalPlace.setText(String.format("%s (%s) \n(%s)",
                trip.getArrivalPlace(), "N/A", trip.getCountry()));
    }

    private void processingResponse(JSONObject response, Trip trip) {
        try {
            JSONArray weather = new JSONArray(response.getString("weather"));
            JSONObject climate = new JSONObject(weather.get(0).toString());
            JSONObject temp = new JSONObject(response.getString(context.getString(R.string.main)));
            this.mTextViewArrivalPlace.setText(String.format("%s (%s) \n(%s)",
                    trip.getArrivalPlace(), temp.getString("temp").concat("°, ")
                            .concat(climate.getString("description")),
                    trip.getCountry()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                Log.d(TAG, "Success JSON code: " + response.toString());
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

    private void getDistance() {

    }
}