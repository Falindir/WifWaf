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
import shagold.wifwaf.view.ErrorMessage;
import shagold.wifwaf.view.TextValidator;
import shagold.wifwaf.view.ValidateMessage;
import shagold.wifwaf.view.filter.text.EditTextFilter;
import shagold.wifwaf.view.filter.text.NumberFilter;
import shagold.wifwaf.view.filter.text.SizeFilter;
import shagold.wifwaf.view.filter.textview.PersonalizedBlankFilter;
import shagold.wifwaf.view.filter.textview.TextViewFilter;

public class AddWalkActivity extends AppCompatActivity {

    private Socket mSocket;
    private User mUser;
    private ArrayList<Dog> dogChoice = new ArrayList<Dog>();
    private List<Dog> userDogs = new ArrayList<Dog>();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_walk);

        //gestion de sockets
        mSocket = SocketManager.getMySocket();
        mUser = SocketManager.getMyUser();
        mSocket.on("RGetAllMyDogs", onRGetAllMyDogs);
        mSocket.emit("getAllMyDogs", mUser.getIdUser());

        //pour la création des intent
        context = this.getApplicationContext();
    }
    
    public void choseTimeStamp(View view) {
        WifWafTimePickerFragment newFragment = new WifWafTimePickerFragment();
        TextView textView = (TextView) findViewById(R.id.timeStampAddWalkMaster);
        newFragment.setTimeText(textView);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private Emitter.Listener onRGetAllMyDogs = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mSocket.off("RGetAllMyDogs");
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
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
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
        TextView textView = (TextView) findViewById(R.id.dateAddWalkMaster);
        newFragment.setDateText(textView);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private Walk getWalk() {
        EditText nameWalk = (EditText) findViewById(R.id.nameWalk);
        String nameW = nameWalk.getText().toString();
        EditText descriptionWalk = (EditText) findViewById(R.id.descriptionWalk);
        String descriptionW = descriptionWalk.getText().toString();
        TextView timeText = (TextView) findViewById(R.id.timeStampAddWalkMaster);
        String time = timeText.getText().toString();
        TextView dateText = (TextView) findViewById(R.id.dateAddWalkMaster);
        String date = dateText.getText().toString();
        String departure = date + " " + time;

        return new Walk(mUser.getIdUser(), nameW, descriptionW, "null", departure, dogChoice);
    }

    private boolean filter() {

        TextValidator textValidator = new TextValidator();

        SizeFilter sizeFilter = new SizeFilter();

        TextViewFilter filterDate = new PersonalizedBlankFilter(ErrorMessage.DATE);
        TextViewFilter filterTime = new PersonalizedBlankFilter(ErrorMessage.TIME);

        EditText nameWalk = (EditText) findViewById(R.id.nameWalk);
        EditText descriptionWalk = (EditText) findViewById(R.id.descriptionWalk);

        TextView dateText = (TextView) findViewById(R.id.dateAddWalkMaster);
        TextView timeText = (TextView) findViewById(R.id.timeStampAddWalkMaster);
        TextView date = (TextView) findViewById(R.id.dateAddWalkMaster);
        TextView time = (TextView) findViewById(R.id.timeStampAddWalkMaster);
        TextView dogs = (TextView) findViewById(R.id.selectDogs);

        boolean result = true;

        ValidateMessage vm = textValidator.validate(nameWalk, sizeFilter);
        if (!vm.getValue()) {
            int min = sizeFilter.getMin();
            int max = sizeFilter.getMax();
            nameWalk.setError(vm.getError().toString() + " min: " + min + " max: " + max);
            result = false;
        }

        vm = textValidator.validate(descriptionWalk, sizeFilter);
        if (!vm.getValue()) {
            int min = sizeFilter.getMin();
            int max = sizeFilter.getMax();
            descriptionWalk.setError(vm.getError().toString() + " min: " + min + " max: " + max);
            result =  false;
        }

        /*vm = textValidator.validate(dateText, filterDate);
        if(!vm.getValue()) {
            date.setError(vm.getError().toString());
            result =  false;
        }
        else {
            date.setError(null);
        }*/

        /*vm = textValidator.validate(timeText, filterTime);
        if(!vm.getValue()) {
            time.setError(vm.getError().toString());
            result =  false;
        }
        else {
            time.setError(null);
        }*/

        if (dogChoice.size() == 0) {
            dogs.setError(getString(R.string.not_enough_dogs));
            result =  false;
        }
        else {
            dogs.setError(null);
        }

        return result;
    }

    private void nextScreen(Intent i){
        if(!filter())
            return;

        Walk walk = getWalk();
        i.putExtra("WALK", walk);
        startActivity(i);
    }

    public void drawingWalk(View view) {
        Intent drawingWalk = new Intent(AddWalkActivity.this, DrawingWalkActivity.class);
        nextScreen(drawingWalk);
    }

    public void walkingWalk(View view) {
        Intent actGPSWalk = new Intent(getApplicationContext(), GPSWalkActivity.class);
        nextScreen(actGPSWalk);
    }
}
