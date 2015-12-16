package shagold.wifwaf.dataBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Walk implements Serializable {

    private int idWalk;
    private int idDog;
    private int idUser;
    private String walkName;
    private String description;
    private String city;
    private String departure; //TODO Timestamp?
    private ArrayList<Location> path = new ArrayList<Location>();

    public Walk(int id, int idDog, int idUser, String wN, String description, String city, String dep) {
        this.idWalk = id;
        this.idDog = idDog;
        this.idUser = idUser;
        this.walkName = wN;
        this.description = description;
        this.city = city;
        this.departure = dep;
    }

    public Walk(int idDog, int idUser, String wN, String description, String city, String dep) {
        this.idDog = idDog;
        this.idUser = idUser;
        this.walkName = wN;
        this.description = description;
        this.city = city;
        this.departure = dep;
    }

    public void addLocationToWalk(double latitude, double longitude){
        int order = path.size() + 1;
        Location newLoc = new Location(latitude, longitude, order);
        path.add(newLoc);
    }

    public Walk(JSONObject jsonWalk) throws JSONException {
        //TODO check with server
    }

    public JSONObject toJson() throws JSONException {
        JSONObject walkJson = new JSONObject();
        walkJson.put("idDog", this.idDog);
        walkJson.put("idUser", this.idUser);
        walkJson.put("walkName", this.walkName);
        walkJson.put("description", this.description);
        walkJson.put("city", this.city);
        walkJson.put("departure", this.departure);
        JSONArray mylocations = new JSONArray();
        for (Location l : path){
            mylocations.put(l.toJson());
        }
        walkJson.put("location", mylocations);
        System.out.println("Résultat" + walkJson);
        return walkJson;
    }

    public int getIdWalk() {
        return idWalk;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getTitle() {
        return walkName;
    }

    public String getDescription() {
        return description;
    }

    public String getCity() {
        return city;
    };

    public void setCity(String city) {
        this.city = city;
    }


}
