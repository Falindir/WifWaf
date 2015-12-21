package shagold.wifwaf;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;

public class PublicPathProfileActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Marker myLocation;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;

    private Walk walk;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_path_profile);
        setUpMapIfNeeded();

        // connexion à l'API
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        walk = (Walk) getIntent().getSerializableExtra("WALK");
        mUser = SocketManager.getMyUser();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {

        if(walk != null) {
            List<shagold.wifwaf.dataBase.Location> path = new ArrayList<shagold.wifwaf.dataBase.Location>(walk.getPath());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(path.get(0).transform(), 16));

            System.out.println("Path " + path.size());

            shagold.wifwaf.dataBase.Location start = path.get(0);

            mMap.addMarker(new MarkerOptions().position(start.transform()).title("Beginning"));

            /*//TODO just for test
            if(path.size() < 2) {

                path.add(new shagold.wifwaf.dataBase.Location(2, 10, 10, 2));
                path.add(new shagold.wifwaf.dataBase.Location(3, 20, 20, 3));
                path.add(new shagold.wifwaf.dataBase.Location(4, 30, 30, 4));
                path.add(new shagold.wifwaf.dataBase.Location(5, 40, 40, 5));
                path.add(new shagold.wifwaf.dataBase.Location(6, 50, 50, 6));

                path.add(0, start);
            }*/

            for(int i = 0; i < path.size() - 1; i++) {
                PolylineOptions temp = new PolylineOptions()
                        .add(path.get(i).transform())
                        .add(path.get(i+1).transform());
                temp.color(Color.RED);
                temp.visible(true);
                temp.width(10);
                mMap.addPolyline(temp);
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        if(myLocation != null) {
            LatLng ll = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            myLocation.setPosition(ll);


        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuManager.defaultMenu(this, item) || super.onOptionsItemSelected(item);
    }

    public void whereAmI(View view) {

        createLocationRequest();

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        //On récupère la position du user et on zoom
        LatLng usrposition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        myLocation = mMap.addMarker(new MarkerOptions().position(usrposition).title("Here you are!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usrposition, 16));

        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void closeWalk(View view) {
        if(walk!= null) {
            Intent walkProfile = new Intent(PublicPathProfileActivity.this, PublicWalkProfileActivity.class);
            walkProfile.putExtra("WALK", walk);
            startActivity(walkProfile);
        }
    }
}
