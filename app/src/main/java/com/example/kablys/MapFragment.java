package com.example.kablys;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.EditText;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;


public class MapFragment extends Fragment  implements GoogleMap.OnMarkerClickListener {
    boolean mLocationPermissionGranted = false;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int  PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private Context ctx;
    private GoogleMap mMap;
    private GoogleMap inPond;
    private FusedLocationProviderClient fusedLocationClient;
    private  Location location;
    private static final float DEFAULT_ZOOM = 15f;
    public String fish;
    Button search;
    SessionManager sessionManager;
    DatabaseAPI db;
    private  ArrayList<Object[]> locations = new ArrayList<Object[]>();
    private  ArrayList<String[]> fishesInPond = new ArrayList<String[]>();


    public MapFragment() {
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx=context;
    }

    public static BitmapDescriptor getImage(byte[] image) {
        Bitmap map =  BitmapFactory.decodeByteArray(image, 0, image.length);
        Bitmap smallMarker = Bitmap.createScaledBitmap(map, 76, 76, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        return  smallMarkerIcon;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckMaps();
        db = new DatabaseAPI(ctx);
        sessionManager = new SessionManager(ctx);
       Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
       Button add =  toolbar.findViewById(R.id.add_permit);
       add.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        search = rootView.findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onMapSearch(rootView);
            }
        });

        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap Map) {
                mMap = Map;
                inPond = Map;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                locations = db.getLocations(sessionManager.get_username());
                fishesInPond = db.getFishesInPond(sessionManager.get_username());
                addMarkers();
                addFishesToPond();
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
                        DialogMap dial = new DialogMap();
                        dial.setTargetFragment(MapFragment.this, 1);
                        dial.latLng = latLng;
                        FragmentManager fm = getFragmentManager();
                        dial.show(getFragmentManager(), "Add a catch");
                        fm.executePendingTransactions();
                        dial.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                             refresh();
                            }
                        });
                    }
                });

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng point) {
                        DialogFishesInPond dial = new DialogFishesInPond();
                        dial.setTargetFragment(MapFragment.this, 2);
                        dial.latLng = point;
                        FragmentManager fm = getFragmentManager();
                        dial.show(getFragmentManager(), "Add a fish");
                        fm.executePendingTransactions();
                        dial.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                refresh();
                            }
                        });
                    }
                });
            }
        });

        return rootView;
    }

    public void onMapSearch(View view) {
        EditText locationSearch = view.findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address>addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(ctx);
            try {
                addressList = geocoder.getFromLocationName(location, 1);


            } catch (IOException e) {
                e.printStackTrace();
            }
            try
            {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                float zoomLevel = 17.0f;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

            }

            catch (IndexOutOfBoundsException e)
            {

            }

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("Programai reikia GPS kad veiktu tinkamai, Įjungti?")
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
            refresh();
        }

        public  void addMarkers()
        {
            Iterator<Object[]> itr = locations.iterator();
            while (itr.hasNext()){
                Object[] array = itr.next(); // You were just missing saving the value for reuse
                LatLng latLng = new LatLng(Double.parseDouble((String) array[1]), Double.parseDouble((String) array[0]));
                mMap.setOnMarkerClickListener(this);
                if (array[5] != null)
                         mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(String.valueOf(array[2]))
                        .snippet(String.valueOf(array[4]))
                        .icon(getImage((byte[]) array[5])));
                else
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(String.valueOf(array[2]))
                            .snippet(String.valueOf(array[4]))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.fish_icon_map)));
            }

        }

     public  void addFishesToPond()
    {
        Iterator<String[]> itr = fishesInPond.iterator();
        while (itr.hasNext()){
            Object[] array = itr.next(); // You were just missing saving the value for reuse
            LatLng latLng = new LatLng(Double.parseDouble((String) array[1]), Double.parseDouble((String) array[0]));
            inPond.setOnMarkerClickListener(this);
                inPond.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(String.valueOf(array[2]))
                        .snippet("")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.exclamation)));
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

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ctx);

        if(available == ConnectionResult.SUCCESS){
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText( ctx,"Neveiks žemėlapis, nes nėra google paslaugų", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void moveCamera(LatLng latLng, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getDeviceLocation(){

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);

        try{

            final Task location = fusedLocationClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
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


    @Override
    public boolean onMarkerClick(Marker marker) {

        // zuvims kurios pagautos
        Iterator<Object[]> itr = locations.iterator();
        while (itr.hasNext()) {
            Object[] array = itr.next();
            LatLng latLng = new LatLng(Double.parseDouble((String) array[1]), Double.parseDouble((String) array[0]));
            if (latLng.equals(marker.getPosition())) {

                DialogCatch dialCatch = new DialogCatch();
                dialCatch.setTargetFragment(MapFragment.this, 2);
                dialCatch.fish = (String) array[2];
                dialCatch.descr = (String) array[4];
                dialCatch.weight = (String) array[3];
                if (array[5] != null) {
                    dialCatch.image = (byte[]) array[5];
                }
                dialCatch.markerID = (int) array[6];
                FragmentManager fm = getFragmentManager();
                dialCatch.show(getFragmentManager(), "See fish");
                fm.executePendingTransactions();
                dialCatch.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Intent SuccessIntent = new Intent(ctx, DrawerActivity.class);
                        startActivity(SuccessIntent);
                        // hard refresh nes neatsinaujina kitaip
                    }
                });



            }

        }


        // zuvims kurios veisiasi

        Iterator<String[]> pond = fishesInPond.iterator();
        while (pond.hasNext()) {
            String[] array2 = pond.next();
            LatLng latLng2 = new LatLng(Double.parseDouble((String) array2[1]), Double.parseDouble((String) array2[0]));
            if (latLng2.equals(marker.getPosition()) && array2[3].equals("1")) {
                DialogViewInPond dialInpond = new DialogViewInPond();
                dialInpond.setTargetFragment(MapFragment.this, 2);
                dialInpond.fish = array2[2];
                dialInpond.id = array2[4];
                FragmentManager fm = getFragmentManager();
                dialInpond.show(getFragmentManager(), "View Pond");
                fm.executePendingTransactions();
                dialInpond.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Intent SuccessIntent = new Intent(ctx, DrawerActivity.class);
                        startActivity(SuccessIntent);
                        // hard refresh nes neatsinaujina kitaip
                    }
                });
            }

        }

        return false;
    }


    public void refresh()
    {
        // refreshinam kad nauji itemai atsirastu zemelepi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getFragmentManager().beginTransaction().detach(this).commitNow();
            getFragmentManager().beginTransaction().attach(this).commitNow();
        } else {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

}
