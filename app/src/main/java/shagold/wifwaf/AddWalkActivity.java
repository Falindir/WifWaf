package shagold.wifwaf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.tool.WifWafColor;
import shagold.wifwaf.fragment.WifWafDatePickerFragment;
import shagold.wifwaf.fragment.WifWafTimePickerFragment;
import shagold.wifwaf.view.TextValidator;
import shagold.wifwaf.view.ValidateMessage;
import shagold.wifwaf.view.filter.text.EditTextFilter;
import shagold.wifwaf.view.filter.text.NumberFilter;
import shagold.wifwaf.view.filter.text.SizeFilter;

public class AddWalkActivity extends AppCompatActivity {

    private User mUser;
    private ArrayList<Dog> dogChoice = new ArrayList<Dog>();
    private List<Dog> userDogs = new ArrayList<Dog>();
    private TextValidator textValidator = new TextValidator();
    private EditTextFilter[] filters = {new NumberFilter()};
    private SizeFilter sizeDescriptionFilter = new SizeFilter();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_walk);

        //gestion de sockets
        Socket mSocket = SocketManager.getMySocket();
        mUser = SocketManager.getMyUser();
        mSocket.on("RGetAllMyDogs", onRGetAllMyDogs);
        mSocket.emit("getAllMyDogs", mUser.getIdUser());

        //pour la création des intent
        context = this.getApplicationContext();
    }
    
    public void choseTimeStamp(View view) {
        WifWafTimePickerFragment newFragment = new WifWafTimePickerFragment();
        TextView textView = (TextView) findViewById(R.id.timeStampAddWalk);
        newFragment.setTimeText(textView);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private Emitter.Listener onRGetAllMyDogs = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            AddWalkActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray dogsJSON = (JSONArray) args[0];
                    userDogs = Dog.generateDogsFromJson(dogsJSON);
                    int positionCheckbox = 5;
                    for (Dog dog : userDogs) {
                        CheckBox cb = new CheckBox(AddWalkActivity.this);
                        cb.setText(dog.getName());
                        cb.setTextColor(WifWafColor.BLACK);
                        
                        final Dog dogCB = dog;
                        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked)
                                    dogChoice.add(dogCB);
                                else 
                                    dogChoice.remove(dogCB);
                            }
                        });

                        LinearLayout layout = (LinearLayout) findViewById(R.id.addWalkLayout);
                        layout.addView(cb, positionCheckbox);
                        positionCheckbox++;
                    }
                    if (userDogs.isEmpty()) {
                        //cet usr n'a aucun chien
                        new AlertDialog.Builder(AddWalkActivity.this)
                                .setTitle(getString(R.string.oups))
                                .setMessage(getString(R.string.no_dogs_for_now))
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(AddWalkActivity.this, AddDogActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }
                }
            });
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuManager.defaultMenu(this, item) || super.onOptionsItemSelected(item);
    }

    public void choseDate(View view) {
        WifWafDatePickerFragment newFragment = new WifWafDatePickerFragment();
        TextView textView = (TextView) findViewById(R.id.dateAddWalk);
        newFragment.setDateText(textView);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void drawingWalk(View view) {

        Intent drawingWalk = new Intent(AddWalkActivity.this, DrawingWalkActivity.class);

        EditText nameWalk = (EditText) findViewById(R.id.nameWalk);
        String nameW = nameWalk.getText().toString();
        EditText descriptionWalk = (EditText) findViewById(R.id.descriptionWalk);
        String descriptionW = descriptionWalk.getText().toString();
        TextView timeText = (TextView) findViewById(R.id.timeStampAddWalk);
        String time = timeText.getText().toString();
        TextView dateText = (TextView) findViewById(R.id.dateAddWalk);
        String date = dateText.getText().toString();
        String departure = date + " " + time;

        Walk walk = new Walk(mUser.getIdUser(), nameW, descriptionW, "null", departure, dogChoice);
        drawingWalk.putExtra("WALK", walk);
        startActivity(drawingWalk);
    }

    public void walkingWalk(View view) {
        final EditText nameWalk = (EditText) findViewById(R.id.nameWalk);
        final EditText descriptionWalk = (EditText) findViewById(R.id.descriptionWalk);

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
        if (dogChoice.size() > 0) {
            if (validText) {
                Walk walk = new Walk(mUser.getIdUser(), nameWalk.getText().toString(), descriptionWalk.getText().toString(), "null", "", dogChoice);
                TextView timeText = (TextView) findViewById(R.id.timeStampAddWalk);
                String time = timeText.getText().toString();
                TextView dateText = (TextView) findViewById(R.id.dateAddWalk);
                String date = dateText.getText().toString();
                walk.setDeparture(date + " " + time);
                actGPSWalk.putExtra("WALK", walk);
                startActivity(actGPSWalk);
            }
        } else {
            TextView dogs = (TextView) findViewById(R.id.selectDogs);
            dogs.setError(getString(R.string.not_enough_dogs));
        }
    }
}
