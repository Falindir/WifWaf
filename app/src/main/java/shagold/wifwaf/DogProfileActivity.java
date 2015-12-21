package shagold.wifwaf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.view.TextValidator;
import shagold.wifwaf.view.ValidateMessage;
import shagold.wifwaf.view.filter.text.EditTextFilter;
import shagold.wifwaf.view.filter.text.NumberFilter;
import shagold.wifwaf.view.filter.text.SizeFilter;

public class DogProfileActivity extends AppCompatActivity {

    private Dog dog;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_profile);

        mSocket = SocketManager.getMySocket();
        mSocket.on("RUpdateDog", onRUpdateDog);
        dog = (Dog) getIntent().getSerializableExtra("DOG");

        // Remplissage des champs pour modif
        initFields();

        // Gestion gender
        Spinner Ssex = (Spinner) findViewById(R.id.dogProfileGender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Ssex.setAdapter(adapter);
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

    public void initFields(){
        EditText dogName = (EditText) findViewById(R.id.dogProfileName);
        dogName.setText(dog.getName());

        // TODO default image

        ImageView dogProfile = (ImageView) findViewById(R.id.avatarDog);
        dogProfile.setImageResource(R.drawable.dogi2);

        EditText dogDescription = (EditText) findViewById(R.id.dogProfileDescription);
        dogDescription.setText(dog.getDescription());

        EditText dogAge = (EditText) findViewById(R.id.dogProfileAge);
        dogAge.setText(Integer.toString(dog.getAge()));

        EditText dogBreed = (EditText) findViewById(R.id.dogProfilebreed);
        dogBreed.setText(dog.getBreed());

        EditText dogSize = (EditText) findViewById(R.id.dogProfileSize);
        dogSize.setText(Integer.toString(dog.getSize()));

        EditText dogGAWM = (EditText) findViewById(R.id.dogProfilegetAlongWithMales);
        dogGAWM.setText(dog.getGetAlongWithMales());

        EditText dogGAWF = (EditText) findViewById(R.id.dogProfilegetAlongWithFemales);
        dogGAWF.setText(dog.getGetAlongWithFemales());

        EditText dogGAWH = (EditText) findViewById(R.id.dogProfilegetAlongWithHumans);
        dogGAWH.setText(dog.getGetAlongWithHumans());

        EditText dogGAWK = (EditText) findViewById(R.id.dogProfilegetAlongWithKids);
        dogGAWK.setText(dog.getGetAlongWithKids());
    }

    public void saveChangeDog(View view) throws JSONException {
        //Définition des filtres
        EditTextFilter[] filterNumber = {new SizeFilter(0,3), new NumberFilter()}; //pour les champs age et taille
        EditTextFilter[] filterSize = {new SizeFilter()};

        //Récupération des valeurs
        EditText ETname = (EditText) findViewById(R.id.dogProfileName);
        EditText ETage = (EditText) findViewById(R.id.dogProfileAge);
        EditText ETbreed = (EditText) findViewById(R.id.dogProfilebreed);
        EditText ETsize = (EditText) findViewById(R.id.dogProfileSize);
        EditText ETGetalongwithMales = (EditText) findViewById(R.id.dogProfilegetAlongWithMales);
        EditText ETGetalongwithFemales = (EditText) findViewById(R.id.dogProfilegetAlongWithFemales);
        EditText ETGetalongwithKids = (EditText) findViewById(R.id.dogProfilegetAlongWithKids);
        EditText ETGetalongwithHumans = (EditText) findViewById(R.id.dogProfilegetAlongWithHumans);
        EditText ETDescription = (EditText) findViewById(R.id.dogProfileDescription);
        Spinner Ssex = (Spinner) findViewById(R.id.dogProfileGender);
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

        //Update d'un chien
        Dog updatedDog = new Dog(dog.getIdDog(), dog.getIdUser(), Sname, age, Sbreed, size, Sgetalongwithmales, Sgetalongwithfemales, Sgetalongwithkids, Sgetalongwithhumans, Sdescription, sGender);
        JSONObject jsonDog = updatedDog.toJsonWithId();
        mSocket.emit("updateDog", jsonDog);
        System.out.println("jsondog" + jsonDog);
    }

    private Emitter.Listener onRUpdateDog = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            DogProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent result = new Intent(DogProfileActivity.this, UserDogsActivity.class);
                    startActivity(result);
                }
            });
        }

    };
}
