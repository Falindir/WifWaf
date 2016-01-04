package shagold.wifwaf.dataBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Participant {

    private int idParticipation;
    private Dog dog;
    private int idWalk;
    private User user;
    private int valid;

    public String getDogName() {
        return dog.getName();
    }

    public int getIdParticipation() {
        return idParticipation;
    }

    public int getIdWalk() {
        return idWalk;
    }

    public Dog getDog(){
        return this.dog;
    }

    public String getUserName() {
        return user.getNickname();
    }

    public int getValid() {
        return valid;
    }

    public User getUser() {
        return user;
    }

    public Participant(Dog idDog, int idWalk, User idUser, int valid){
        this.dog = idDog;
        this.idWalk = idWalk;
        this.user = idUser;
        this.valid = valid; // valid permet de déterminer la couleur de la participation
    }

    public Participant(int idP, Dog idDog, int idWalk, User idUser, int valid){
        this.idParticipation = idP;
        this.dog = idDog;
        this.idWalk = idWalk;
        this.user = idUser;
        this.valid = valid; // valid permet de déterminer la couleur de la participation
    }

    public Participant(JSONObject participantJson) throws JSONException {
        this.idParticipation = participantJson.getInt("idParticipation");
        JSONObject objDog = participantJson.getJSONObject("dog");
        this.dog = new Dog(objDog);
        JSONObject objUser = participantJson.getJSONObject("user");
        this.user = new User(objUser);
        this.idWalk = participantJson.getInt("idWalk");
        this.valid = participantJson.getInt("valid");
    }

    public JSONObject toJson() throws JSONException {
        JSONObject resultat = new JSONObject();
        resultat.put("idDog", this.dog.getIdDog());
        resultat.put("idWalk", this.getIdWalk());
        resultat.put("idUser", this.getUser().getIdUser());
        return resultat;
    }

    public JSONObject toJsonWithId() throws JSONException {
        JSONObject resultat = new JSONObject();
        resultat.put("idParticipation", this.getIdParticipation());
        resultat.put("idDog", this.dog.getIdDog());
        resultat.put("idWalk", this.getIdWalk());
        resultat.put("idUser", this.getUser().getIdUser());
        return resultat;
    }

    public static JSONArray generateJsonArrayWithIdFromListParticipants(List<Participant> participants) throws JSONException {
        JSONArray resultat = new JSONArray();
        for (Participant p : participants){
            resultat.put(p.toJsonWithId());
        }
        return resultat;
    }

    public static List<Participant> generateParticipantsFromJson(JSONArray participantsJson) {
        List<Participant> participants = new ArrayList<Participant>();
        if(participantsJson != null) {
            for (int i = 0; i < participantsJson.length(); i++) {
                JSONObject currentP = null;
                try {
                    currentP = participantsJson.getJSONObject(i);
                    Participant newParticipant = new Participant(currentP);
                    participants.add(newParticipant);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return participants;
    }

    /* allInvalid permet de savoir si la liste de participants est à afficher ou pas
    * Si retourne vrai alors il n'y a pas besoin d'afficher*/
    public static boolean allInvalid(ArrayList<Participant> participants){
        for(Participant p : participants){
            if(p.getValid() == 0){
                return false;
            }
        }
        return true;
    }

}
