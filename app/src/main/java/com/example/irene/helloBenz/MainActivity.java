/**
 * This is a small wear app which can unlock the doors of Mercedes-Benz Car
 * by using Mercedes-Benz's experimenting Car API.
 *
 * @author Irene Chung
 */
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

public class MainActivity extends WearableActivity {
    private TextView mTextView;
    private ImageButton unlockBtn;
    private String accessToken =  "ca0f9669-296a-42ae-b6af-1a8be9a49184";
    private String refreshToken = "aa0ae3bc-481c-4c08-86ee-268535440f74";
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

    /**
     * Send a post request to API to unlock the doors.
     */
    private void unlockDoor() {
        String urlDoor = "https://api.mercedes-benz.com/experimental/connectedvehicle/v1/vehicles/" +
                          VID + "/doors";
        sendUnlockPostRequest(urlDoor);
    }
    /**
     * Parse the refresh token returned from OAuth server.
     * And modify the access token by the returned token.
     * @param response a http response returned from server
     */
    private void parseNewToken(String response) {

        Log.d("Test", response);
    }

    /**
     * Refresh the access token by sending a post request to the auth server
     * with current token.
     */
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
        /* add to request queue. */
        HttpHandle.getInstance(this).getRequestQueue().add(strReq);
    }
    /**
     * Send an post http request to server according to the url.
     * @param url the url for the http request
     */
    private void sendUnlockPostRequest(String url){
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("command", "UNLOCK");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonBody.toString();

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(MainActivity.this, "unlocked!", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                
                Toast.makeText(MainActivity.this, "unlock error!", Toast.LENGTH_SHORT).show();
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
        HttpHandle.getInstance(this).getRequestQueue().add(request_json);
    }
}
