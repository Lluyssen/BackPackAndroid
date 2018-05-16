package app.backpackandroid;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

//Network Volley
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/**
 * Created by guillaume on 16/03/18.
 */

public class HttpRequest {

    //private String local_url = "http://10.0.2.2:5000/";
    private String ip = "192.168.1.45";
    private String local_url = "http://" + ip + ":5000/";
    //private String local_url = "https://backpack-api-epitech.herokuapp.com/";
    private Context context;
    private String token = new String();
    private String request_response = new String();
    GoogleMap mMap;
    HashMap<String,Point> markerList;

    public HttpRequest(Context ctx, GoogleMap Map, HashMap<String,Point> markerList2)
    {
        context = ctx;
        mMap = Map;
        markerList = markerList2;
    }

    public HttpRequest(Context ctx)
    {
        context = ctx;
    }

    public String getToken()
    {
        return token;
    }

    public void Get(final String url, final Map<String, String> headers, Response.Listener<String> listener)
    {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, listener, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            //System.out.println("ERROR ON GET REQUEST URL = " + url);
            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
        }
    })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        queue.add(stringRequest);
    }

    public String GetResponse()
    {
        return request_response;
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

    public void login(final String username, final String password)
    {
        //FAIRE REQUETTE SYNCHRONOUS https://stackoverflow.com/questions/16904741/can-i-do-a-synchronous-request-with-volley
        //OU APPELER UN FONCTION POUR RECUPE LE TOKEN DANS ONRESPONSE
        String url = local_url + "token";

        Map<String, String> headers = new HashMap<String, String>();
        String credentials = username + ":" + password;
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", auth);

        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("RESP = " + response);
                token = response;
            }
        };

        Get(url, headers, listener);
    }

    public void GetToken(final String username, final String password)
    {
        //FAIRE REQUETTE SYNCHRONOUS https://stackoverflow.com/questions/16904741/can-i-do-a-synchronous-request-with-volley
        //OU APPELER UN FONCTION POUR RECUPE LE TOKEN DANS ONRESPONSE
        String url = local_url + "token";

        Map<String, String> headers = new HashMap<String, String>();
        String credentials = username + ":" + password;
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", auth);

        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("RESP = " + response);
                token = response;
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(token);
                    String result = jObject.getString("token");
                    Intent intent = new Intent(context, MapsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("token", result);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "FAILED to get token, check username or password", Toast.LENGTH_SHORT).show();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "FAILED to get token, check username or password", Toast.LENGTH_SHORT).show();
            }
        };

       Get(url, headers, listener);
    }

    public void GetUsers()
    {
        String url = local_url + "users";

        Map<String, String> headers = new HashMap<String, String>();
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("USERS = " + response);
                request_response = response;

                JSONArray jsonarray = null;
                try {
                    jsonarray = new JSONArray(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = jsonarray.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        String name = jsonobject.getString("username");
                        System.out.println("LOOLOLO = " + name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        String url = jsonobject.getString("url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Get(url, headers, listener);
    }

    public void parsePOIS(String response)
    {
        JSONArray jsonarray = null;

        try {
            jsonarray = new JSONArray(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = null;
            try {
                jsonobject = jsonarray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                String name = jsonobject.getString("name");
                String lats = jsonobject.getString("lat");
                String longs = jsonobject.getString("long");
                String createur = jsonobject.getString("userName");
                String type = jsonobject.getString("type");

                double longitude = Double.parseDouble(longs);
                double lat = Double.parseDouble(lats);
                //System.out.println("ID POINT = " + jsonobject.getString("id"));
                LatLng pos = new LatLng(lat,longitude);
                MarkerOptions marker = new MarkerOptions().position(pos).title(name).snippet("Created by: " + createur + "\nType: " + type);

                if (type.contains("vue"))
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                else if (type.contains("eau"))
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                else if (type.contains("campement"))
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                else if (type.contains("Autre"))
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                //
                mMap.addMarker(marker);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void GetPois(String token)
    {
        String url = local_url + "pois";

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + token);

        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("RESP = " + response);
                request_response = response;

                parsePOIS(response);
                //display all points
            }
        };

        Get(url, headers, listener);
    }

    public String getStringImage(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void PostPois(Point newPoint, String name, String type, String token)
    {
        Map<String,String> point = new HashMap<String, String>();
        if (newPoint.photoList != null && !newPoint.photoList.isEmpty())
        {
            point.put("images", getStringImage(newPoint.photoList.get(0)));
        }
        else
            System.out.println("PHOTOS LISTE IS NULL FOR THIS POINT");
        point.put("name", name);
        point.put("description", newPoint.marker.getSnippet());
        point.put("lat", Double.toString(newPoint.marker.getPosition().latitude));
        point.put("long", Double.toString(newPoint.marker.getPosition().longitude));
        point.put("type", type);

        System.out.println("POST Lat = " + newPoint.marker.getPosition().latitude);
        System.out.println("POST Long = " + newPoint.marker.getPosition().longitude);

        Map<String, String> header = new HashMap<String, String>();
        header.put("Authorization", "Bearer " + token);

        JSONObject jsonObject = new JSONObject(point);
        byte[] body = jsonObject.toString().getBytes();

        //Fuel.post(local_url + "pois").body(body).header(header);
        Post(local_url + "pois", point, header);
    }
}

