package shagold.wifwaf.dataBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jimmy on 22/11/15.
 */
public class Walk implements Serializable {

    private int id;
    private int idCreator;
    private String title;
    private String description;

    public Walk(int id, int idCreator, String title, String description) {
        this.id = id;
        this.idCreator = idCreator;
        this.title = title;
        this.description = description;
    }

    public Walk(JSONObject json) throws JSONException {
        //TODO check with server
    }

    public int getId() {
        return id;
    }

    public int getIdCreator() {
        return idCreator;
    }

    public boolean isCreator(int idUser) {
        return idCreator == idUser;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
