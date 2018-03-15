package app.backpackandroid;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.HttpURLConnection;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        markerList = new ArrayList<Point>();
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

        // Add a marker in Sydney and move the camera

        Bitmap bitmap;
        bitmap = getImage("http://jbinformatique.com/2017/12/09/developpement-android-zoom-imageview-java");

        LatLng tek = new LatLng(48.81552199999999, 2.362973000000011);
        mMap.addMarker(new MarkerOptions().position(tek).title("Tek").snippet("tetetetetetetetetetetet")/*.icon(BitmapDescriptorFactory.fromBitmap(bitmap))*/);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tek));
    }

    public void addPoint(LatLng point)
    {
        final Dialog dialog = new Dialog(MapsActivity.this);
        View view = getLayoutInflater().inflate(R.layout.add_point, null);
        Button addButton = (Button) view.findViewById(R.id.addBtn);
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
                }
            }
        });

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
