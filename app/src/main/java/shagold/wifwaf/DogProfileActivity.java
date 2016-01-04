package shagold.wifwaf;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import shagold.wifwaf.dataBase.Behaviour;
import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.tool.WifWafColor;
import shagold.wifwaf.view.TextValidator;
import shagold.wifwaf.view.ValidateMessage;
import shagold.wifwaf.view.filter.text.EditTextFilter;
import shagold.wifwaf.view.filter.text.NumberFilter;
import shagold.wifwaf.view.filter.text.SizeFilter;

public class DogProfileActivity extends AppCompatActivity {

    private Dog dog;
    private ArrayList<Behaviour> selectedBehaviours = new ArrayList<Behaviour>();
    private Socket mSocket;
    private LinearLayout actlayout;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int ACTION_SELECT_PICTURE = 2;

    ImageView mImageView;
    Bitmap imageBitmap;
    String bitmapImagedata = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_profile);

        mSocket = SocketManager.getMySocket();
        mSocket.emit("getAllBehaviours");
        mSocket.on("RUpdateDog", onRUpdateDog);
        mSocket.on("RGetAllBehaviours", onRGetAllBehaviours);

        dog = (Dog) getIntent().getSerializableExtra("DOG");

        // Remplissage des champs pour modif
        initFields();

        // Gestion gender
        Spinner Ssex = (Spinner) findViewById(R.id.dogProfileGender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Ssex.setAdapter(adapter);

        if(dog.isMale())
            Ssex.setSelection(adapter.getPosition("Male"));
        else
            Ssex.setSelection(adapter.getPosition("Female"));

        // Gestion photo
        mImageView = (ImageView) findViewById(R.id.avatarDog);
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

        ImageView dogProfile = (ImageView) findViewById(R.id.avatarDog);
        dogProfile.setImageBitmap(dog.getPhotoBitmap());
        bitmapImagedata = dog.getPhoto();

        EditText dogDescription = (EditText) findViewById(R.id.dogProfileDescription);
        dogDescription.setText(dog.getDescription());

        EditText dogBreed = (EditText) findViewById(R.id.dogProfilebreed);
        dogBreed.setText(dog.getBreed());

        final TextView dogAgeValue = (TextView) findViewById(R.id.dogAgeProfileValue);
        String age = String.valueOf(Double.parseDouble(dog.getAge()));
        dogAgeValue.setText(age);
        SeekBar ageControl = (SeekBar) findViewById(R.id.ageDogProfileSeek);
        int ageV = (int) (Double.parseDouble(dog.getAge()) * 10);
        ageControl.setProgress(ageV);
        ageControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            double progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = ((double) progress) / 10;
                dogAgeValue.setText(String.valueOf(progressChanged));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final TextView dogSizeValue = (TextView) findViewById(R.id.dogSizeProfileValue);
        dogSizeValue.setText(Integer.toString(dog.getSize()));
        SeekBar sizeControl = (SeekBar) findViewById(R.id.sizeDogProfileSeek);
        sizeControl.setProgress(dog.getSize() - 1);
        sizeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress + 1;
                dogSizeValue.setText(String.valueOf(progressChanged));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


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
        TextView ETage = (TextView) findViewById(R.id.dogAgeProfileValue);
        EditText ETbreed = (EditText) findViewById(R.id.dogProfilebreed);
        TextView ETsize = (TextView) findViewById(R.id.dogSizeProfileValue);
        EditText ETGetalongwithMales = (EditText) findViewById(R.id.dogProfilegetAlongWithMales);
        EditText ETGetalongwithFemales = (EditText) findViewById(R.id.dogProfilegetAlongWithFemales);
        EditText ETGetalongwithKids = (EditText) findViewById(R.id.dogProfilegetAlongWithKids);
        EditText ETGetalongwithHumans = (EditText) findViewById(R.id.dogProfilegetAlongWithHumans);
        EditText ETDescription = (EditText) findViewById(R.id.dogProfileDescription);
        Spinner Ssex = (Spinner) findViewById(R.id.dogProfileGender);
        String gender = Ssex.getSelectedItem().toString();

        boolean sGender = gender.equals("male");

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

        //Update d'un chien
        Dog updatedDog = new Dog(dog.getIdDog(), dog.getIdUser(), Sname, age, Sbreed, size, Sgetalongwithmales, Sgetalongwithfemales, Sgetalongwithkids, Sgetalongwithhumans, Sdescription, sGender, selectedBehaviours, bitmapImagedata);
        JSONObject jsonDog = updatedDog.toJsonWithId();
        mSocket.emit("updateDog", jsonDog);
        System.out.println("jsondog" + jsonDog);
    }

    public void takePic(View view){
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    public void selectPic(View view){
        //Préparation du bouton d'exploration de fichiers
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        String title = getString(R.string.select_picture_from_explorer);
        startActivityForResult(Intent.createChooser(intent,
                title), ACTION_SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            mImageView = (ImageView) findViewById(R.id.avatarDog);
            mImageView.setImageBitmap(imageBitmap);
            preparePhoto();
        }
        if (requestCode == ACTION_SELECT_PICTURE && resultCode == RESULT_OK) {
            getFileFromPath(data);
        }
    }

    public void getFileFromPath(final Intent data) {
        runOnUiThread(new Runnable() {
            public void run() {
                Uri selectedImageUri = data.getData();
                BitmapFactory.Options bfOptions = new BitmapFactory.Options();

                InputStream stream = null;
                try {
                    stream = getContentResolver().openInputStream(selectedImageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap myImage = BitmapFactory.decodeStream(stream, null, bfOptions);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                myImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                myImage = Bitmap.createScaledBitmap(myImage, 204, 153, false);
                bitmapImagedata = Dog.encodeTobase64(myImage);

                mImageView.setImageBitmap(myImage);
            }
        });
    }

    public void preparePhoto(){
        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
        bfOptions.inTempStorage = new byte[32 * 1024];

        // On convertit l'image en tableau de BYTE
        bitmapImagedata = Dog.encodeTobase64(imageBitmap);
    }

    private Emitter.Listener onRGetAllBehaviours = new Emitter.Listener() {
        @Override
        public void call(final Object... args)  {
            DogProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray listBehaviour =  (JSONArray) args[0];

                    for (int i = 0; i < listBehaviour.length(); i++) {
                        JSONObject currentObj = null;
                        try {
                            currentObj = listBehaviour.getJSONObject(i);
                            int idBehaviour = currentObj.getInt("idBehaviour");
                            String desc = currentObj.getString("description");
                            final Behaviour currentB = new Behaviour(idBehaviour, desc);
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
                            for(Behaviour b:dog.getBehaviours()){
                                if (b.getIdBehaviour() == currentB.getIdBehaviour()){
                                    cb.setChecked(true);
                                }
                            }
                            actlayout = (LinearLayout) findViewById(R.id.layoutDogProfile);
                            actlayout.addView(cb, 16);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

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
