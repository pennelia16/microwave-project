package com.example.cmf_d;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.Marker;
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
import com.example.cmf_d.MicrowaveInfoWindow;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //-------------------global vars---------------

    private GoogleMap mMap;
    private LatLng currentLocation;
    private static final String TAG = "MapActivity";
    private PlacesClient placesClient;
    private static final int DEFAULT_ZOOM = 15;
    private static final int CLOSEUP_ZOOM = 25;

    List<LatLng> placeLatLngs = new ArrayList<>();
    List<String> placeNames = new ArrayList<>();
    List<MicrowaveDescription> placeDescriptions = new ArrayList<>();
    List<MicrowaveInfo> placeMicrowaveInfos = new ArrayList<>();

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

    public class MicrowaveInfo {
        private LatLng latLng;
        private String title;
        private MicrowaveDescription description;

        public MicrowaveInfo(LatLng latLng, String title, MicrowaveDescription microwaveDescription){
            this.latLng = latLng;
            this.title = title;
            this.description = microwaveDescription;
        }

        public MicrowaveInfo(int id){
            this.latLng = placeLatLngs.get(id);
            this.title = placeNames.get(id);
            this.description = placeDescriptions.get(id);
        }

        public LatLng getLatLng(){
            return this.latLng;
        }

        public String getTitle(){
            return this.title;
        }

        public MicrowaveDescription getDescription(){
            return this.description;
        }
    }
    public class MicrowaveDescription {
        private int waitTime;
        private String state;
        private int numMicrowaves;

        public MicrowaveDescription(){
            this.waitTime = 0;
            this.state = "Unknown";
            this.numMicrowaves = 0;
        }

        public MicrowaveDescription(int waitTime, String state, int numMicrowaves){
            this.waitTime = waitTime;
            this.state = state;
            this.numMicrowaves = numMicrowaves;
        }

        public int getWaitTime(){
            return this.waitTime;
        }

        public String getState(){
            return this.state;
        }

        public int getNumMicrowaves(){
            return this.numMicrowaves;
        }
    }
    //-------------------------widgets----------------

    private EditText mSearchText;
    private ImageView mGps;

    //------------------------methods-----------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearchText = findViewById(R.id.input_search);
        mGps = findViewById(R.id.ic_gps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initSearchBar(){

        mSearchText.setOnEditorActionListener((new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                  || actionId == EditorInfo.IME_ACTION_DONE
                  || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                  || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    geoLocate();
                }
                return false;
            }
        }));

        mGps.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
        hideSoftKeyboard();
    }

    private void hideSoftKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mSearchText.getWindowToken(),0);
    }

    private void geoLocate(){
        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e){
            Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
        }
        if(list.size() > 0) {
            Address address = list.get(0);

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
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
        if(getDeviceLocation()){
            mMap.setMyLocationEnabled(true);
        }
        initMicrowaveLatLngs(); // Add microwave locations to list
        initPlaceNames(); // Add place names to list
        initPlaceDescriptions();
        initMicrowaveMarkers(); // Add microwave markers to map
        initSearchBar();
    }

    private boolean getDeviceLocation(){
        boolean ret = false;
        Places.initialize(getApplicationContext(), "AIzaSyBBXymrVHpKofAJZJiaMO7sfyOL1AsUlgw");
        placesClient = Places.createClient(this);

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ret = true;
            placesClient.findCurrentPlace(request).addOnSuccessListener(((response) -> {
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    currentLocation = placeLikelihood.getPlace().getLatLng();
                    break;
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));
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
        return ret;
    }
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);
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

    public List<LatLng> getPlaceLatLngs(){
        return placeLatLngs;
    }

    public List<String> getPlaceNames(){
        return placeNames;
    }

    public List<MicrowaveDescription> getPlaceDescriptions(){
        return placeDescriptions;
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

    private void initPlaceDescriptions() {
        placeDescriptions.add(new MicrowaveDescription(0, "Unknown",2)); // CSSS_CUBE
        placeDescriptions.add(new MicrowaveDescription(0, "Unknown",3)); // MACMILLAN_AGORA
        placeDescriptions.add(new MicrowaveDescription(0, "Unknown",2)); // BUCH_TOWER
        placeDescriptions.add(new MicrowaveDescription(0, "Unknown",2)); // BUCH_D_ARTS
        placeDescriptions.add(new MicrowaveDescription(0, "Unknown",4)); // NEST
        placeDescriptions.add(new MicrowaveDescription(0, "Unknown",6)); // SAUDER_EXCH
        placeDescriptions.add(new MicrowaveDescription(0, "Unknown",1)); // GEOGRAPHY
        placeDescriptions.add(new MicrowaveDescription(0, "Unknown",2)); // SUS_LADHA
        placeDescriptions.add(new MicrowaveDescription(0, "Unknown",4)); // ESSS_KAISER
        placeDescriptions.add(new MicrowaveDescription(0, "Unknown",2)); // TOTEM_RES
        placeDescriptions.add(new MicrowaveDescription(0, "Unknown",2)); // VANIER_RES
        placeDescriptions.add(new MicrowaveDescription(0, "Unknown",2)); // ORCHARD_RES
    }

    private void initMicrowaveMarkers() {
        for (MicrowavePlaces place : MicrowavePlaces.values()) {
            MarkerOptions options = new MarkerOptions()
                    .position(placeLatLngs.get(place.getValue()))
                    .title(placeNames.get(place.getValue()))
                    .snippet("Microwave Info:" + "\n" +
                            "Number of microwaves: " + placeDescriptions.get(place.getValue()).getNumMicrowaves() + "\n" +
                            "Estimated wait time: " + placeDescriptions.get(place.getValue()).getWaitTime() + "\n" +
                            "Condition: " + placeDescriptions.get(place.getValue()).getState() + "\n");
            final MicrowaveInfo microwaveInfo = new MicrowaveInfo(place.getValue());
            mMap.addMarker(options);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    mMap.setInfoWindowAdapter(new MicrowaveInfoWindow(microwaveInfo, getApplicationContext()));
                    marker.showInfoWindow();
                    return false;
                }
            });
            Log.d(TAG, "initMicrowaveMarkers: Added marker: lat: " + options.getPosition().latitude + ", lng: " + options.getPosition().longitude + ", name: " + options.getTitle());
        }
    }

}

