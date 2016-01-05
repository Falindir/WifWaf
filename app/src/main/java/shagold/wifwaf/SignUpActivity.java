package shagold.wifwaf;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.fragment.WifWafDatePickerFragment;
import shagold.wifwaf.view.ErrorMessage;
import shagold.wifwaf.view.TextValidator;
import shagold.wifwaf.view.ValidateMessage;
import shagold.wifwaf.view.filter.text.EditTextFilter;
import shagold.wifwaf.view.filter.text.EmailFilter;
import shagold.wifwaf.view.filter.text.NumberFilter;
import shagold.wifwaf.view.filter.text.SizeFilter;
import shagold.wifwaf.view.filter.textview.PersonalizedBlankFilter;
import shagold.wifwaf.view.filter.textview.TextViewFilter;

public class SignUpActivity extends AppCompatActivity {

    private Socket mSocket;
    private User mUser;
    private ImageView mImageView;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int ACTION_SELECT_PICTURE = 2;
    Bitmap imageBitmap;
    String bitmapImagedata = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Ecoute evenement
        mSocket = SocketManager.getMySocket();
        mSocket.on("RTrySignUp", onRTrySignUp);

        mImageView = (ImageView) findViewById(R.id.imageviewSignUp);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
    }

    public void showDatePickerDialog(View v) {
        WifWafDatePickerFragment newFragment = new WifWafDatePickerFragment();
        TextView ETBirthday = (TextView) findViewById(R.id.BirthdayMaster);
        newFragment.setDateText(ETBirthday);
        newFragment.show(getSupportFragmentManager(), "datePicker");
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

    public void takePic(View view){
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
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

    public void trySignUp(View view) throws JSONException {
        //Définition des filtres
        EditTextFilter[] filterNumber = {new SizeFilter(0,9), new NumberFilter()}; //pour le champ numéro de téléphone
        EditTextFilter[] filterSize = {new SizeFilter()}; // pour les champs texte classiques
        EditTextFilter[] filterEmail = {new SizeFilter(), new EmailFilter()};
        TextViewFilter filterDate = new PersonalizedBlankFilter(ErrorMessage.DATE);

        // Récupération des valeurs
        EditText ETnickname = (EditText) findViewById(R.id.Nickname);
        EditText ETpassword = (EditText) findViewById(R.id.Password);
        EditText ETemail = (EditText) findViewById(R.id.Email);
        EditText ETPhoneNumber = (EditText) findViewById(R.id.PhoneNumber);
        TextView ETBirthday = (TextView) findViewById(R.id.BirthdayMaster);
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

        /*ValidateMessage vmBirthday = textValidator.validate(ETBirthday, filterDate);
        TextView birthday = (TextView) findViewById(R.id.BirthdayMaster);
        if(!vmBirthday.getValue()) {
            valid = false;
            birthday.setError(vmBirthday.getError().toString());
        }
        else {
            birthday.setError(null);
        }*/

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
        User user = new User(Semail,Snickname,Spassword,Sbirthday,SphoneNumber,Sdescription,bitmapImagedata);
        JSONObject jsonUser = user.toJson();
        mSocket.emit("TrySignUp", jsonUser);
    }

    private Emitter.Listener onRTrySignUp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            SignUpActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject param = (JSONObject) args[0];
                    try {
                        if((int)param.get("idUser") < 0) {
                           AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                            String error = getString(R.string.oups);
                            alertDialog.setTitle(error);
                            String email = getString(R.string.error_email_already_exists);
                            alertDialog.setMessage(email);
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
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

                            //Création/enregistrement token
                            Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
                            startService(intent);

                            startActivity(resultat);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
