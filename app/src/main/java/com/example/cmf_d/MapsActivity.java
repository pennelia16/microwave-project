package com.example.cmf_d;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng currentLocation;
    private static final String TAG = "MapActivity";

    List<LatLng> placeLatLngs = new ArrayList<>();
    List<String> placeNames = new ArrayList<>();

    public enum MicrowavePlaces {
        CSSS_CUBE(0), MACMILLAN_AGORA(1), BUCH_TOWER(2), BUCH_D_ARTS(3), NEST(4), SAUDER_EXCH(5),
        GEOGRAPHY(6), SUS_LADHA(7), ESSS_KAISER(8), TOTEM_RES(9), VANIER_RES(10), ORCHARD_RES(11);

        private MicrowavePlaces(final int val) {
            this.val = val;
        }

        private int val;

        public int getValue() {
            return val;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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
        Places.initialize(getApplicationContext(), "AIzaSyBBXymrVHpKofAJZJiaMO7sfyOL1AsUlgw");

        PlacesClient placesClient = Places.createClient(this);

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            placesClient.findCurrentPlace(request).addOnSuccessListener(((response) -> {
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    currentLocation = placeLikelihood.getPlace().getLatLng();
                    break;
                }
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            })).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e("Error", "Place not found: " + apiException.getStatusCode());
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            getLocationPermission();
        }

        initMicrowaveLatLngs(); // Add microwave locations to list
        initPlaceNames(); // Add place names to list
        initMicrowaveMarkers(); // Add microwave markers to map
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        ActivityCompat.requestPermissions(this,
                new String[]{ACCESS_FINE_LOCATION},
                0);
    }

    private void initMicrowaveLatLngs() {
        placeLatLngs.add(new LatLng(49.261228, -123.248809)); // CSSS_CUBE
        placeLatLngs.add(new LatLng(49.261288, -123.251227)); // MACMILLAN_AGORA
        placeLatLngs.add(new LatLng(49.268764, -123.253448)); // BUCH_TOWER
        placeLatLngs.add(new LatLng(49.269457, -123.254338)); // BUCH_D_ARTS
        placeLatLngs.add(new LatLng(49.266653, -123.249886)); // NEST
        placeLatLngs.add(new LatLng(49.265225, -123.253602)); // SAUDER_EXCH
        placeLatLngs.add(new LatLng(49.266226, -123.256133)); // GEOGRAPHY
        placeLatLngs.add(new LatLng(49.266199, -123.251373)); // SUS_LADHA
        placeLatLngs.add(new LatLng(49.262322, -123.249267)); // ESSS_KAISER
        placeLatLngs.add(new LatLng(49.258191, -123.252981)); // TOTEM_RES
        placeLatLngs.add(new LatLng(49.264962, -123.258670)); // VANIER_RES
        placeLatLngs.add(new LatLng(49.260601, -123.251164)); // ORCHARD_RES
    }

    private void initPlaceNames() {
        placeNames.add("Computer Science Student Society"); // CSSS_CUBE
        placeNames.add("null"); // MACMILLAN_AGORA
        placeNames.add("null"); // BUCH_TOWER
        placeNames.add("null"); // BUCH_D_ARTS
        placeNames.add("null"); // NEST
        placeNames.add("null"); // SAUDER_EXCH
        placeNames.add("null"); // GEOGRAPHY
        placeNames.add("null"); // SUS_LADHA
        placeNames.add("null"); // ESSS_KAISER
        placeNames.add("null"); // TOTEM_RES
        placeNames.add("null"); // VANIER_RES
        placeNames.add("null"); // ORCHARD_RES
    }

    private void initMicrowaveMarkers() {
        for (MicrowavePlaces place : MicrowavePlaces.values()) {
            MarkerOptions options = new MarkerOptions().position(placeLatLngs.get(place.getValue())).title(placeNames.get(place.getValue()));
            mMap.addMarker(options);
            Log.d(TAG, "initMicrowaveMarkers: Added marker: lat: " + options.getPosition().latitude + ", lng: " + options.getPosition().longitude + ", name: " + options.getTitle());
        }
    }

}

