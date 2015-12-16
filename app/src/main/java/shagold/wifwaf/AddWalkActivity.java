package shagold.wifwaf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.list.DogAdapter;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.tool.WifWafColor;
import shagold.wifwaf.view.ErrorMessage;
import shagold.wifwaf.view.TextValidator;
import shagold.wifwaf.view.ValidateMessage;
import shagold.wifwaf.view.filter.text.EditTextFilter;
import shagold.wifwaf.view.filter.text.EmailFilter;
import shagold.wifwaf.view.filter.text.NumberFilter;
import shagold.wifwaf.view.filter.text.SizeFilter;

/**
 * Created by jimmy on 22/11/15.
 */
public class AddWalkActivity extends AppCompatActivity {

    private Socket mSocket;
    private User mUser;
    private JSONArray dogsJSON;
    private AlertDialog alertSelectDogs;
    private TextView dogsForWall;
    private int dogsSelectedNumber = 0;
    private List<Dog> dogChoise = new ArrayList<Dog>();
    private List<Dog> userDogs = new ArrayList<Dog>();
    private TextValidator textValidator = new TextValidator();
    private EditTextFilter[] filters = {new NumberFilter()};
    private SizeFilter sizeTitleFilter = new SizeFilter(1, 25);
    private SizeFilter sizeDescriptionFilter = new SizeFilter(10, 25);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_walk);

        mSocket = SocketManager.getMySocket();
        mUser = SocketManager.getMyUser();
        mSocket.on("RGetAllMyDogs", onRGetAllMyDogs);
        mSocket.emit("getAllMyDogs", mUser.getIdUser());

        initEditText();
        initAlertDialog();
        initConfirmButton();
    }

    private void initEditText() {
        dogsForWall = (TextView) findViewById(R.id.dogs_for_wal);
        dogsForWall.setText("No dogs");
        dogsForWall.setFocusable(false);
    }

    private void initConfirmButton(){

        final EditText nameWalk = (EditText) findViewById(R.id.nameWalk);
        final EditText descriptionWalk = (EditText) findViewById(R.id.descriptionWalk);

        Button confirmNewWalk = (Button) findViewById(R.id.addWalkButton);
        confirmNewWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                Log.d("Ts", ts);

                for (Dog d : dogChoise)
                    Log.d("dog", d.getName());

                boolean validText = true;

                ValidateMessage vm = textValidator.validate(nameWalk, filters);
                if (!vm.getValue()) {
                    validText = vm.getValue();
                    nameWalk.setError(vm.getError().toString());
                }
/*
                vm = textValidator.validate(nameWalk, sizeTitleFilter);
                if(!vm.getValue()) {
                    validText = vm.getValue();
                    nameWalk.setError(vm.getError().toString() + " : min - " + sizeTitleFilter.getMin() + " , max - " + sizeTitleFilter.getMax());
                }*/

                vm = textValidator.validate(descriptionWalk, filters);
                if (!vm.getValue()) {
                    validText = vm.getValue();
                    descriptionWalk.setError(vm.getError().toString());
                }

                vm = textValidator.validate(descriptionWalk, sizeDescriptionFilter);
                if (!vm.getValue()) {
                    validText = vm.getValue();
                    descriptionWalk.setError(vm.getError().toString() + " : min " + sizeDescriptionFilter.getMin() + " , max " + sizeDescriptionFilter.getMax());
                }

                final Intent actGPSWalk = new Intent(getApplicationContext(), GPSWalkActivity.class);
                if (dogsSelectedNumber > 0) {
                    if (validText) {
                        Walk walk = new Walk(dogChoise.get(0).getIdDog(), mUser.getIdUser(),nameWalk.getText().toString(), descriptionWalk.getText().toString(), "null", "");
                        actGPSWalk.putExtra("WALK", walk);
                        startActivity(actGPSWalk);
                    }
                } else {
                    dogsForWall.setError(ErrorMessage.BLANK.toString());
                }

            }
        });
    }

    private void initAlertDialog() {
        Button selectDogsForWalk = (Button) findViewById(R.id.selectDogsForWalk);
        selectDogsForWalk.setBackgroundColor(WifWafColor.BROWN_DARK);
        selectDogsForWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDogs = generateDogsFromJSON();
                List<String> dogsName = new ArrayList<String>();

                for (Dog dog : userDogs)
                    dogsName.add(dog.getName());

                final CharSequence[] dogsList = dogsName.toArray(new CharSequence[dogsName.size()]);

                AlertDialog.Builder multyChoiceDialog = new AlertDialog.Builder(AddWalkActivity.this);

                multyChoiceDialog.setTitle("Choice Dogs");

                boolean[] _selections = new boolean[dogsList.length];

                multyChoiceDialog.setMultiChoiceItems(dogsList, _selections, new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                    }
                });

                multyChoiceDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView list = ((AlertDialog) dialog).getListView();
                        List<String> dogsSelected = new ArrayList<String>();
                        for (int i = 0; i < list.getCount(); i++) {
                            boolean checked = list.isItemChecked(i);
                            if (checked)
                                dogsSelected.add(list.getItemAtPosition(i).toString());
                        }
                        updateItemDogs(dogsSelected);
                    }
                });

                multyChoiceDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                alertSelectDogs = multyChoiceDialog.create();
                alertSelectDogs.show();
            }
        });
    }

    private List<Dog> generateDogsFromJSON() {
        List<Dog> dogs = new ArrayList<Dog>();

        if(dogsJSON != null)
            for (int i = 0; i < dogsJSON.length(); i++) {
                JSONObject currentObj = null;
                try {
                    currentObj = dogsJSON.getJSONObject(i);
                    int idUser = currentObj.getInt("idUser");
                    String dogName = currentObj.getString("dogName");
                    int age = currentObj.getInt("age");
                    String breed = currentObj.getString("breed");
                    int size = currentObj.getInt("size");
                    String getAlongWithMales = currentObj.getString("getAlongWithMales");
                    String getAlongWithFemales = currentObj.getString("getAlongWithFemales");
                    String getAlongWithKids = currentObj.getString("getAlongWithKids");
                    String getAlongWithHumans = currentObj.getString("getAlongWithHumans");
                    String description = currentObj.getString("description");

                    dogs.add(new Dog(idUser, dogName, age, breed, size, getAlongWithMales, getAlongWithFemales, getAlongWithKids, getAlongWithHumans, description));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        return dogs;
    }

    private Emitter.Listener onRGetAllMyDogs = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            AddWalkActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dogsJSON = (JSONArray) args[0];
                }

            });
        }

    };

    private void updateItemDogs(List<String> list) {

        String result = "";

        if(list.size() == 0) {
           result = "No dogs";
            setDogNumber(0);
        }
        else {
            setDogNumber(list.size());
            dogChoise.clear();
            for(Dog dog : userDogs) {
                if(list.contains(dog.getName())) {
                    dogChoise.add(dog);
                }
            }

            for(String d : list)
                result += d + "\n";
        }

        dogsForWall.setText(result);
    }

    private void setDogNumber(int n) {
        dogsSelectedNumber = n;
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
}
