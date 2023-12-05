package com.example.agrisiteclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FOPreviousLocation extends AppCompatActivity implements OnMapReadyCallback {
    String fullName, VSDomainFromDB, userIDFromDB;
    private DatabaseReference locationReference;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foprevious_location);

        // Initialize Firebase database reference
        locationReference = FirebaseDatabase.getInstance().getReference("FieldOfficer_Fused_Location_Details");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView2);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        // Retrieve and display previous location history
        retrieveLocationHistory();
    }

    private void retrieveLocationHistory() {
        Query query = locationReference.child("userID"); // Replace with the actual user ID

        query.get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                LocationData locationData = snapshot.getValue(LocationData.class);
                if (locationData != null) {
                    LatLng location = new LatLng(locationData.getLatitude(), locationData.getLongitude());
                    String locationName = locationData.getLocationName();

                    // Display markers for each historical location
                    map.addMarker(new MarkerOptions()
                            .position(location)
                            .title("Visited on " + locationData.getFormattedDateTime())
                            .snippet(locationName)
                            .icon(setIcon(FOPreviousLocation.this, R.drawable.baseline_location_on_24)));

                    // Move camera to the last location
                    map.moveCamera(CameraUpdateFactory.newLatLng(location));
                    map.animateCamera(CameraUpdateFactory.zoomTo(12f));
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("FOPreviousLocation", "Error retrieving location history", e);
        });
    }

    // Google Map Marker Icon
    public BitmapDescriptor setIcon(AppCompatActivity context, int drawableID) {
        Drawable drawable = ActivityCompat.getDrawable(context, drawableID);
        drawable.setBounds(0, 0, drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth());

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // Implement other necessary methods and variables here

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        map.getUiSettings().setMyLocationButtonEnabled(true);
    }

    public void showUserData() {
        Intent intent = getIntent();

        fullName = intent.getStringExtra("full_name_of_user");
        VSDomainFromDB = intent.getStringExtra("selectedVSDomain");
        userIDFromDB = intent.getStringExtra("userID");
    }
}
