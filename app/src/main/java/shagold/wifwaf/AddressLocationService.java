package shagold.wifwaf;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import shagold.wifwaf.tool.Constants;

public class AddressLocationService extends IntentService {

    protected ResultReceiver mReceiver;
    public AddressLocationService(){
        super("AddressLocationService");
    }

    public AddressLocationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";

        // On récupère la position passée au service grâce à un extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);

        mReceiver = intent.getParcelableExtra(
                Constants.RECEIVER);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            // Erreur réseau ou entrée/sortie
            errorMessage = "I/O problems";
            Log.e("HandleIntent", errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Erreur de longitude/lattitude invalides
            errorMessage = "invalid_lat_long_used";
            Log.e("HandleIntent", errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Cas où aucune ville n'est trouvée
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
            }
            envoiResultatAuDestinataire(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            envoiResultatAuDestinataire(Constants.SUCCESS_RESULT,
                    address.getLocality() + ", " + address.getCountryName());
        }
    }

    private void envoiResultatAuDestinataire(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}