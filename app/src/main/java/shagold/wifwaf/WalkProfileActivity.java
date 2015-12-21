package shagold.wifwaf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.list.DogPublicAdapter;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.view.filter.text.EditTextFilter;

public class WalkProfileActivity extends AppCompatActivity {

    private Walk walk;
    private Button useWalk;
    private Socket mSocket;
    private ArrayList<Dog> dogWalk = new ArrayList<Dog>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_profile);

        walk = (Walk) getIntent().getSerializableExtra("WALK");

        EditText walkTitle = (EditText) findViewById(R.id.walkTitle);
        walkTitle.setText(walk.getTitle());

        EditText walkDescription = (EditText) findViewById(R.id.walkDescription);
        walkDescription.setText(walk.getDescription());

        EditText walkCity = (EditText) findViewById(R.id.walkCity);
        walkCity.setText(walk.getCity());

        for(Dog d : walk.getDogs()) {
            mSocket = SocketManager.getMySocket();
            mSocket.emit("getDogById", d.getIdDog());
        }

        mSocket.on("RGetDogById", onRGetDogById);

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

    public void walkDogs(View view) {


        AlertDialog.Builder userDogsDialog = new AlertDialog.Builder(WalkProfileActivity.this);

        userDogsDialog.setTitle("Walk Dogs");

        List<Dog> dogs = new ArrayList<Dog>(dogWalk);
        DogPublicAdapter adapter = new DogPublicAdapter(WalkProfileActivity.this, dogs);

        final ListView modeList = new ListView(WalkProfileActivity.this);
        modeList.setAdapter(adapter);

        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Dog dog = (Dog) modeList.getItemAtPosition(position);
                Intent clickedWalkProfile = new Intent(getApplicationContext(), DogProfileActivity.class);
                clickedWalkProfile.putExtra("DOG", dog);
                startActivity(clickedWalkProfile);
            }
        });

        userDogsDialog.setView(modeList);

        AlertDialog alertDogs = userDogsDialog.create();
        alertDogs.show();

    }

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
}
