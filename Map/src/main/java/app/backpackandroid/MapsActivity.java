package app.backpackandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    /*TRAJET */
    private static final int LOCATION_REQUEST = 500;
    private ArrayList<LatLng> ListPoints;
    /*TRAJET */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ListPoints = new ArrayList<>();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(50, 5);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        LatLng tek = new LatLng(48.81552199999999, 2.362973000000011);
        mMap.addMarker(new MarkerOptions().position(tek).title("Tek").snippet("tetetetetetetetetetetet"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tek));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                InfoDialog(marker);
            }
        });

        /*TRAJET */

        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                if (ListPoints.size() == 2)
                    ListPoints.clear();
                ListPoints.add(marker.getPosition());
                if (ListPoints.size() == 1)
                    Toast.makeText(getApplicationContext(), "Your travel Start at " + marker.getTitle(), Toast.LENGTH_SHORT).show();
                if (ListPoints.size() == 2) {
                    Toast.makeText(getApplicationContext(), "Your travel Ended at " + marker.getTitle(), Toast.LENGTH_SHORT).show();
                    String url = getRequestUrl(ListPoints.get(0), ListPoints.get(1));
                    TaskRequestDirection taskRequestDirection = new TaskRequestDirection();
                    taskRequestDirection.execute(url);
                }
            }
        });
        /*TRAJET */

        Button button = (Button) findViewById(R.id.PositionBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPStracker g = new GPStracker(getApplicationContext());
                Location l = g.getLocation();
                if (l != null) {
                    double lat = l.getAltitude();
                    double lon = l.getLongitude();
                    Toast.makeText(getApplicationContext(), "LAT : " + lat + " \n LON : " + lon, Toast.LENGTH_LONG).show();
                }
            }
        });

        Button button1 = findViewById(R.id.TutoBtn);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "TUTO PAGE", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MapsActivity.this, TutoPage.class));
            }
        });
    }

    /*TRAJET */

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null)
                stringBuffer.append(line);
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                inputStream.close();
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    private String getRequestUrl(LatLng origin, LatLng dest) {
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        //String mode = "mode=walking";
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    mMap.setMyLocationEnabled(true);
                break;
        }
    }

    public class TaskRequestDirection extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
                    https://maps.googleapis.com/maps/api/directions/
                    points.add(new LatLng(lat, lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(8);
                polylineOptions.color(Color.GREEN);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null)
                mMap.addPolyline(polylineOptions);
            else
                Toast.makeText(getApplicationContext(), "Direction not set", Toast.LENGTH_SHORT).show();
        }
    }

    /*TRAJET */

    public void InfoDialog(Marker marker) {

        final Dialog dialog = new Dialog(MapsActivity.this);
        dialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        View view = getLayoutInflater().inflate(R.layout.infowindow, null);
        Button addButton = (Button) view.findViewById(R.id.closeBtn);
        ImageView addImage = view.findViewById(R.id.DisplayImage);
        Drawable new_image = getResources().getDrawable(R.drawable.plage);
        addImage.setBackgroundDrawable(new_image);
        TextView addText = view.findViewById(R.id.Textinfo);
        addText.setText("Guillaume ca marche");
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.show();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}