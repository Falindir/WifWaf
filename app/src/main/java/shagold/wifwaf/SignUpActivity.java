package shagold.wifwaf;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.tool.WifWafDatePickerFragment;
import shagold.wifwaf.view.ErrorMessage;
import shagold.wifwaf.view.TextValidator;
import shagold.wifwaf.view.ValidateMessage;
import shagold.wifwaf.view.filter.text.EditTextFilter;
import shagold.wifwaf.view.filter.text.EmailFilter;
import shagold.wifwaf.view.filter.text.NumberFilter;
import shagold.wifwaf.view.filter.text.SizeFilter;

public class SignUpActivity extends AppCompatActivity {

    private Socket mSocket;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mSocket = SocketManager.getMySocket();
        mSocket.on("onTestString", onTestString);
        mSocket.on("onTestJson", onTestJson);
        mSocket.on("onTestJsonArray", onTestJsonArray);
        mSocket.on("RTrySignUp", onRTrySignUp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuManager.emptyMenu(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO faire socket.off pour chaque event écouté
        mSocket.off("RTrySignUp", onRTrySignUp);
    }

    public void showDatePickerDialog(View v) {
        WifWafDatePickerFragment newFragment = new WifWafDatePickerFragment();
        TextView ETBirthday = (TextView) findViewById(R.id.Birthday);
        newFragment.setDateText(ETBirthday);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void trySignUp(View view) throws JSONException {
        //Définition des filtres
        EditTextFilter[] filterNumber = {new SizeFilter(0,9), new NumberFilter()}; //pour le champ numéro de téléphone
        EditTextFilter[] filterSize = {new SizeFilter()}; // pour les champs texte classiques
        EditTextFilter[] filterEmail = {new SizeFilter(), new EmailFilter()};

        // Récupération des valeurs
        EditText ETnickname = (EditText) findViewById(R.id.Nickname);
        EditText ETpassword = (EditText) findViewById(R.id.Password);
        EditText ETemail = (EditText) findViewById(R.id.Email);
        EditText ETPhoneNumber = (EditText) findViewById(R.id.PhoneNumber);
        TextView ETBirthday = (TextView) findViewById(R.id.Birthday);
        ETBirthday.setFocusable(false);
        EditText ETDescription = (EditText) findViewById(R.id.Description);

        //Test validité des champs
        TextValidator textValidator = new TextValidator();
        boolean valid = true;
        //Nickname
        ValidateMessage vmNickname = textValidator.validate(ETnickname, filterSize);
        if(!vmNickname.getValue()) {
            valid = false;
            int min = ((SizeFilter) filterSize[0]).getMin();
            int max = ((SizeFilter) filterSize[0]).getMax();
            ETnickname.setError(vmNickname.getError().toString() + " min: " + min + " max: " + max);
        }

        //Mot de passe
        ValidateMessage vmPass = textValidator.validate(ETpassword, filterSize);
        if(!vmPass.getValue()) {
            valid = false;
            int min = ((SizeFilter) filterSize[0]).getMin();
            int max = ((SizeFilter) filterSize[0]).getMax();
            ETpassword.setError(vmPass.getError().toString() + " min: " + min + " max: " + max);
        }

        //Adresse mail
        ValidateMessage vmMail = textValidator.validate(ETemail, filterEmail);
        if(!vmMail.getValue()) {
            valid = false;
            if (vmMail.getError().equals(ErrorMessage.SIZE)){
                int min = ((SizeFilter) filterSize[0]).getMin();
                int max = ((SizeFilter) filterSize[0]).getMax();
                ETemail.setError(vmMail.getError().toString() + " min: " + min + " max: " + max);
            }
            else{
                ETemail.setError(vmMail.getError().toString());
            }
        }

        //Numéro de téléphone
        /*ValidateMessage vmNumTel = textValidator.validate(ETPhoneNumber, filterNumber);
        if(!vmNumTel.getValue()) {
            valid = false;
            if (vmNumTel.getError().equals(ErrorMessage.SIZE)){
                int min = ((SizeFilter) filterSize[0]).getMin();
                int max = ((SizeFilter) filterSize[0]).getMax();
                ETPhoneNumber.setError(vmNumTel.getError().toString() + " min: " + min + " max: " + max);
            }
            else{
                ETPhoneNumber.setError(vmNumTel.getError().toString());
            }
        }*/

        //Description
        ValidateMessage vmDescrip = textValidator.validate(ETDescription, filterSize);
        if(!vmDescrip.getValue()) {
            valid = false;
            int min = ((SizeFilter) filterSize[0]).getMin();
            int max = ((SizeFilter) filterSize[0]).getMax();
            ETDescription.setError(vmDescrip.getError().toString() + " min: " + min + " max: " + max);
        }

        if (!valid){
            return;
        }

        //Récupération valeur des champs
        String Snickname = ETnickname.getText().toString();
        String Spassword = ETpassword.getText().toString();
        String Semail = ETemail.getText().toString();
        int SphoneNumber = Integer.parseInt(ETPhoneNumber.getText().toString());
        String Sbirthday = ETBirthday.getText().toString();
        String Sdescription = ETDescription.getText().toString();

        //Encryptage mdp
        Spassword = User.encryptPassword(Spassword);

        //Test inscription
        User user = new User(Semail,Snickname,Spassword,Sbirthday,SphoneNumber,Sdescription,"");
        JSONObject jsonUser = user.toJson();
        mSocket.emit("TrySignUp", jsonUser);
    }

    private Emitter.Listener onTestString = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            SignUpActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String a = (String) args[0];
                    System.out.println("je recois onTest" + a);
                    mSocket.emit("onTestSendString", "unMot");
                }
            });
        }
    };

    private Emitter.Listener onTestJson = new Emitter.Listener() {
        @Override
        public void call(final Object... args)  {
            SignUpActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject a =  (JSONObject) args[0];
                    System.out.println("je recois l'objet" + a.toString());
                    //préparer l'objet et faire l'envoi ici
                    JSONObject JsonTest = new JSONObject();
                    try {
                        JsonTest.put("email", "adr@mail.fr");
                        JsonTest.put("name", "thisismyname");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("JsonObject préparé: " + JsonTest.toString());
                    mSocket.emit("onTestSendJson", JsonTest);
                }
            });
        }
    };

    private Emitter.Listener onTestJsonArray = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            SignUpActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray a = (JSONArray) args[0];
                    System.out.println("je recois l'objet" + a.toString());
                    //préparer l'objet pour l'envoi*
                    JSONArray JsonArrayTest = null;
                    try {
                        JsonArrayTest = new JSONArray()
                                .put(new JSONObject()
                                        .put("prenom", "jimmy")
                                        .put("nom", "lopez"))
                                .put(new JSONObject()
                                        .put("prenom", "marlene")
                                        .put("nom", "gui"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.print("JsonArray préparé: " + JsonArrayTest.toString());
                    mSocket.emit("onTestSendJsonArray", JsonArrayTest);
                }
            });
        }
    };

    private Emitter.Listener onRTrySignUp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            SignUpActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject param = (JSONObject) args[0];
                    try {
                        if((int)param.get("id") < 0) { //TODO faire un traitement spécifique pour chaque erreur?
                           AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                            alertDialog.setTitle("Erreur");
                            alertDialog.setMessage("Cette adresse mail est déjà utilisée"); //TODO internationalisation
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        else{
                            mUser = new User(param);
                            Intent resultat = new Intent(SignUpActivity.this, HomeActivity.class);
                            System.out.println("[Réussite inscription]"+param);
                            SocketManager.setMyUser(mUser);

                            startActivity(resultat);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
