package com.ariondan.vendor.network;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ariondan.vendor.model.LoginModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;

/**
 * Created by AoD Akitektuo on 03-Sep-17 at 19:45.
 */

public class NetworkManager {

    private static final String HOST = "http://festpay.azurewebsites.net/api/";
    private static final String HOST_USER = HOST + "user/";

    private Context context;
    private UserResponse notifier;

    public NetworkManager(Activity activity) {
        setContext(activity);
        setNotifier((UserResponse) activity);
    }

    public void logIn(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Fill in all fields.", Toast.LENGTH_SHORT).show();
        } else {
            if (email.contains("@")) {
                RequestQueue queue = Volley.newRequestQueue(getContext());
                String url = HOST_USER + "logIn";
                try {
                    final String json = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(new LoginModel(email, password));
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (Boolean.parseBoolean(response)) {
                                getNotifier().logIn();
                            } else {
                                Toast.makeText(getContext(), "Wrong credentials.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            try {
                                return json == null ? null : json.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", json, "utf-8");
                                return null;
                            }
                        }
                    };
                    queue.add(stringRequest);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Invalid e-mail.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void passwordForgotten(String email) {
        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Field was empty.", Toast.LENGTH_SHORT).show();
        } else {
            if (email.contains("@")) {
                RequestQueue queue = Volley.newRequestQueue(getContext());
                String url = HOST_USER + "passwordForgotten?email=" + email;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (Boolean.parseBoolean(response)) {
                                    getNotifier().passwordForgotten();
                                } else {
                                    Toast.makeText(getContext(), "Wrong e-mail.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                queue.add(stringRequest);
            } else {
                Toast.makeText(getContext(), "Invalid e-mail.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Context getContext() {
        return context;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    private UserResponse getNotifier() {
        return notifier;
    }

    private void setNotifier(UserResponse notifier) {
        this.notifier = notifier;
    }
}
