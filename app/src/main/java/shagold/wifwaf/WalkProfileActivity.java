package shagold.wifwaf;

import android.app.AlertDialog;
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
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.dataBase.Location;
import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.fragment.WifWafWalkChangeFragment;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.tool.WifWafColor;
import shagold.wifwaf.fragment.WifWafDatePickerFragment;
import shagold.wifwaf.fragment.WifWafTimePickerFragment;
import shagold.wifwaf.tool.WifWafWalkComparator;
import shagold.wifwaf.tool.WifWafWalkDeparture;

public class WalkProfileActivity extends AppCompatActivity {

    private Walk walk;
    private User mUser;
    private Socket mSocket;
    private ArrayList<Dog> dogWalk = new ArrayList<Dog>();
    private List<Dog> userDogs = new ArrayList<Dog>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_profile);

        walk = (Walk) getIntent().getSerializableExtra("WALK");

        EditText walkTitle = (EditText) findViewById(R.id.walkTitle);
        walkTitle.setText(walk.getTitle());

        EditText walkDescription = (EditText) findViewById(R.id.walkDescription);
        walkDescription.setText(walk.getDescription());

        TextView walkCity = (TextView) findViewById(R.id.walkCity);
        walkCity.setText(walk.getCity());

        WifWafWalkDeparture departure = new WifWafWalkDeparture(walk.getDeparture());

        TextView dateDepartureWalk = (TextView) findViewById(R.id.walkDateDeparture);
        dateDepartureWalk.setText(departure.getFormattedDate());

        TextView timeDepartureWalk = (TextView) findViewById(R.id.walkTimeDeparture);
        timeDepartureWalk.setText(departure.getFormattedTime());

        mSocket = SocketManager.getMySocket();
        mUser = SocketManager.getMyUser();

        for(Dog d : walk.getDogs()) {
            mSocket.emit("getDogById", d.getIdDog());
        }

        mSocket.on("RGetDogById", onRGetDogById);
        mSocket.on("RGetAllMyDogs", onRGetAllMyDogs);
        mSocket.emit("getAllMyDogs", mUser.getIdUser());

    }

    public void viewPathWalk(View view) {
        Intent useWalk = new Intent(getApplicationContext(), PublicPathProfileActivity.class);
        useWalk.putExtra("WALK", walk);
        startActivity(useWalk);
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

    public void redirectUserProfile(View view){
        Intent resultat = new Intent(WalkProfileActivity.this, PublicUserProfileActivity.class);
        startActivity(resultat);
    }

    public void deleteWalk(View view) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(WalkProfileActivity.this);
        dialog.setTitle("Delete Walk");
        dialog.setMessage("Are you sure to delete the walk : \n\n\t" + walk.getTitle());
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id = walk.getIdWalk();
                //mSocket.emit("deleteWalk", id);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDeleteWalk = dialog.create();
        alertDeleteWalk.show();
    }

    private Emitter.Listener onRGetAllMyDogs = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            WalkProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    List<Integer> dogWalkId = new ArrayList<Integer>();
                    for (Dog d : walk.getDogs()) {
                        dogWalkId.add(d.getIdDog());
                    }

                    JSONArray dogsJSON = (JSONArray) args[0];
                    userDogs = Dog.generateDogsFromJson(dogsJSON);
                    int index = 9;
                    for (Dog dog : userDogs) {
                        CheckBox cb = new CheckBox(WalkProfileActivity.this);
                        cb.setText(dog.getName());
                        cb.setTextColor(WifWafColor.BLACK);

                        if (dogWalkId.contains(dog.getIdDog())) {
                            cb.setChecked(true);
                        }

                        final Dog dogCB = dog;
                        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked)
                                    dogWalk.add(dogCB);
                                else {
                                    Dog[] dw = new Dog[dogWalk.size()];
                                    for(int i = 0; i < dogWalk.size(); i++)
                                        dw[i] = dogWalk.get(i);

                                    for(int j = 0; j < dw.length; j++) {
                                        if(dw[j].getIdDog() == dogCB.getIdDog()) {
                                            dogWalk.remove(j);
                                        }
                                    }
                                }
                            }
                        });

                        LinearLayout layout = (LinearLayout) findViewById(R.id.walkProfileLayout);
                        layout.addView(cb, index);
                        index++;
                    }
                }
            });
        }
    };

    private Emitter.Listener onRGetDogById = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            WalkProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONArray param = (JSONArray) args[0];
                        Dog dog = new Dog(param.getJSONObject(0));
                        dogWalk.add(dog);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public void useWalk(View view) {
        Intent result = new Intent(WalkProfileActivity.this, UseWalkActivity.class);

        WifWafWalkComparator wc = new WifWafWalkComparator(walk);

        Walk newWalk = getWalk();

        if(wc.isSameWalk(newWalk)) {
            result.putExtra("WALK", walk);
            startActivity(result);
        }
        else {
            WifWafWalkChangeFragment newFragment = new WifWafWalkChangeFragment();
            newFragment.setWalk(newWalk);
            newFragment.show(getSupportFragmentManager(), "useWalk");
        }
    }

    public void showDatePickerDialog(View view) {
        WifWafDatePickerFragment newFragment = new WifWafDatePickerFragment();
        TextView TVDate = (TextView) findViewById(R.id.walkDateDeparture);
        newFragment.setDateText(TVDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View view) {
        WifWafTimePickerFragment newFragment = new WifWafTimePickerFragment();
        TextView TVTime = (TextView) findViewById(R.id.walkTimeDeparture);
        newFragment.setTimeText(TVTime);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void saveChangeWalk(View view) {

        WifWafWalkComparator wc = new WifWafWalkComparator(walk);

        Walk newWalk = getWalk();

        if(!wc.isSameWalk(newWalk)) {
            // TODO emit save
        }
        else {
            Intent result = new Intent(WalkProfileActivity.this, UserWalksActivity.class);
            startActivity(result);
        }
    }

    private Walk getWalk() {

        EditText walkTitle = (EditText) findViewById(R.id.walkTitle);
        String name = walkTitle.getText().toString();

        EditText walkDescription = (EditText) findViewById(R.id.walkDescription);
        String description = walkDescription.getText().toString();

        TextView dateDepartureWalk = (TextView) findViewById(R.id.walkDateDeparture);
        String date = dateDepartureWalk.getText().toString();

        TextView timeDepartureWalk = (TextView) findViewById(R.id.walkTimeDeparture);
        String time = timeDepartureWalk.getText().toString();

        String departure = date + " " + time;

        Walk newWalk = new Walk(walk.getIdUser(), name, description, walk.getCity(), departure, dogWalk);

        for(Location location : walk.getPath())
            newWalk.addLocationToWalk(location.getLattitude(), location.getLongitude());

        return newWalk;
    }
}
