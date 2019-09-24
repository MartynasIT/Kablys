package com.example.kablys;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;


public class MapFragment extends Fragment{
    boolean mLocationPermissionGranted = false;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int  PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private Context ctx;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private  Location location;
    private static final float DEFAULT_ZOOM = 15f;
    public String fish;
    boolean ok_pressed;
    SessionManager sessionManager;
    DatabaseAPI db;
    private  ArrayList<String[]> locations = new ArrayList<String[]>();


    public MapFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckMaps();
        db = new DatabaseAPI(ctx);
        sessionManager = new SessionManager(ctx);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        //db.getLocations("snapsas");
        //((AppCompatActivity)getActivity()).getSupportActionBar().hide();
       // Toolbar toolbarMaps = rootView.findViewById(R.id.toolmaps);
       // ((AppCompatActivity)getActivity()).setSupportActionBar(toolbarMaps);

        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap Map) {
                mMap = Map;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                locations = db.getLocations(sessionManager.get_username());
                addMarkers();
                //db.getLocations("snapsas");
                if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    getDeviceLocation();
                }
                else {

                    getLocationPermission();
                }


                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        //FragmentManager fm = getActivity().getSupportFragmentManager();
                        DialogMap dial = new DialogMap();
                        dial.setTargetFragment(MapFragment.this, 1);
                        dial.latLng = latLng;
                        dial.show(getFragmentManager(), "Add a catch");

                           /* mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(fish)
                                    .snippet("Your marker snippet")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.fish_icon_map)));

                            */


                    }
                });
            }
        });

        return rootView;
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Taip", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                })
                .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) ctx.getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            // refreshinam tam kad atsinaujintu zemelapis po allow

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getFragmentManager().beginTransaction().detach(this).commitNow();
            getFragmentManager().beginTransaction().attach(this).commitNow();
        } else {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
        }


        public  void addMarkers()
        {
            for (String[] array : locations){
                LatLng latLng = new LatLng(Double.parseDouble(array[1]), Double.parseDouble(array[0]));
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(array[2])
                        .snippet(array[3])
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.fish_icon_map)));
            }
        }




    public boolean CheckMaps()
    {
        if (isServicesOK())
        {
            if(isMapsEnabled())
            {
                return true;
            }
        }

        return false;
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ctx);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText( ctx,"You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);

        try{

            final Task location = fusedLocationClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation!=null)
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        }
                    }
                });

        }catch (SecurityException e){

        }
    }


}
