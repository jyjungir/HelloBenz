/**
 * This class is for handing the HTTP requests using Volley library.
 * This class will instantiate an instance of a request queue, and users of this class
 * can use this queue to send HTTP requests by adding requests into the queue.
 * @author Irene Chung
 */
package com.example.irene.helloBenz;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;

public class HttpHandle {
    private static HttpHandle handleInstance;
    private RequestQueue mRequestQueue;
    private Context mContext;

    public HttpHandle(Context context) {
        this.mContext = context;
    }

    public static synchronized HttpHandle getInstance(Context context) {
        if (handleInstance == null) {
            handleInstance = new HttpHandle(context);
        }
        return handleInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }
}
