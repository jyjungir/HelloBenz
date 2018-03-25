package com.example.irene.helloBenz;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import android.view.View.OnClickListener;
import com.android.volley.Request;
import com.android.volley.Response;

public class MainActivity extends WearableActivity {
    //public static final int LEFT_FRONT_DOOR = 1;
    private TextView mTextView;
    private ImageButton unlockBtn;
    private String accessToken =  "4b30859a-5e31-4dd4-9b5c-db4bf35b6169";
    private String refreshToken = "64c692c4-865f-4e1b-b764-0d92e9ed7506";
    private static final String VID = "A882F4C07FAF66C650";
    private static final String BASE64_ID = "MGYwYTlhMzctZmYyZS00Zjg0LTkwNjctNzIwMjgzMTJmNjk3OjkzNDE5YzBiLTQ5NmEtNGNkYi1hNDRkLTU2ZGE5NGIzOWEyMg==";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        unlockBtn = (ImageButton) findViewById(R.id.imageButton2);
        unlockBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Unlocking car doors", Toast.LENGTH_SHORT).show();
                unlockDoor();
            }
        });
        // Enables Always-on
        setAmbientEnabled();
    }

    private void unlockDoor() {
        String urlDoor = "https://api.mercedes-benz.com/experimental/connectedvehicle/v1/vehicles/" +
                          VID + "/doors";
        sendPostRequest(urlDoor);
    }

    private void parseNewToken(String response) {
        Log.d("Test", response);
    }

    private void refreshToken() {
        // todo
        String url = "https://api.secure.mercedes-benz.com/oidc10/auth/oauth/v2/token";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Test", response.toString());
                Toast.makeText(MainActivity.this, "Token refreshed!", Toast.LENGTH_SHORT).show();
                parseNewToken(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Test", error.toString());
                Toast.makeText(MainActivity.this, "token Error!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Basic " + BASE64_ID);
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    String mRequestBody = "grant_type=refresh_token&refresh_token=" + refreshToken;
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };
        HttpHandle.getInstance(this).getRequestQueue().add(strReq);
    }

    private void sendPostRequest(String url) {
        // create string request
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Test", response.toString());
                Toast.makeText(MainActivity.this, "Door unlocked!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Test", error.toString());
                if (error instanceof ServerError) {
                    Toast.makeText(MainActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    refreshToken();
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        // add to request queue
        HttpHandle.getInstance(this).getRequestQueue().add(strReq);
    }
}
