package app.backpackandroid;


import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.widget.Toast;

//Network Volley
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by guillaume on 16/03/18.
 */

public class HttpRequest {

    //private String local_url = "http://10.0.2.2:5000/";
    private String ip = "192.168.1.45";
    private String local_url = "http://" + ip + ":5000/";
    private Context context;

    public HttpRequest(Context ctx)
    {
        context = ctx;
    }

    public String Get(final String url, final Map<String, String> headers)
    {
        final String[] test = new String[1];
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("RESP = " + response);
                        test[0] = response;
                }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println("ERROR ON GET REQUEST URL = " + url);
        }
    })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        queue.add(stringRequest);
        return test[0];
    }

    public void Post(String url, final Map<String,String> data, final Map<String,String> header)
    {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //mPostCommentResponse.requestCompleted();
                System.out.println("YES OK POST");
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("NOOOOOOOOO POST FAIL");
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                return data;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return header;
            }
        };
        queue.add(stringRequest);
    }

    public void PostUser(String username, String password)
    {
        Map<String,String> user = new HashMap<String, String>();
        user.put("username", username);
        user.put("password", password);

        Map<String,String> header = new HashMap<String, String>();
        header.put("Content-Type","application/x-www-form-urlencoded");

        Post(local_url + "users", user, header);
    }

    public void GetToken(final String username, final String password)
    {
        String url = local_url + "token";

        Map<String, String> headers = new HashMap<String, String>();
        String credentials = username + ":" + password;
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", auth);

       Get(url, headers);
    }

    public void GetUsers()
    {
        String url = local_url + "users";

        Map<String, String> headers = new HashMap<String, String>();
        Get(url, headers);
    }

    public void GetPois(String token)
    {
        String url = local_url + "pois";

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + token);
        Get(url, headers);
    }

    public void PostPois(String name, String desc, double latitude, double longitude, String token)
    {
        Map<String,String> point = new HashMap<String, String>();
        point.put("name", name);
        point.put("description", desc);
        point.put("lat", Double.toString(latitude));
        point.put("long", Double.toString(longitude));


        Map<String, String> header = new HashMap<String, String>();
        header.put("Authorization", "Bearer " + token);

        Post(local_url + "pois", point, header);
    }
}
