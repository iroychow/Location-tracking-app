package com.example.ishitaroychowdhury.googleapidemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListenerstart, locationListenerEnd;
    PolylineOptions polylineOptions;
    LatLngBounds.Builder builder = null;
    Boolean flag = false, endFlag = false;
    LatLngBounds bounds;

    UiSettings uiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        //to get location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //builder
        builder = new LatLngBounds.Builder();

        //polyline
        polylineOptions = new PolylineOptions();


        //location listener for start tracking
        locationListenerstart = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //to get lat and long
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng1 = new LatLng(latitude, longitude);
                polylineOptions.add(latLng1);
                builder.include(latLng1);
                bounds = builder.build();
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    String str = addressList.get(0).getLocality() + " , ";
                    str += addressList.get(0).getCountryName();

                    if (flag == false) {
                        mMap.addMarker(new MarkerOptions().position(latLng1).title(str));
                        //set flag
                        endFlag = true;
                        flag = true;
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Polyline polyline = mMap.addPolyline(polylineOptions);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };


        // location listener for end tracking
        locationListenerEnd = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //to get lat and long
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng2 = new LatLng(latitude, longitude);
                polylineOptions.add(latLng2);
                builder.include(latLng2);
                bounds = builder.build();
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    String str = addressList.get(0).getSubLocality() + " , ";
                    str += addressList.get(0).getLocality();


                    mMap.addMarker(new MarkerOptions().position(latLng2).title(str));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Polyline polyline = mMap.addPolyline(polylineOptions);
                locationManager.removeUpdates(locationListenerEnd);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {




                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }

                if (endFlag == true) {
                    locationManager.removeUpdates(locationListenerstart);
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                        Toast.makeText(MapsActivity.this, "Stopped location tracking", Toast.LENGTH_SHORT).show();
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerEnd);
                        flag = false;
                        endFlag = false;

                    } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        Toast.makeText(MapsActivity.this, "Stopped location tracking", Toast.LENGTH_SHORT).show();
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerEnd);
                        flag = false;
                        endFlag = false;
                    }

                    //mMap.clear();

                } else {
                    //*******condition to check if provider is enabled
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        Toast.makeText(MapsActivity.this, "Start location tracking", Toast.LENGTH_SHORT).show();
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerstart);


                    } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(MapsActivity.this, "Start location tracking", Toast.LENGTH_SHORT).show();
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerstart);

                    }

                }
            }

        });

    }

}
