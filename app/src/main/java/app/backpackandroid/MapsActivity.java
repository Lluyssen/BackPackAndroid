package app.backpackandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Point
{
    Marker   marker;
    List<Bitmap>    photoList;
    String          id;

    public Point(Marker mkr, List<Bitmap> photos)
    {
        id = mkr.getId(); //Remplacer par id
        this.marker = mkr;
        photoList = photos;
    }

    public void add(Marker mkr)
    {
        this.marker = mkr;
    }
}

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap               mMap;
    private HashMap<String,Point>   markerList;
    private List<Bitmap>    photoListTmp;
    private static int SELECTED_PICTURE = 1;
    private HttpRequest httpRequest;
    private Dialog dialog;
    private View dialogView;
    String token = "";

    /*TRAJET */
    private static final int LOCATION_REQUEST = 500;
    private ArrayList<LatLng> ListPoints;
    /*TRAJET */

    //----Filtre----
    private boolean water_point_hide = true; // si cache ou non
    private boolean camping_point_hide = true; // si cache ou non
    private boolean view_point_hide = true; // si cache ou non
    //-------------

    LinearLayout layoutPrevPhoto;
    LinearLayout.LayoutParams layoutPrevPhotoParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        dialog = new Dialog(MapsActivity.this);
        markerList = new HashMap<String,Point>();
        photoListTmp = new ArrayList<Bitmap>();
        ListPoints = new ArrayList<LatLng>();

        Bundle b = this.getIntent().getExtras();
        String value = ""; // or other values
        if(b != null)
            value = b.getString("token");
        else
            System.out.println("FAIL BUNDLE IS NULL");

        token = value;
        System.out.println("TOKENNNNNNNNNNNNNNNNNNNNNNNNNNNNNN = " + token);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        httpRequest = new HttpRequest(MapsActivity.this, mMap, markerList);

        googleMap.setOnMapLongClickListener(
                new GoogleMap.OnMapLongClickListener() {
                                                @Override
                                                public void onMapLongClick(LatLng point) {

                                                    addPoint(point);

                                                    EditText ed = (EditText) findViewById(R.id.editName);
                                                    //ADD TO LIST
                                                    //CHANGE TO ADD VIEW !!!!!!!!!!!!!!!!!
                                                }
                                            }
        );

       httpRequest.GetPois(token);
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        LatLng tek = new LatLng(48.81552199999999, 2.362973000000011);
        mMap.addMarker(new MarkerOptions().position(tek).title("Tek").snippet("tetetetetetetetetetetet")/*.icon(BitmapDescriptorFactory.fromBitmap(bitmap))*/);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tek));

        Button button = (Button) findViewById(R.id.PositionBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
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
                startActivity(new Intent(MapsActivity.this, TutoPage.class));
            }
        });

        /*TRAJET */


        final Button waterfilter_button = (Button) findViewById(R.id.WaterBtn);
        WaterFilterButton(waterfilter_button);

        final Button viewfilter_button = (Button) findViewById(R.id.ViewBtn);
        ViewFilterButton(viewfilter_button);

        final Button campingfilter_button = (Button) findViewById(R.id.CampingBtn);
        CampingFilterButton(campingfilter_button);

        FilterButtonAppear(waterfilter_button, viewfilter_button, campingfilter_button);
    }

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
        //ImageView addImage = view.findViewById(R.id.DisplayImage);
        Drawable new_image = getResources().getDrawable(R.drawable.plage);
        //addImage.setBackgroundDrawable(new_image);
        TextView addText = view.findViewById(R.id.Textinfo);
        addText.setText(marker.getTitle() + "\n" + marker.getSnippet());

        layoutPrevPhoto = (LinearLayout) view.findViewById(R.id.picturePrevOfPoint);
        layoutPrevPhotoParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        System.out.println("MARKER ID = " + marker.getId());

        //ON PEUX VOIR QUE LES PHOTOS DES POINTS QU'ON VIENT D'AJOUTER
        if (!markerList.isEmpty())
        {
            Point point = markerList.get(marker.getId());
            if (point != null && !point.photoList.isEmpty())
            {
                for (int i = 0; i != point.photoList.size(); i++) {
                    ImageView imageView = new ImageView(MapsActivity.this);

                    imageView.setImageBitmap(point.photoList.get(i));
                    imageView.setLayoutParams(layoutPrevPhotoParams);

                    layoutPrevPhoto.addView(imageView);
                }
            }
            else
                System.out.println("NO PHOTO FOR THIS POINT");
        }

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

    public void addPoint(LatLng point)
    {
        View view = getLayoutInflater().inflate(R.layout.add_point, null);
        Button addButton = (Button) view.findViewById(R.id.addBtn);
        ImageButton addPhoto = (ImageButton) view.findViewById(R.id.addPhoto);
        final EditText editTextName = (EditText) view.findViewById(R.id.editName);
        photoListTmp.clear();
        //preview photo
        layoutPrevPhoto = (LinearLayout) view.findViewById(R.id.picturePrev);
        layoutPrevPhotoParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //liste deroulante type
        final Spinner spinner = (Spinner) view.findViewById(R.id.typeSpinner);
        //Création d'une liste d'élément à mettre dans le Spinner(pour l'exemple)
        List typeList = new ArrayList();
        typeList.add("Point de vue");
        typeList.add("Point d'eau");
        typeList.add("Point de campement");
        typeList.add("Autre");

        ArrayAdapter adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                typeList
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //-------------------------------

        dialogView = view;
        dialog.setContentView(view);
        dialog.setTitle("Add point");
        dialog.setCancelable(true);
        dialog.show();

        final MarkerOptions newMarker = new MarkerOptions().position(point);
       addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextName.getText().toString().isEmpty()) {
                    String type = spinner.getSelectedItem().toString();
                    if (type.contains("vue"))
                        newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    else if (type.contains("eau"))
                        newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    else if (type.contains("campement"))
                        newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    else if (type.contains("Autre"))
                        newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                    newMarker.title(editTextName.getText().toString()).snippet("Type: " + spinner.getSelectedItem().toString());
                    Toast.makeText(MapsActivity.this, "New point added !", Toast.LENGTH_SHORT).show();
                    Marker marker = mMap.addMarker(newMarker);
                    Point newPoint = new Point(marker, new ArrayList<Bitmap>(photoListTmp));
                    markerList.put(marker.getId(), newPoint);
                    dialog.dismiss();
                    System.out.println("Lat = " + newMarker.getPosition().latitude);
                    System.out.println("Long = " + newMarker.getPosition().longitude);

                    httpRequest.PostPois(newPoint, editTextName.getText().toString(), spinner.getSelectedItem().toString(), token);
                }
            }
        });

       addPhoto.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i = new Intent(
                       Intent.ACTION_PICK,
                       android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

               startActivityForResult(i, SELECTED_PICTURE);
           }
       });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECTED_PICTURE && resultCode == RESULT_OK)
        {
            //setContentView(R.layout.add_point);

            //View view = dialog.getCurrentFocus();
            //View view = LayoutInflater.from(dialog.getContext()).inflate(R.layout.add_point, null);
            //dialog.setContentView(view);
            //View view = getLayoutInflater().inflate(R.layout.add_point, null);

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            //------------------------------
            layoutPrevPhotoParams.gravity = Gravity.CENTER;
            ImageView imageView = new ImageView(MapsActivity.this);

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(bitmap);
            imageView.setLayoutParams(layoutPrevPhotoParams);

            photoListTmp.add(bitmap);

            layoutPrevPhoto.addView(imageView);
            //-----------------------------

            //ImageView imageView = (ImageView) findViewById(R.id.imageUploadPrev);

            //ImageView imageView = (ImageView) view.findViewById(R.id.imageUploadPrev);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            //Redefine button of dialog
            /*ImageButton addPhoto = (ImageButton) dialogView.findViewById(R.id.addPhoto);
            addPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, SELECTED_PICTURE);
                }
            });*/
            //-------------------------------------------
            dialog.show();
        }
    }

    public Bitmap getImage(String img) {
        try {
            URL url = new URL(img);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            System.out.println("CA MARCHE PAS !!");
            e.printStackTrace();
            return null;
        }
    }

    void WaterFilterButton(Button waterfilter_button)
    {
        waterfilter_button.setVisibility(View.INVISIBLE);
        waterfilter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  // cache tous les points d'interet de type 'water'
            {
                for (int i = 0; i != markerList.size(); ++i) {
                    if (markerList.get(i).marker.getSnippet().contains("eau") && water_point_hide == true)
                        markerList.get(i).marker.setVisible(false);
                    else if (markerList.get(i).marker.getSnippet().contains("eau") && water_point_hide == false)
                        markerList.get(i).marker.setVisible(true);
                }
                water_point_hide = (water_point_hide == true) ? false : true;
            }
        });
    }

    void ViewFilterButton(Button viewfilter_button)
    {
        viewfilter_button.setVisibility(View.INVISIBLE);
        viewfilter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) // cache tous les points d'interet de type 'view'
            {
                for (int i = 0; i != markerList.size(); ++i) {
                    if (markerList.get(i).marker.getSnippet().contains("vue") && view_point_hide == true)
                        markerList.get(i).marker.setVisible(false);
                    else if (markerList.get(i).marker.getSnippet().contains("vue") && view_point_hide == false)
                        markerList.get(i).marker.setVisible(true);
                }
                view_point_hide = (view_point_hide == true) ? false : true;
            }
        });
    }

    void CampingFilterButton(Button campingfilter_button)
    {
        campingfilter_button.setVisibility(View.INVISIBLE);
        campingfilter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) // cache tous les points d'interet de type 'camping'
            {
                for (int i = 0; i != markerList.size(); i++) {
                    if (markerList.get(i).marker.getSnippet().contains("campement") && camping_point_hide == true)
                        markerList.get(i).marker.setVisible(false);
                    else if (markerList.get(i).marker.getSnippet().contains("campement") && camping_point_hide == false)
                        markerList.get(i).marker.setVisible(true);
                }
                camping_point_hide = (camping_point_hide == true) ? false : true;
            }
        });
    }

    public void FilterButtonAppear(final Button waterfilter_button, final Button viewfilter_button, final Button campingfilter_button)
    {
        Button filter_button = (Button) findViewById(R.id.FilterBtn);
        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) // animation apparition des boutons de filtre
            {
                if (campingfilter_button.getVisibility() == View.VISIBLE) {
                    campingfilter_button.setVisibility(View.INVISIBLE);
                    waterfilter_button.setVisibility(View.INVISIBLE);
                    viewfilter_button.setVisibility(View.INVISIBLE);
                } else {
                    campingfilter_button.setVisibility(View.VISIBLE);
                    waterfilter_button.setVisibility(View.VISIBLE);
                    viewfilter_button.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
