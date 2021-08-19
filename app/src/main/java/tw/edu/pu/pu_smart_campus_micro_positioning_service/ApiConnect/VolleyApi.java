package tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect;

import android.app.Activity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolleyApi {

    private final Activity activity;
    private final String API_URL;

    public VolleyApi(Activity activity, String API_URL) {
        this.activity = activity;
        this.API_URL = API_URL;
    }

    public void get_API_CheckActivity() {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        Map<String, String> params = new HashMap<String, String>();
//                        params.put("Authorization", "Bearer "+ yourToken);
//                        return params;
//                    }

                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(stringRequest);
    }

    public void post_API_LoginActivity(String id, String pass, String imei) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        Map<String, String> postParam= new HashMap<>();
        postParam.put("id", id);
        postParam.put("pass", pass);
        postParam.put("IMEI", imei);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, API_URL, new JSONObject(postParam), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public void post_API_TESTING(MaterialTextView tvText)  {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                tvText.setText("Post Response: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvText.setText("Post Response: Failed!");
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("a", "1");
                params.put("b", "2");
                params.put("c", "3");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void post_API_Login(String id, String pass, String imei, final VolleyCallback callback)  {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(activity, response, Toast.LENGTH_SHORT).show();
                callback.onSuccess(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("pass", pass);
                params.put("IMEI", imei);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void get_API_TESTING(MaterialTextView tvText)  {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                tvText.setText("GET Response: " + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvText.setText("GET Response: Failed!");
            }
        });

        requestQueue.add(stringRequest);
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }
}
