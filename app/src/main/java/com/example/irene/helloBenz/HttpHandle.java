package com.example.irene.helloBenz;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;

/**
 * Created by Irene on 2018/3/24.
 */

public class HttpHandle {
    private static HttpHandle handleInstance;
    private RequestQueue mRequestQueue;
    private Context mContext;

    public HttpHandle(Context context) {
        this.mContext = context;
        //initializeQueue(context);
    }

    private void initializeQueue(Context context) {
        if (mRequestQueue ==  null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
    }
    public static synchronized HttpHandle getInstance(Context context) {
        Log.d("Test", "getInstance");
        if (handleInstance == null) {
            handleInstance = new HttpHandle(context);
        }
        return handleInstance;
    }
    public RequestQueue getRequestQueue() {
        Log.d("Test", "getRequestQueue");
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }
}
