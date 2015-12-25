package shagold.wifwaf;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;

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

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
            }
            sendResultToAddressee(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            sendResultToAddressee(Constants.SUCCESS_RESULT, address.getLocality() + ", " + address.getCountryName());
        }
    }

    private void sendResultToAddressee(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}