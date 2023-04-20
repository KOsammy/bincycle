package com.example.bincycle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Home extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private Marker marker;
    TextView user;
    Button confirm, add1, add2, add3, sub1, sub2, sub3;
    EditText value1, value2, value3;
    int currentValue1, currentValue2, currentValue3;
    LinearLayout select1, select2, select3;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user = findViewById(R.id.name);
        Toolbar toolbar = findViewById(R.id.toolbar);
        String fullname = getIntent().getStringExtra("username");
        user.setText(fullname);

        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        LinearLayout first = findViewById(R.id.first);
        LinearLayout linear = findViewById(R.id.linear);
        LinearLayout request = findViewById(R.id.request);
        confirm = findViewById(R.id.confirm);
        add1 = findViewById(R.id.add1);
        add2 = findViewById(R.id.add2);
        add3 = findViewById(R.id.add3);
        sub1 = findViewById(R.id.sub1);
        sub2 = findViewById(R.id.sub2);
        sub3 = findViewById(R.id.sub3);
        value1 = findViewById(R.id.value1);
        value2 = findViewById(R.id.value2);
        value3 = findViewById(R.id.value3);
        select1 = findViewById(R.id.select1);
        select2 = findViewById(R.id.select2);
        select3 = findViewById(R.id.select3);


        select1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value1.setText("1");
            }
        });

        select2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value2.setText("1");
            }
        });

        select3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value3.setText("1");
            }
        });

        add1.setOnClickListener(view -> {
            currentValue1 = Integer.parseInt(value1.getText().toString());
            currentValue1++;
            value1.setText(String.valueOf(currentValue1));
        });

        sub1.setOnClickListener(view -> {
            currentValue1 = Integer.parseInt(value1.getText().toString());
            if(currentValue1 > 0){
                currentValue1--;
                value1.setText(String.valueOf(currentValue1));
            }
        });

        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentValue2 = Integer.parseInt(value2.getText().toString());
                currentValue2++;
                value2.setText(String.valueOf(currentValue2));
            }
        });
        sub2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentValue2 = Integer.parseInt(value2.getText().toString());
                if (currentValue2 > 0) {
                    currentValue2--;
                    value2.setText(String.valueOf(currentValue2));
                }
            }
        });

        add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentValue3 = Integer.parseInt(value3.getText().toString());
                currentValue3++;
                value3.setText(String.valueOf(currentValue3));
            }
        });

        sub3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentValue3 = Integer.parseInt(value3.getText().toString());
                if (currentValue3 > 0) {
                    currentValue3--;
                    value3.setText(String.valueOf(currentValue3));
                }
            }
        });

        confirm.setOnClickListener(view -> {
            first.setVisibility(View.GONE);
            linear.setVisibility(View.VISIBLE);
            request.setVisibility(View.VISIBLE);

        });

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkLocationPermission()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            marker = mMap.addMarker(new MarkerOptions().position(userLocation).draggable(true).title("Your Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18));
                            marker.remove();
                        } else {
                            Toast.makeText(Home.this, "Location not found", Toast.LENGTH_SHORT).show();
                        }
                    });

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        marker.remove();
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                       marker = mMap.addMarker(new MarkerOptions().position(userLocation).draggable(true).title("Your Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18));

                        //stores user location in the database.
                        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child("UserId");
                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));

                    }
                }


            };

            locationRequest = new LocationRequest();
            locationRequest.setInterval(5000); // 5 seconds
            locationRequest.setFastestInterval(3000); // 3 seconds
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    private void setSupportActionBar(Toolbar toolbar) {
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
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        //googleMap.setMapStyle( MapStyleOptions.loadRawResourceStyle(getApplicationContext(),R.raw.bincycle_maps_style));
        mMap.setOnMapClickListener(this::onMapClick);


    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                    Toast.makeText(this, "we got here", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void onMapClick(LatLng latLng){
        marker.setPosition(latLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void onLocationChanged(Location location) {
        if (marker != null) {
            marker.remove();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("RidersOnline");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userID);

    }
}