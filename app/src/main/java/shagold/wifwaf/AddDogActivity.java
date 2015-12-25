package shagold.wifwaf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import shagold.wifwaf.dataBase.Behaviour;
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
    private ArrayList<Behaviour> selectedBehaviours = new ArrayList<Behaviour>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Gestion socket
        mSocket = SocketManager.getMySocket();
        mUser = SocketManager.getMyUser();
        mSocket.on("RGetAllBehaviours", onRGetAllBehaviours);
        mSocket.on("RTryAddDog", onRTryAddDog);
        mSocket.emit("getAllBehaviours");

        // Gestion vue + gestion activity's state
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog);

        // Gestion gender
        Spinner Ssex = (Spinner) findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Ssex.setAdapter(adapter);

        SeekBar ageControl = (SeekBar) findViewById(R.id.ageDogSeek);
        SeekBar sizeControl = (SeekBar) findViewById(R.id.sizeDogSeek);

        final TextView ETage = (TextView) findViewById(R.id.age);
        ETage.setText("0");

        final TextView ETsize = (TextView) findViewById(R.id.size);
        ETsize.setText("1");

        ageControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            double progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = ((double) progress) / 10;
                ETage.setText(String.valueOf(progressChanged));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sizeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress + 1;
                ETsize.setText(String.valueOf(progressChanged));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void tryAddDog(View view) throws JSONException {
        //Définition des filtres
        EditTextFilter[] filterNumber = {new SizeFilter(0,3), new NumberFilter()}; //pour les champs age et taille
        EditTextFilter[] filterSize = {new SizeFilter()};

        //Récupération des valeurs
        EditText ETname = (EditText) findViewById(R.id.name);
        TextView ETage = (TextView) findViewById(R.id.age);
        EditText ETbreed = (EditText) findViewById(R.id.breed);
        TextView ETsize = (TextView) findViewById(R.id.size);
        EditText ETGetalongwithMales = (EditText) findViewById(R.id.getAlongWithMales);
        EditText ETGetalongwithFemales = (EditText) findViewById(R.id.getAlongWithFemales);
        EditText ETGetalongwithKids = (EditText) findViewById(R.id.getAlongWithKids);
        EditText ETGetalongwithHumans = (EditText) findViewById(R.id.getAlongWithHumans);
        EditText ETDescription = (EditText) findViewById(R.id.description);
        Spinner Ssex = (Spinner) findViewById(R.id.gender);
        String gender = Ssex.getSelectedItem().toString();
        boolean sGender;
        if (gender.equals("Male")){
            sGender = true;
        }
        else{
            sGender = false;
        }

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

        //Breed
        ValidateMessage vmBreed = textValidator.validate(ETbreed, filterSize);
        if(!vmBreed.getValue()) {
            valid = false;
            int min = ((SizeFilter) filterSize[0]).getMin();
            int max = ((SizeFilter) filterSize[0]).getMax();
            ETbreed.setError(vmBreed.getError().toString() + " min: " + min + " max: " + max);
        }

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
        String age = ETage.getText().toString();
        int size = Integer.parseInt(ETsize.getText().toString());
        String Sbreed = ETbreed.getText().toString();
        String Sname = ETname.getText().toString();
        String Sgetalongwithmales = ETGetalongwithMales.getText().toString();
        String Sgetalongwithfemales = ETGetalongwithFemales.getText().toString();
        String Sgetalongwithkids = ETGetalongwithKids.getText().toString();
        String Sgetalongwithhumans = ETGetalongwithHumans.getText().toString();
        String Sdescription = ETDescription.getText().toString();

        //Test ajout d'un chien
        Dog dog = new Dog(mUser.getIdUser(), Sname, age, Sbreed, size, Sgetalongwithmales, Sgetalongwithfemales, Sgetalongwithkids, Sgetalongwithhumans, Sdescription, sGender, selectedBehaviours);
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
                    for (int i = 0; i < listBehaviour.length(); i++) {
                        JSONObject currentObj = null;
                        try {
                            currentObj = listBehaviour.getJSONObject(i);
                            int idBehaviour = currentObj.getInt("idBehaviour");
                            String description = currentObj.getString("description");
                            final Behaviour currentB = new Behaviour(idBehaviour, description);
                            CheckBox cb = new CheckBox(getApplicationContext());
                            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked)
                                        selectedBehaviours.add(currentB);
                                    else
                                        selectedBehaviours.remove(currentB);
                                }
                            });
                            cb.setText(currentB.getDescription());
                            cb.setId(currentB.getIdBehaviour());
                            cb.setTextColor(WifWafColor.BLACK);
                            actlayout = (LinearLayout) findViewById(R.id.layout);
                            actlayout.addView(cb, 12);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener onRTryAddDog = new Emitter.Listener() {
        @Override
        public void call(final Object... args)  {
            AddDogActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Intent getUserDog = new Intent(getApplicationContext(), UserDogsActivity.class);
                    startActivity(getUserDog);
                }
            });
        }
    };
}

