package shagold.wifwaf;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.tool.WifWafColor;
import shagold.wifwaf.view.TextValidator;
import shagold.wifwaf.view.ValidateMessage;
import shagold.wifwaf.view.filter.text.EditTextFilter;
import shagold.wifwaf.view.filter.text.NumberFilter;
import shagold.wifwaf.view.filter.text.SizeFilter;

public class AddDogActivity extends AppCompatActivity {

    private Socket mSocket;
    private User mUser;
    private Button confirmAddDog;
    private LinearLayout actlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Gestion socket
        mSocket = SocketManager.getMySocket();
        mUser = SocketManager.getMyUser();
        mSocket.on("RGetAllBehaviours", onRGetAllBehaviours);
        mSocket.emit("getAllBehaviours");

        // Gestion vue + gestion activity's state
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog);

        // Gestion click bouton de confirmation
        this.confirmAddDog = (Button) findViewById(R.id.confirmAddDogButton);
        this.confirmAddDog.setBackgroundColor(WifWafColor.BROWN_DARK);
        /*final Intent getUserDog = new Intent(getApplicationContext(), UserDogsActivity.class);
        confirmAddDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getUserDog);
            }
        });*/
    }

    public void tryAddDog(View view) throws JSONException {
        //Définition des filtres
        EditTextFilter[] filterNumber = {new SizeFilter(0,3), new NumberFilter()}; //pour les champs age et taille
        EditTextFilter[] filterSize = {new SizeFilter()};

        //Récupération des valeurs
        EditText ETname = (EditText) findViewById(R.id.name);
        EditText ETage = (EditText) findViewById(R.id.age);
        EditText ETbreed = (EditText) findViewById(R.id.breed);
        EditText ETsize = (EditText) findViewById(R.id.size);
        EditText ETGetalongwithMales = (EditText) findViewById(R.id.getAlongWithMales);
        EditText ETGetalongwithFemales = (EditText) findViewById(R.id.getAlongWithFemales);
        EditText ETGetalongwithKids = (EditText) findViewById(R.id.getAlongWithKids);
        EditText ETGetalongwithHumans = (EditText) findViewById(R.id.getAlongWithHumans);
        EditText ETDescription = (EditText) findViewById(R.id.description);

        //Test validité des champs
        TextValidator textValidator = new TextValidator();
        boolean valid = true;
        //Name
        ValidateMessage vmName = textValidator.validate(ETname, filterSize);
        if(!vmName.getValue()) {
            valid = false;
            int min = ((SizeFilter) filterSize[0]).getMin();
            int max = ((SizeFilter) filterSize[0]).getMax();
            ETname.setError(vmName.getError().toString() + " min: " + min + " max: " + max);
        }

        //Age
       /* ValidateMessage vmAge = textValidator.validate(ETage, filterNumber);
        if(!vmAge.getValue()) {
            valid = false;
            if (vmAge.getError().equals(ErrorMessage.SIZE)){
                int min = ((SizeFilter) filterSize[0]).getMin();
                int max = ((SizeFilter) filterSize[0]).getMax();
                ETage.setError(vmAge.getError().toString() + " min: " + min + " max: " + max);
            }
            else{
                ETage.setError(vmAge.getError().toString());
            }
        }*/

        //Breed
        ValidateMessage vmBreed = textValidator.validate(ETbreed, filterSize);
        if(!vmBreed.getValue()) {
            valid = false;
            int min = ((SizeFilter) filterSize[0]).getMin();
            int max = ((SizeFilter) filterSize[0]).getMax();
            ETbreed.setError(vmBreed.getError().toString() + " min: " + min + " max: " + max);
        }

        //Size
        /*ValidateMessage vmSize = textValidator.validate(ETsize, filterNumber);
        if(!vmSize.getValue()) {
            valid = false;
            if (vmSize.getError().equals(ErrorMessage.SIZE)){
                int min = ((SizeFilter) filterSize[0]).getMin();
                int max = ((SizeFilter) filterSize[0]).getMax();
                ETsize.setError(vmSize.getError().toString() + " min: " + min + " max: " + max);
            }
            else{
                ETsize.setError(vmSize.getError().toString());
            }
        }*/

        //Get along...
        ValidateMessage vmGAWM = textValidator.validate(ETGetalongwithMales, filterSize);
        if(!vmGAWM.getValue()) {
            valid = false;
            int min = ((SizeFilter) filterSize[0]).getMin();
            int max = ((SizeFilter) filterSize[0]).getMax();
            ETGetalongwithMales.setError(vmGAWM.getError().toString() + " min: " + min + " max: " + max);
        }
        ValidateMessage vmGAWF = textValidator.validate(ETGetalongwithFemales, filterSize);
        if(!vmGAWM.getValue()) {
            valid = false;
            int min = ((SizeFilter) filterSize[0]).getMin();
            int max = ((SizeFilter) filterSize[0]).getMax();
            ETGetalongwithFemales.setError(vmGAWF.getError().toString() + " min: " + min + " max: " + max);
        }
        ValidateMessage vmGAWH = textValidator.validate(ETGetalongwithHumans, filterSize);
        if(!vmGAWH.getValue()) {
            valid = false;
            int min = ((SizeFilter) filterSize[0]).getMin();
            int max = ((SizeFilter) filterSize[0]).getMax();
            ETGetalongwithHumans.setError(vmGAWH.getError().toString() + " min: " + min + " max: " + max);
        }
        ValidateMessage vmGAWK = textValidator.validate(ETGetalongwithKids, filterSize);
        if(!vmGAWK.getValue()) {
            valid = false;
            int min = ((SizeFilter) filterSize[0]).getMin();
            int max = ((SizeFilter) filterSize[0]).getMax();
            ETGetalongwithKids.setError(vmGAWK.getError().toString() + " min: " + min + " max: " + max);
        }

        ValidateMessage vmDesc = textValidator.validate(ETDescription, filterSize);
        if(!vmDesc.getValue()) {
            valid = false;
            int min = ((SizeFilter) filterSize[0]).getMin();
            int max = ((SizeFilter) filterSize[0]).getMax();
            ETDescription.setError(vmDesc.getError().toString() + " min: " + min + " max: " + max);
        }

        if (!valid){
            return;
        }

        //Récupération valeurs des champs
        int age = Integer.parseInt(ETage.getText().toString());
        int size = Integer.parseInt(ETsize.getText().toString());
        String Sbreed = ETbreed.getText().toString();
        String Sname = ETname.getText().toString();
        String Sgetalongwithmales = ETGetalongwithMales.getText().toString();
        String Sgetalongwithfemales = ETGetalongwithFemales.getText().toString();
        String Sgetalongwithkids = ETGetalongwithKids.getText().toString();
        String Sgetalongwithhumans = ETGetalongwithHumans.getText().toString();
        String Sdescription = ETDescription.getText().toString();

        //Test ajout d'un chien
        Dog dog = new Dog(mUser.getIdUser(), Sname, age, Sbreed, size, Sgetalongwithmales, Sgetalongwithfemales, Sgetalongwithkids, Sgetalongwithhumans, Sdescription);
        JSONObject jsonDog = dog.toJson();
        System.out.println("TryAddDog" + jsonDog);
        mSocket.emit("TryAddDog", jsonDog);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuManager.defaultMenu(this, item) || super.onOptionsItemSelected(item);
    }

    private Emitter.Listener onRGetAllBehaviours = new Emitter.Listener() {
        @Override
        public void call(final Object... args)  {
            AddDogActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray listBehaviour =  (JSONArray) args[0];
                    System.out.println("je recois l'objet" + listBehaviour.toString());
                    for (int i = 0; i < listBehaviour.length(); i++) {
                        JSONObject currentObj = null;
                        try {
                            currentObj = listBehaviour.getJSONObject(i);
                            int idBehaviour = currentObj.getInt("idBehaviour");
                            String description = currentObj.getString("description");
                            CheckBox cb = new CheckBox(getApplicationContext());
                            cb.setText(description);
                            cb.setId(idBehaviour);
                            cb.setTextColor(WifWafColor.BLACK);
                            actlayout = (LinearLayout) findViewById(R.id.layout);
                            actlayout.addView(cb, 9);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };
}
