package shagold.wifwaf.dataBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Dog implements Serializable {
    private int idDog;
    private int idUser;
    private String dogName;
    private String age;
    private String breed;
    private int size;
    private String getAlongWithMales;
    private String getAlongWithFemales;
    private String getAlongWithKids;
    private String getAlongWithHumans;
    private String description;
    private boolean male;

    public Dog(){}

    public Dog(int id){
        this.idDog = id;
    }

    public Dog(JSONObject dogJson) throws JSONException {
        this.idDog = dogJson.getInt("idDog");
        this.idUser = dogJson.getInt("idUser");
        this.dogName = dogJson.getString("dogName");
        this.age = dogJson.getString("age");
        this.breed = dogJson.getString("breed");
        this.size = dogJson.getInt("size");
        this.getAlongWithMales = dogJson.getString("getAlongWithMales");
        this.getAlongWithFemales = dogJson.getString("getAlongWithFemales");
        this.getAlongWithKids = dogJson.getString("getAlongWithKids");
        this.getAlongWithHumans = dogJson.getString("getAlongWithHumans");
        this.description = dogJson.getString("description");
        if("male".equals(dogJson.getString("gender"))) {
            this.male = true;
        }
        else if("female".equals(dogJson.getString("gender"))) {
            this.male = false;
        }
    }

    public Dog(int idDog, int idUser, String dogName, String age, String breed, int size, String getAlongWithMales, String getAlongWithFemales, String getAlongWithKids, String getAlongWithHumans, String description, boolean male){
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
        this.male = male;
    }

    public Dog(int idUser, String dogName, String age, String breed, int size, String getAlongWithMales, String getAlongWithFemales, String getAlongWithKids, String getAlongWithHumans, String description, boolean male){
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
        this.male = male;
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
        if(isMale()){
            dogJson.put("gender", "male");
        }
        else{
            dogJson.put("gender", "female");
        }
        return dogJson;
    }

    public JSONObject toJsonWithId() throws JSONException {
        JSONObject dogJson = new JSONObject();
        dogJson.put("idDog", this.idDog);
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
        if(isMale()){
            dogJson.put("gender", "male");
        }
        else{
            dogJson.put("gender", "female");
        }
        return dogJson;
    }

    public int getIdDog() {
        return idDog;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return dogName;
    }

    public String getGetAlongWithMales(){ return getAlongWithMales; }

    public String getGetAlongWithFemales(){ return getAlongWithFemales; }

    public String getGetAlongWithKids(){ return getAlongWithKids; }

    public String getGetAlongWithHumans(){ return getAlongWithHumans; }

    public String getAge() { return age; }

    public String getBreed() { return breed; }

    public int getSize() { return size; }

    public boolean isMale() {
        return male;
    }

    public String getSex() {
        if(isMale())
            return "male";
        else
            return "female";
    }

    public static List<Dog> generateDogsFromJson(JSONArray dogsJSON) {
        List<Dog> dogs = new ArrayList<Dog>();
        if(dogsJSON != null) {
            for (int i = 0; i < dogsJSON.length(); i++) {
                JSONObject currentObj = null;
                try {
                    currentObj = dogsJSON.getJSONObject(i);
                    Dog newDog = new Dog(currentObj);
                    dogs.add(newDog);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return dogs;
    }
}
