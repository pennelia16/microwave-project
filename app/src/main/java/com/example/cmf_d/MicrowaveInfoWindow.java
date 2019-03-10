package com.example.cmf_d;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class MicrowaveInfoWindow implements GoogleMap.InfoWindowAdapter {

    //public icon;
    private LatLng latLng;
    private String title;
    private MapsActivity.MicrowaveDescription microwaveDescription;

    private final View mWindow;
    private Context mContext;

    public MicrowaveInfoWindow(LatLng latLng, String title, MapsActivity.MicrowaveDescription microwaveDescription, Context context){
        this.latLng = latLng;
        this.title = title;
        this.microwaveDescription = microwaveDescription;
        this.mContext = context;
        this.mWindow = LayoutInflater.from(context).inflate(R.layout.microwave_info_window, null);
    }

    public MicrowaveInfoWindow(MapsActivity.MicrowaveInfo microwaveInfo, Context context){
        this.latLng = microwaveInfo.getLatLng();
        this.title = microwaveInfo.getTitle();
        this.microwaveDescription = microwaveInfo.getDescription();
        this.mContext = context;
        this.mWindow = LayoutInflater.from(context).inflate(R.layout.microwave_info_window, null);
    }

    private void renderWindowText(Marker marker, View view){
        TextView tvTitle = view.findViewById(R.id.title);

        if(!title.equals("")){
            tvTitle.setText(title);
        }

        TextView tvNumMicrowaves = view.findViewById(R.id.numMicrowaves);

        if(!title.equals("")){
            Integer numMicrowaves = microwaveDescription.getNumMicrowaves();
            String str = "Microwaves: " + numMicrowaves.toString();
            tvNumMicrowaves.setText(str);
        }

        TextView tvWaitTime = view.findViewById(R.id.waitTime);

        if(!title.equals("")){
            Integer waitTime = microwaveDescription.getWaitTime();
            String str = "Approximate wait time: " + waitTime.toString();
            tvWaitTime.setText(str);
        }

        // description

        TextView tvState = view.findViewById(R.id.state);

        if(!title.equals("")){
            String str = "Current condition: " + microwaveDescription.getState();
            tvState.setText(str);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}