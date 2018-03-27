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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import android.view.View.OnClickListener;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * This is a small wear app which can unlock the doors of Mercedes-Benz Car
 * by using Mercedes-Benz's experimenting Car API.
 *
 * We skip the initial authenntication steps here, so there are some hard code of tokens
 * which are used to communicate with Mercedes-Benz API.
 *
 * @author Irene Chung
 */
public class MainActivity extends WearableActivity {
    private TextView mTextView;
    private ImageButton unlockBtn;
    private ImageButton lockBtn;
    private static String accessToken =  "c0e03842-197d-4e78-a622-844bd153b144";
    private static String refreshToken = "e0341116-2c2b-46f6-8139-257cd951112d";
    private static final String VID = "A882F4C07FAF66C650";
    private static final String BASE64_ID = "MGYwYTlhMzctZmYyZS00Zjg0LTkwNjctNzIwMjgzMTJmNjk3OjkzNDE5YzBiLTQ5NmEtNGNkYi1hNDRkLTU2ZGE5NGIzOWEyMg==";
    private static final String DEBUGTAG = "DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        unlockBtn = (ImageButton) findViewById(R.id.imageButtonUnlock);
        lockBtn = (ImageButton) findViewById(R.id.imageButtonLock);
        unlockBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Unlocking car doors", Toast.LENGTH_SHORT).show();
                unlockDoor();
            }
        });
        lockBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Locking car doors", Toast.LENGTH_SHORT).show();
                lockDoor();
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

    /**
     * Send a post request to API to lock the doors.
     */
    private void lockDoor() {
        String urlDoor = "https://api.mercedes-benz.com/experimental/connectedvehicle/v1/vehicles/" +
                         VID + "/doors";
        sendDoorRequest(urlDoor, "LOCK");
    }
    /**
     * Send a post request to API to unlock the doors.
     */
    private void unlockDoor() {
        String urlDoor = "https://api.mercedes-benz.com/experimental/connectedvehicle/v1/vehicles/" +
                          VID + "/doors";
        sendDoorRequest(urlDoor, "UNLOCK");
    }
    /**
     * Parse the refresh token returned from OAuth server.
     * And modify the access token by the returned token.
     * @param response a json format response returned from server
     */
    private void parseNewToken(JSONObject response) {
        try {
            String newAccess = response.getString("access_token");
            String newRefresh = response.getString("refresh_token");
            this.accessToken = newAccess;
            this.refreshToken = newRefresh;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refresh the access token by sending a post request to the auth server
     * with current token.
     */
    private void refreshToken() {
        String url = "https://api.secure.mercedes-benz.com/oidc10/auth/oauth/v2/token";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    parseNewToken(jsonObject);
                    Toast.makeText(MainActivity.this, "Token refreshed!", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(DEBUGTAG, error.toString());
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
        /* add to request queue. */
        HttpHandle.getInstance(this).getRequestQueue().add(strReq);
    }

    /**
     * Send an post http request to server according to the url and command.
     * @param url url for door API
     * @param command lock or unlock
     */
    private void sendDoorRequest(String url, String command) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("command", command);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonBody.toString();

        JsonObjectRequest requestJson = new JsonObjectRequest(Request.Method.POST, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof AuthFailureError) {
                    refreshToken();
                } else if (error instanceof ServerError) {
                    Toast.makeText(MainActivity.this, "Server error!", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
            @Override
            public byte[] getBody() {
                return requestBody.getBytes();
            }
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        HttpHandle.getInstance(this).getRequestQueue().add(requestJson);
    }
}
