package app.backpackandroid;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class Point
{
    MarkerOptions   marker;

    public Point(MarkerOptions mkr)
    {
        this.marker = mkr;
    }

    public void add(MarkerOptions mkr)
    {
        this.marker = mkr;
    }
}

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap               mMap;
    private List<Point>    markerList;
    private List<String>    photoList;
    private static int SELECTED_PICTURE = 1;
    private HttpRequest httpRequest;
    private Dialog dialog;
    String token = ""; //"eyJhbGciOiJIUzI1NiIsImlhdCI6MTUyMTQ1NTEwMiwiZXhwIjoxMTUyMTQ1NTEwMX0.eyJpZCI6Mn0.qT19ib8C6x1Di-gUKoy6PZJTR1kYX6IOZeYzgVGF19g";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        dialog = new Dialog(MapsActivity.this);
        markerList = new ArrayList<Point>();
        photoList = new ArrayList<String>();

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
        httpRequest = new HttpRequest(MapsActivity.this, mMap);

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

        //httpRequest.PostUser("newUser", "newUser");
        //httpRequest.GetToken("oui", "oui");
        //httpRequest.GetUsers();
        //httpRequest.PostPois("TESTPOI", "desc test", 65.9999999, 45.9, "eyJhbGciOiJIUzI1NiIsImlhdCI6MTUyMTMyNDU0NCwiZXhwIjoxNTIxMzI1MTQ0fQ.eyJpZCI6Mn0.kS_IP6obDLiF6GksjhdDdkM_ge7kKIT0z3pVq4RpF_s");
        httpRequest.GetPois(token);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                InfoDialog(marker);
            }
        });

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
    }

    public void InfoDialog(Marker marker) {

        final Dialog dialog = new Dialog(MapsActivity.this);
        dialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        View view = getLayoutInflater().inflate(R.layout.infowindow, null);
        Button addButton = (Button) view.findViewById(R.id.closeBtn);
        ImageView addImage = view.findViewById(R.id.DisplayImage);
        Drawable new_image= getResources().getDrawable(R.drawable.plage);
        addImage.setBackgroundDrawable(new_image);
        TextView addText = view.findViewById(R.id.Textinfo);
        addText.setText(marker.getTitle() + "\n" + marker.getSnippet());
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

        dialog.setContentView(view);
        dialog.setTitle("Add point");
        dialog.setCancelable(true);
        dialog.show();

        final MarkerOptions newMarker = new MarkerOptions().position(point);
       addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextName.getText().toString().isEmpty()) {
                    newMarker.title(editTextName.getText().toString());
                    Toast.makeText(MapsActivity.this, "New point added !", Toast.LENGTH_SHORT).show();
                    mMap.addMarker(newMarker);
                    markerList.add(new Point(newMarker));
                    dialog.dismiss();
                    System.out.println("Lat = " + newMarker.getPosition().latitude);
                    System.out.println("Long = " + newMarker.getPosition().longitude);

                    httpRequest.PostPois(editTextName.getText().toString(), "no desc", newMarker.getPosition().latitude, newMarker.getPosition().longitude, token);
                    //HttpRequest httpRequest = new HttpRequest(MapsActivity.this);
                    //httpRequest.GetToken("oui", "oui");
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

            /*
            //setContentView(R.layout.prev_photos);
            LinearLayout layout = (LinearLayout) findViewById(R.id.picturePrev);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


            for (int i = 0; i < 4; i++) {
                layoutParams.setMargins(20, 20, 20, 20);
                layoutParams.gravity = Gravity.CENTER;
                ImageView imageView = new ImageView(MapsActivity.this);

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

//                imageView.setImageResource(R.drawable.image);
                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                imageView.setLayoutParams(layoutParams);

                layout.addView(imageView);

            }*/

            //setContentView(R.layout.add_point);

            //View view = dialog.getCurrentFocus();
            View view = LayoutInflater.from(dialog.getContext()).inflate(R.layout.add_point, null);
            dialog.setContentView(view);
            //View view = getLayoutInflater().inflate(R.layout.add_point, null);

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            //------------------------------
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.picturePrev);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


            for (int i = 0; i < 4; i++) {
                //layoutParams.setMargins(20, 20, 20, 20);
                layoutParams.gravity = Gravity.CENTER;
                ImageView imageView = new ImageView(MapsActivity.this);

                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                imageView.setLayoutParams(layoutParams);

                layout.addView(imageView);

            }
            //------------------------------

            photoList.add(picturePath); //ca ajoute trop tard

            //ImageView imageView = (ImageView) findViewById(R.id.imageUploadPrev);

            //ImageView imageView = (ImageView) view.findViewById(R.id.imageUploadPrev);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
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
}
