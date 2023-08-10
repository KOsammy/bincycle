package com.example.bincycle;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Home extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private Marker marker, riderMarker;
    private Location location;
    private LatLng userLocation, pickupLocation;
    private TextView user;
    private Button confirm, add1, add2, add3, sub1, sub2, sub3, request_btn;
    private EditText value1, value2, value3;
    private int currentValue1, currentValue2, currentValue3;
    private LinearLayout select1, select2, select3;
    private NavigationView navigationView;
    private DatabaseReference customerLocationRef; // Database reference for customer location
    private GeoFire geoFire; // GeoFire instance for customer location
    private DatabaseReference ridersLocationRef; // Database reference for riders' locations
    private GeoQuery ridersGeoQuery; // GeoQuery instance for riders' locations

    private ValueEventListener riderFoundIDListener;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = findViewById(R.id.toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        LinearLayout first = findViewById(R.id.first);
        LinearLayout linear = findViewById(R.id.linear);
        LinearLayout request = findViewById(R.id.request);
        navigationView = findViewById(R.id.nav_view);
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
        request_btn = findViewById(R.id.request_btn);

        select1.setOnClickListener(v -> value1.setText("1"));

        select2.setOnClickListener(v -> value2.setText("1"));

        select3.setOnClickListener(v -> value3.setText("1"));

        add1.setOnClickListener(view -> {
            currentValue1 = Integer.parseInt(value1.getText().toString());
            currentValue1++;
            value1.setText(String.valueOf(currentValue1));
        });

        sub1.setOnClickListener(view -> {
            currentValue1 = Integer.parseInt(value1.getText().toString());
            if (currentValue1 > 0) {
                currentValue1--;
                value1.setText(String.valueOf(currentValue1));
            }
        });

        add2.setOnClickListener(view -> {
            currentValue2 = Integer.parseInt(value2.getText().toString());
            currentValue2++;
            value2.setText(String.valueOf(currentValue2));
        });

        sub2.setOnClickListener(view -> {
            currentValue2 = Integer.parseInt(value2.getText().toString());
            if (currentValue2 > 0) {
                currentValue2--;
                value2.setText(String.valueOf(currentValue2));
            }
        });

        add3.setOnClickListener(v -> {
            currentValue3 = Integer.parseInt(value3.getText().toString());
            currentValue3++;
            value3.setText(String.valueOf(currentValue3));
        });

        sub3.setOnClickListener(v -> {
            currentValue3 = Integer.parseInt(value3.getText().toString());
            if (currentValue3 > 0) {
                currentValue3--;
                value3.setText(String.valueOf(currentValue3));
            }
        });

        confirm.setOnClickListener(view -> {
            first.setVisibility(View.GONE);
            linear.setVisibility(View.VISIBLE);
            request.setVisibility(View.VISIBLE);
        });

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // Initialize riders' location reference

        if (checkLocationPermission()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            userLocation = new LatLng(location.getLatitude(), location.getLongitude());
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
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        marker = mMap.addMarker(new MarkerOptions().position(userLocation).draggable(true).title("Your Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                            @Override
                            public void onCameraIdle() {
                                CameraPosition cameraPosition = mMap.getCameraPosition();
                                // Your camera idle logic here using cameraPosition
                                float currentZoom = -1;
                                if (cameraPosition.zoom != currentZoom){
                                    currentZoom = cameraPosition.zoom;
                                    // do you action here
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom));
                                }
                            }
                        });




                        request_btn.setOnClickListener(v -> {
                            // Stores user location in the database using GeoFire
                            marker.remove();
                            String userID = Objects.requireNonNull(FirebaseAuth.getInstance()
                                    .getCurrentUser()).getUid();
                            DatabaseReference ref = FirebaseDatabase.getInstance()
                                    .getReference("CustomerRequest");
                            GeoFire geofire = new GeoFire(ref);
                            geofire.setLocation(userID, new GeoLocation(location.getLatitude(),location.getLongitude()));
                            pickupLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(pickupLocation)
                                    .title("Pick up Here"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                            getClosestRider();
                            request_btn.setText("Searching for Rider");
                        });
                    }
                }
            };

            locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(2000)
                    .setMaxUpdateDelayMillis(100)
                    .build();

            // Request location updates using the newly created locationRequest
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    private ActionBar getSupportActionBar() {
        return null;
    }

    int radius = 1;
    private Boolean riderFound = false;
    private String riderFoundID; // Changed to String type

    private void getClosestRider() {

        ridersLocationRef = FirebaseDatabase.getInstance().getReference().child("RidersAvailable");
        GeoFire geoFire = new GeoFire(ridersLocationRef);
        ridersGeoQuery = geoFire
                .queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        ridersGeoQuery.removeAllListeners();

        GeoQueryEventListener geoQueryEventListener = new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                   if (!riderFound) {
                       riderFound = true;
                       riderFoundID = key; // Store the rider ID
                       DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference()
                               .child("Users")
                               .child("Riders")
                               .child(riderFoundID);
                       String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                       HashMap<String, Object> map = new HashMap<>();
                       map.put("CustomerRideId", customerId);
                       Log.d("Test Query", String.valueOf(riderFound));
                       riderRef.updateChildren(map);
                       ridersGeoQuery.removeAllListeners();
                       getRiderLocation();
                   }

            }

            @Override
            public void onKeyExited(String key) {
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }


            @Override
            public void onGeoQueryReady() {
                if (!riderFound) {
                    radius++;
                    Log.d("Rider id",riderFoundID);
                    getClosestRider();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        };

        ridersGeoQuery.addGeoQueryEventListener(geoQueryEventListener);
    }

    private void getRiderLocation() {
        DatabaseReference riderWorkingRef = FirebaseDatabase.getInstance().getReference()
                .child("RidersWorking")
                .child(riderFoundID)
                .child("l");
        riderWorkingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Object> map = (List<Object>) snapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    request_btn.setText("Rider found");
                    if (map.get(0) != null) { // Check if map contains enough elements
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng riderLatLng = new LatLng(locationLat,locationLng);

                        // Remove the existing marker (if any) before adding the new one
                        if (riderMarker != null) {
                            riderMarker.remove();
                        }

                        riderMarker = mMap.addMarker(new MarkerOptions().position(riderLatLng).title("Rider Location"));
                        request_btn.setText("Rider Found");
                    } else {
                        // Handle the case where the map does not contain enough elements (latitude and longitude)
                        // For debugging purposes, you can add log statements here to see the content of the map
                        Log.d("RiderLocation", "Invalid map data: ");
                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled method if necessary
            }
        });
    }


    private void setSupportActionBar(Toolbar toolbar) {
        // Set up the toolbar.
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this::onMapClick);
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block this thread waiting for the user's response!
                // After the user sees the explanation, try again to request the permission.
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
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                    Toast.makeText(this, "We got here", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onMapClick(LatLng latLng) {
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

}
