package com.example.cmf_d;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.util.concurrent.ListenableFutureTask;

/**
 * Credit: https://www.geodatasource.com/developers/java
 */
public class DistanceCalculator
{
    public static double getDistance(LatLng latLng1, LatLng latLng2) {

        double lat1 = latLng1.latitude;
        double lon1 = latLng1.longitude;
        double lat2 = latLng2.latitude;
        double lon2 = latLng2.longitude;

        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515 * 1.609344;
            return (dist);
        }
    }
}
