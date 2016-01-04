package shagold.wifwaf.dataBase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
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
    private ArrayList<Behaviour> behaviours = new ArrayList<Behaviour>();
    private String photo;

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
        //Traitement photo
        String photo = dogJson.getString("photo");
        this.photo = photo;

        if("male".equals(dogJson.getString("gender"))) {
            this.male = true;
        }
        else if("female".equals(dogJson.getString("gender"))) {
            this.male = false;
        }
        if (dogJson.has("behaviours")) {
            JSONArray trace = (JSONArray) dogJson.get("behaviours");
            if (trace != null) {
                for (int i = 0; i < trace.length(); i++) {
                    JSONObject currentBehaviour = null;
                    try {
                        currentBehaviour = trace.getJSONObject(i);
                        Behaviour newBehaviour = new Behaviour(currentBehaviour);
                        this.behaviours.add(newBehaviour);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Dog(int idDog, int idUser, String dogName, String age, String breed, int size, String getAlongWithMales, String getAlongWithFemales, String getAlongWithKids, String getAlongWithHumans, String description, boolean male, ArrayList<Behaviour> b, String photo){
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
        this.behaviours = b;
        this.photo = photo;
    }

    public Dog(int idUser, String dogName, String age, String breed, int size, String getAlongWithMales, String getAlongWithFemales, String getAlongWithKids, String getAlongWithHumans, String description, boolean male, ArrayList<Behaviour> b, String photo){
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
        this.behaviours = b;
        this.photo = photo;
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
        JSONArray myBehaviours = new JSONArray();
        for (Behaviour b : behaviours){
            myBehaviours.put(b.toJson());
        }
        dogJson.put("behaviours", myBehaviours);
        dogJson.put("photo", photo);
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
        dogJson.put("photo", photo);
        if(isMale()){
            dogJson.put("gender", "male");
        }
        else{
            dogJson.put("gender", "female");
        }
        JSONArray myBehaviours = new JSONArray();
        for (Behaviour b : behaviours){
            myBehaviours.put(b.toJson());
        }
        dogJson.put("behaviours", myBehaviours);
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

    public String getPhoto() {
        return photo;
    }

    public ArrayList<Behaviour> getBehaviours(){
        return this.behaviours;
    }

    public boolean isMale() {
        return male;
    }

    public String getSex() {
        if(isMale())
            return "male";
        else
            return "female";
    }

    public Bitmap getPhotoBitmap() {
        return decodeBase64(this.photo);
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

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static String encodeTobase64(Bitmap image)
    {
        Bitmap monImg = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        monImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }

}
