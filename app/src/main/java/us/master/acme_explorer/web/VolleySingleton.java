package us.master.acme_explorer.web;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    // fields
    private static VolleySingleton singleton;
    private static Context context;
    private RequestQueue requestQueue;


    private VolleySingleton(Context context) {
        VolleySingleton.context = context.getApplicationContext();
        requestQueue = getRequestQueue();
    }

    /**
     * return the single instance of singleton
     *
     * @param context context where the requests will be executed
     * @return Instance
     */
    public static synchronized VolleySingleton getInstance(Context context) {
        if (singleton == null) {
            singleton = new VolleySingleton(context.getApplicationContext());
        }
        return singleton;
    }

    /**
     * get the instance of the request queue
     *
     * @return request queue
     */
    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Add the request to the queue
     *
     * @param req petición
     * @param <T> Resultado final de tipo T
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
