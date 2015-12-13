package shagold.wifwaf.dataBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {

    private int idUser;
    private String email = "";
    private String nickname;
    private String password;
    private String birthday = ""; //TODO classe gestion de dates
    private int phoneNumber = 0;
    private String description = "";
    private String photo = "";
    private int flag = 0;

    public User(){}

    public User(JSONObject userJson) throws JSONException {
        this.idUser = (int) userJson.get("id");
        this.email = (String) userJson.get("email");
        this.nickname = (String) userJson.get("nickname");
        this.password = (String) userJson.get("password");
        this.birthday = (String) userJson.get("birthday");
        this.phoneNumber = (int) userJson.get("phoneNumber");
        this.description = (String) userJson.get("description");
        this.photo = (String) userJson.get("photo");
        this.flag = (int) userJson.get("flag");
    }

    public User(String email, String nickname, String password, String birthday, int phoneNumber, String description, String photo){
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.photo = photo;
        this.flag = 0; // TODO gestion flag: niveau de privatisation des données
    }

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject userJson = new JSONObject();
        userJson.put("email", this.email);
        userJson.put("nickname", this.nickname);
        userJson.put("password", this.password);
        userJson.put("birthday", this.birthday);
        userJson.put("phoneNumber", this.phoneNumber);
        userJson.put("description", this.description);
        userJson.put("photo", this.photo);
        return userJson;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getBirthday() {
        return birthday;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setIdUser(int idUser) { this.idUser = idUser; }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public static String encryptPassword(String source) {
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(source.getBytes(), 0, source.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
    }

}
