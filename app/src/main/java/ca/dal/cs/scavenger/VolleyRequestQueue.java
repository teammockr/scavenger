//created by odavison
package ca.dal.cs.scavenger;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


// Singleton maintaining the Volley RequestQueue for the app
class VolleyRequestQueue {
    private static RequestQueue mRequestQueue;

    // Disallow instantiation from outside
    private VolleyRequestQueue() {};

    // Ensure mRequestQueue is instantiated on the Application context
    static void initialize(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    // Add a request to the singleton queue
    public static <T> void add(Request<T> req) {
        mRequestQueue.add(req);
    }

    // Cancel all requests in the queue that are tagged with 'tag'
    public static void cancelAll(@NonNull Object tag) {
        mRequestQueue.cancelAll(tag);
    }
}
