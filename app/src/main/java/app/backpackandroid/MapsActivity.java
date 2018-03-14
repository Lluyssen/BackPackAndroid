package app.backpackandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap               mMap;
    private List<MarkerOptions>    markerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        markerList = new ArrayList<MarkerOptions>();
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
                                                    MarkerOptions newMarker = new MarkerOptions().position(point).title("My point").snippet("new point");
                                                    mMap.addMarker(newMarker);
                                                    markerList.add(newMarker);
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
