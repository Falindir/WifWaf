package shagold.wifwaf.dataBase;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private int idUser;
    private String email;
    private String nickname;
    private String password;
    private String birthday;
    private int phoneNumber;
    private String description;
    private String photo;

    public User(){}

    public User(String email, String nickname, String password, String birthday, int phoneNumber, String description, String photo){
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.photo = photo;
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

    public void setIdUser(int idUser) {
        this.idUser = idUser;

    }

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



}