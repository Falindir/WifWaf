package shagold.wifwaf;

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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.dataBase.Location;
import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.tool.WifWafColor;
import shagold.wifwaf.fragment.WifWafDatePickerFragment;
import shagold.wifwaf.fragment.WifWafTimePickerFragment;

public class UseWalkActivity extends AppCompatActivity {

    private Socket mSocket;
    private Walk walk;
    private User mUser;
    private ArrayList<Dog> dogChoice = new ArrayList<Dog>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_walk);

        walk = (Walk) getIntent().getSerializableExtra("WALK");
        initUseWalk();

        mSocket = SocketManager.getMySocket();
        mUser = SocketManager.getMyUser();
        mSocket.on("RGetAllMyDogs", onRGetAllMyDogs);
        mSocket.emit("getAllMyDogs", mUser.getIdUser());
        mSocket.on("RTryAddWalk", onRTryAddWalk);

    }

    private void initUseWalk() {
        if(walk != null) {
            EditText title = (EditText) findViewById(R.id.nameUseWalk);
            title.setText(walk.getTitle());
            EditText description = (EditText) findViewById(R.id.descriptionUseWalk);
            description.setText(walk.getDescription());
        }
    }

    private Emitter.Listener onRGetAllMyDogs = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            UseWalkActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray dogsJSON = (JSONArray) args[0];
                    List<Dog> userDogs = Dog.generateDogsFromJson(dogsJSON);
                    int index = 11;
                    for (Dog dog : userDogs) {
                        CheckBox cb = new CheckBox(UseWalkActivity.this);
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

                        LinearLayout layout = (LinearLayout) findViewById(R.id.useWalkLayout);
                        layout.addView(cb, index);
                        index++;
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

    public void choseTimeStamp(View view) {
        WifWafTimePickerFragment newFragment = new WifWafTimePickerFragment();
        TextView textView = (TextView) findViewById(R.id.timeStampUseWalk);
        newFragment.setTimeText(textView);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void choseDate(View view) {
        WifWafDatePickerFragment newFragment = new WifWafDatePickerFragment();
        TextView textView = (TextView) findViewById(R.id.dateUseWalk);
        newFragment.setDateText(textView);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void saveUseWalk(View view) {
        EditText titleET = (EditText) findViewById(R.id.nameUseWalk);
        String name = titleET.getText().toString();
        EditText descriptionET = (EditText) findViewById(R.id.descriptionUseWalk);
        String description = descriptionET.getText().toString();
        TextView timeTV = (TextView) findViewById(R.id.timeStampUseWalk);
        String time = timeTV.getText().toString();
        TextView dateTV = (TextView) findViewById(R.id.dateUseWalk);
        String date = dateTV.getText().toString();
        String departure = date + " " + time;

        Walk newWalk = new Walk(mUser.getIdUser(), name, description, walk.getCity(), departure, dogChoice);

        for(Location location : walk.getPath())
            newWalk.addLocationToWalk(location.getLattitude(), location.getLongitude());

        try {
            JSONObject walkJson = newWalk.toJson();
            mSocket.emit("TryAddWalk", walkJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener onRTryAddWalk = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            UseWalkActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent result = new Intent(UseWalkActivity.this, UserWalksActivity.class);
                    startActivity(result);
                }
            });
        }
    };
}
