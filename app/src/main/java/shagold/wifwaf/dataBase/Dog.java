package shagold.wifwaf.dataBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Dog implements Serializable {
    private int idDog;
    private int idUser;
    private String dogName;
    private int age;
    private String breed;
    private int size;
    private String getAlongWithMales;
    private String getAlongWithFemales;
    private String getAlongWithKids;
    private String getAlongWithHumans;
    private String description;
    private boolean male; // TODO finish sex of Dog

    public Dog(){}

    public Dog(JSONObject dogJson) throws JSONException {
        this.idDog = dogJson.getInt("idDog");
        this.idUser = dogJson.getInt("idUser");
        this.dogName = dogJson.getString("dogName");
        this.age = dogJson.getInt("age");
        this.breed = dogJson.getString("breed");
        this.size = dogJson.getInt("size");
        this.getAlongWithMales = dogJson.getString("getAlongWithMales");
        this.getAlongWithFemales = dogJson.getString("getAlongWithFemales");
        this.getAlongWithKids = dogJson.getString("getAlongWithKids");
        this.getAlongWithHumans = dogJson.getString("getAlongWithHumans");
        this.description = dogJson.getString("description");
    }

    public Dog(int idDog, int idUser, String dogName, int age, String breed, int size, String getAlongWithMales, String getAlongWithFemales, String getAlongWithKids, String getAlongWithHumans, String description){
        this.idDog = idDog;
        this.idUser = idUser;
        this.dogName = dogName;
        this.age = age;
        this.breed = breed;
        this.size = size;
        this.getAlongWithMales = getAlongWithMales;
        this.getAlongWithFemales = getAlongWithFemales;
        this.getAlongWithKids = getAlongWithKids;
        this.getAlongWithHumans = getAlongWithHumans;
        this.description = description;
    }

    public Dog(int idUser, String dogName, int age, String breed, int size, String getAlongWithMales, String getAlongWithFemales, String getAlongWithKids, String getAlongWithHumans, String description){
        this.idUser = idUser;
        this.dogName = dogName;
        this.age = age;
        this.breed = breed;
        this.size = size;
        this.getAlongWithMales = getAlongWithMales;
        this.getAlongWithFemales = getAlongWithFemales;
        this.getAlongWithKids = getAlongWithKids;
        this.getAlongWithHumans = getAlongWithHumans;
        this.description = description;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject dogJson = new JSONObject();
        dogJson.put("idUser", this.idUser);
        dogJson.put("dogName", this.dogName);
        dogJson.put("age", this.age);
        dogJson.put("breed", this.breed);
        dogJson.put("size", this.size);
        dogJson.put("getAlongWithMales", this.getAlongWithMales);
        dogJson.put("getAlongWithFemales", this.getAlongWithFemales);
        dogJson.put("getAlongWithKids", this.getAlongWithKids);
        dogJson.put("getAlongWithHumans", this.getAlongWithHumans);
        dogJson.put("description", this.description);
        return dogJson;
    }

    public int getIdDog() {
        return idDog;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return dogName;
    }

    public boolean isMale() {
        return male;
    }
}
