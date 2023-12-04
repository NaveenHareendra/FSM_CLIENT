package com.example.agrisiteclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FieldOfficerLocation extends AppCompatActivity implements OnMapReadyCallback {

    private DatabaseReference locationReference;
    GoogleMap map;
    public static int LOCATION_REQUEST_CODE = 100;
    FusedLocationProviderClient fusedLocationProviderClient;
    String fullName, VSDomainFromDB, userIDFromDB;
    Handler handler;
    long refreshTime = 5000; //1000 = 1 second
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_officer_location);

        // Initialize Firebase database reference
        locationReference = FirebaseDatabase.getInstance().getReference("FieldOfficer_Fused_Location_Details");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        handler = new Handler();

        handler.postDelayed(runnable = new Runnable(){
            @Override
            public void run(){
                handler.postDelayed(runnable, refreshTime);
                checkLocationPermission();
            }
        }, refreshTime);

        //checkLocationPermission();
        showUserData();

    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Get Location
            getUserLocation();
        } else {
            requestForpermissions();
        }
    }

    public void showUserData() {
        Intent intent = getIntent();

        fullName = intent.getStringExtra("full_name_of_user");
        VSDomainFromDB = intent.getStringExtra("selectedVSDomain");
        userIDFromDB = intent.getStringExtra("userID");


        //Toast.makeText(TaskPreview.this, "Your Selected FullName in TP Activity: " + fullName, Toast.LENGTH_SHORT).show();
        //Toast.makeText(TaskPreview.this, "Your Selected Division is TP Activity: " + VSDomainFromDB, Toast.LENGTH_SHORT).show();
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location !=null){
                    double lat = location.getLatitude();
                    double longitude = location.getLongitude();

                    LatLng userLocation = new LatLng(lat, longitude);

                    // Upload location to Firebase
                    uploadLocationToFirebase(userLocation);

                    map.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                    map.animateCamera(CameraUpdateFactory.zoomTo(12));

                    map.addMarker(new MarkerOptions().position(userLocation).title("I am Here").icon(setIcon(FieldOfficerLocation.this, R.drawable.mappin)));

                    Log.i("YOUR LOCATION", ""+lat+""+longitude);


                }
            }
        });

    }

    private void uploadLocationToFirebase(LatLng userLocation) {
        // Get the current timestamp and formatted date and time
        long timestamp = System.currentTimeMillis();
        String formattedDateTime = getFormattedDateTime(timestamp);


        String locationName = getLocationName(userLocation.latitude, userLocation.longitude);

        // Create a location object with latitude and longitude
        LocationData locationData = new LocationData(userIDFromDB,VSDomainFromDB,userLocation.latitude, userLocation.longitude, locationName,timestamp,formattedDateTime);

        // Push the location data to Firebase
        //locationReference.push().setValue(locationData);
        // Push the location data to Firebase
        locationReference.child(userIDFromDB).setValue(locationData);
    }

    private String getLocationName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this);
        String locationName = "Unknown";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                locationName = address.getAddressLine(0);  // Get the first address line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return locationName;
    }

    //Convert the timestamp into a human readable date and time format.

    private String getFormattedDateTime(long timestamp) {
        // Convert milliseconds since epoch to a Date object
        Date date = new Date(timestamp);

        // Format the date and time as you want (e.g., "yyyy-MM-dd HH:mm:ss")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    private void requestForpermissions() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == LOCATION_REQUEST_CODE) {
            Toast.makeText(this,"Permission Accepted", Toast.LENGTH_SHORT).show();
            getUserLocation();
        }else{

            Toast.makeText(this,"Permission Rejected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        map.getUiSettings().setMyLocationButtonEnabled(true);
        //6.8532492,79.908804

        /*LatLng latLng = new LatLng(6.8532492,79.908804); // for navinna

        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(12f));

        map.addMarker(new MarkerOptions().position(latLng).title("I am Here"));*/
    }

    //Google Map Marker Icon
    public BitmapDescriptor setIcon(Activity context, int drawableID){
        Drawable drawable = ActivityCompat.getDrawable(context, drawableID);
        drawable.setBounds(0,0,drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth());

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }
}