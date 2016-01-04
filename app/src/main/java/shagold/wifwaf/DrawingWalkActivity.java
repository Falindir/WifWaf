package shagold.wifwaf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.tool.Constants;

public class DrawingWalkActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private Marker lastAddedM = null;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private PolylineOptions temp;
    private Marker myLocation;
    private PolylineOptions lines = new PolylineOptions();
    private LinkedList<LatLng> linesLatLng = new LinkedList<LatLng>();
    private List<PolylineOptions> pl = new ArrayList<PolylineOptions>();
    private AddressResultReceiver mResultReceiver;
    private boolean startPoint = true;
    private Socket mSocket;
    private Walk walk;
    Context context;

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler h) {
            super(h);
        }

        @Override
        protected void onReceiveResult(int codeResult, Bundle dataResult) {
            if (codeResult == Constants.SUCCESS_RESULT)
                walk.setCity(dataResult.getString(Constants.RESULT_DATA_KEY));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_walk);
        setUpMapIfNeeded();

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        createLocationRequest();

        //Récup balade crée
        walk = (Walk) getIntent().getSerializableExtra("WALK");

        //Gestion socket
        mSocket = SocketManager.getMySocket();
        mSocket.on("RTryAddWalk", onRTryAddWalk);

        //Sélection des points
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                linesLatLng.add(point);
                if(startPoint) {
                    Intent intent = new Intent(DrawingWalkActivity.this, AddressLocationService.class);
                    mResultReceiver = new AddressResultReceiver(new Handler());
                    intent.putExtra(Constants.RECEIVER, mResultReceiver);
                    Location loc = new Location("Dep");
                    loc.setLatitude(point.latitude);
                    loc.setLongitude(point.longitude);
                    intent.putExtra(Constants.LOCATION_DATA_EXTRA, loc);
                    startService(intent);
                    startPoint = false;
                    LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());

                    //Gestion marker point de départ
                    myLocation = mMap.addMarker(new MarkerOptions().position(pos).title(getString(R.string.starting_point)));
                    //Gestion toast sélection de pts
                    CharSequence text = getString(R.string.add_point);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

                if(linesLatLng.size() > 1) {
                    if (lastAddedM != null && temp != null){
                        lastAddedM.remove();
                        //ancien point en bleu
                        temp.color(Color.BLUE);
                        mMap.addPolyline(temp);
                    }
                    temp = new PolylineOptions()
                            .add(linesLatLng.getLast())
                            .add(linesLatLng.get(linesLatLng.size() - 2));
                    temp.color(Color.RED);
                    temp.visible(true);
                    temp.width(10);
                    pl.add(temp);
                    mMap.addPolyline(temp);
                    LatLng last = linesLatLng.get(linesLatLng.size() -1);
                    String nameM = getString(R.string.end);
                    lastAddedM = mMap.addMarker(new MarkerOptions().position(last).title(nameM));
                }
            }
        });
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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


    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        //Ajout marqueur avec position courante
        LatLng mapos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        String maposN = getString(R.string.my_position);
        myLocation = mMap.addMarker(new MarkerOptions().position(mapos).title(maposN));

        //On se centre sur la position courante
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapos, 16));

        //Affichage 1er toast d'instructions
        context = getApplicationContext();
        String firstInstructionDrawWalk = getString(R.string.first_instruction_draw);
        CharSequence text = firstInstructionDrawWalk;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public void finishWalk(View view) {
        for(LatLng p : linesLatLng) {
            walk.addLocationToWalk(p.latitude, p.longitude);
        }
        try {
            JSONObject walkJson = walk.toJson();
            mSocket.emit("TryAddWalk", walkJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener onRTryAddWalk = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            DrawingWalkActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent resultat = new Intent(DrawingWalkActivity.this, UserWalksActivity.class);
                    startActivity(resultat);
                }
            });
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuManager.defaultMenu(this, item) || super.onOptionsItemSelected(item);
    }
}
