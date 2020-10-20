package io.github.kheynov.bicyclepark;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.smart_bike_park.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static io.github.kheynov.bicyclepark.Util.Constants.errorTag;
import static io.github.kheynov.bicyclepark.Util.Constants.infoTag;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private GoogleMap mMap;
    private boolean hasStopped = false;
    View mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapView = findViewById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        }
    }

    void setFirebaseDatabase() {
        try {
            DatabaseReference ref = firebaseDatabase.getReference("markers");
            final ArrayList<ArrayList<String>> markers = new ArrayList<>();
            final Snackbar snackbar = Snackbar.make(mapView, "Обновляем...", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    ArrayList<String> markerTypes;
                    for (DataSnapshot marker : dataSnapshot.getChildren()) {
                        markerTypes = new ArrayList<>();
                        for (DataSnapshot markerType : marker.getChildren()) {
                            markerTypes.add(String.valueOf(markerType.getValue()));
                            Log.i("FIREBASE", String.valueOf(markerType.getValue()));
                        }
                        markers.add(markerTypes);
                    }
                    updateMarkers(markers);
                    snackbar.dismiss();
                    snackbar.setText("Обновлено").setDuration(BaseTransientBottomBar.LENGTH_SHORT).show();

                    Log.i(infoTag, markers.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Snackbar.make(mapView,"Не удалось синхронизировать с сервером", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(MapsActivity.this, "Не удалось синхронизировать", Toast.LENGTH_SHORT).show();

                    Log.i(errorTag, databaseError.getMessage());

                }
            });
        } catch (Exception e) {
            Snackbar.make(mapView,"Не удалось синхронизировать с сервером", Snackbar.LENGTH_LONG).show();
            Toast.makeText(MapsActivity.this, "Не удалось синхронизировать", Toast.LENGTH_SHORT).show();
            Log.e(errorTag, e.getMessage());
        }
    }

    void updateMarkers(ArrayList<ArrayList<String>> markersArray) {
        mMap.clear();
        for (ArrayList<String> marker : markersArray) {
            double latitude = Double.parseDouble(marker.get(1));
            double longitude = Double.parseDouble(marker.get(2));
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
            Log.i("MAPINFO", "NEW MARKER");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasStopped) {
            hasStopped = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hasStopped = true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(5.0f);
        LatLng cameraCenter = new LatLng(55.00336, 82.940194);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraCenter, 11.0f));
        setFirebaseDatabase();
    }
}