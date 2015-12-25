package shagold.wifwaf.dataBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Behaviour implements Serializable {
    private int idBehaviour;
    private String description;

    public Behaviour(int id, String desc){
        this.idBehaviour = id;
        this.description = desc;
    }

    public Behaviour(int id){
        this.idBehaviour = id;
    }

    public Behaviour(String desc){
        this.description = desc;
    }

    public String getDescription(){
        return this.description;
    }

    public int getIdBehaviour(){
        return this.idBehaviour;
    }

    // On ne retourne les ID que parce qu'on cherche à créer les dog behaviour ds la bd
    public JSONObject toJson() throws JSONException {
        JSONObject behaviourJson = new JSONObject();
        behaviourJson.put("idBehaviour", this.idBehaviour);
        return behaviourJson;
    }

    public Behaviour(JSONObject behaviourJson) throws JSONException {
        this.idBehaviour = behaviourJson.getInt("idBehaviour");
        this.description = behaviourJson.getString("description");
        System.out.println("Valeurs ds constructeur" + this.idBehaviour + this.description);
    }


    public boolean equals(Behaviour b){
        if (this.description.equals(b.description) && this.idBehaviour == b.idBehaviour){
            return true;
        }
        return false;
    }
}
