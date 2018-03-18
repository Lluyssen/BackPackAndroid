package app.backpackandroid;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
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
