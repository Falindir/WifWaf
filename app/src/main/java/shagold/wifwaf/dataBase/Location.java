package shagold.wifwaf.dataBase;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Location implements Serializable {
    private int idLocation;
    private double lattitude;
    private double longitude;
    private int ordering;

    public Location(int idLocation, double latt, double longitude, int o){
        this.idLocation = idLocation;
        this.lattitude = latt;
        this.longitude = longitude;
        this.ordering = o;
    }

    public Location(double latt, double longitude, int o){
        this.lattitude = latt;
        this.longitude = longitude;
        this.ordering = o;
    }

    public Location(JSONObject jsonLocation) throws JSONException {
        this.idLocation = (int) jsonLocation.get("idLocation");
        String lat = (String) jsonLocation.get("lattitude");
        this.lattitude = Double.parseDouble(lat);
        String longi = (String) jsonLocation.get("longitude");
        this.longitude = Double.parseDouble(longi);
        this.ordering = (int) jsonLocation.get("ordering");
    }

    public JSONObject toJson() throws JSONException {
        JSONObject locationJson = new JSONObject();
        locationJson.put("latitude", this.lattitude);
        locationJson.put("longitude", this.longitude);
        locationJson.put("ordering", this.ordering);
        return locationJson;
    }

    public double getLattitude() {
        return lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng transform() {
        return new LatLng(lattitude, longitude);
    }
}
