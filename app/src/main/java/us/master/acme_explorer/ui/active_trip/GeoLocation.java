package us.master.acme_explorer.ui.active_trip;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.android.volley.VolleyLog.TAG;


class GeoLocation {
    void getLocation(String location, Context context, Handler handler) {
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
                            if (addressList != null && addressList.size() > 0) {
                                Address address = addressList.get(0);
                                StringBuilder builder = new StringBuilder();
                                builder.append(address.getLatitude()).append("\n");
                                builder.append(address.getLongitude()).append("\n");
                                Log.i(TAG, "run: -> " + addressList.toString());
                                latitude = address.getLatitude();
                                longitude = address.getLongitude();
                                result = builder.substring(0);
                            }
                            i++;
                        } catch (Exception e) {
                            e.printStackTrace();
                            addressList = geocoder.getFromLocationName(location, 1);
                            if (addressList != null && addressList.size() > 0) {
                                Address address = addressList.get(0);
                                StringBuilder builder = new StringBuilder();
                                builder.append(address.getLatitude()).append("\n");
                                builder.append(address.getLongitude()).append("\n");
                                Log.i(TAG, "run: -> " + addressList.toString());
                                latitude = address.getLatitude();
                                longitude = address.getLongitude();
                                result = builder.substring(0);
                            }
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
                }
            }
        };
        thread.start();
    }
}