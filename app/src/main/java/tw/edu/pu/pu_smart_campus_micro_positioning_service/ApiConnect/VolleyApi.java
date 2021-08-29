package tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect;

import android.app.Activity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.CheckModel;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.ShareData;

public class VolleyApi {

    private final String TAG = "volley: ";

    private final Activity activity;
    private final String API_URL;

    public VolleyApi(Activity activity, String API_URL) {
        this.activity = activity;
        this.API_URL = API_URL;
    }

    /**
     ***************** GET METHOD ********************
     */
    //final @NonNull String apiToken
    public void get_API_CheckActivity() {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ShareData shareData = new ShareData(activity);

                ArrayList<CheckModel> checkModels = new ArrayList<>();

                try {
                    JSONArray allData = new JSONArray(response);

                    for (int i = 0; i < allData.length(); i++) {
                        JSONObject data = allData.getJSONObject(i);
                        checkModels.add(i, new CheckModel(data.getString("ViewName"), String.valueOf(data.getInt("Peoplenumber"))));
                    }

                    shareData.saveData(checkModels);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer + token");
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void get_API_GuideActivity(VolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailed(error);
            }
        });

        requestQueue.add(stringRequest);
    }

    /**
     ***************** POST METHOD ********************
     */

    public void post_API_Login(String id, String pass, final VolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailed(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sidimei", id);
                params.put("password", pass);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void post_API_Login_Guest(String imei, final VolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailed(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sidimei", imei);
                params.put("role", String.valueOf(0));
                params.put("name", "tourist");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void post_API_Safety(final VolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailed(error);
            }
        });

        requestQueue.add(stringRequest);
    }

    /**
     ***************** Override METHOD ********************
     */

    public interface VolleyCallback {
        void onSuccess(String result);
        void onFailed(VolleyError error);
    }
}
