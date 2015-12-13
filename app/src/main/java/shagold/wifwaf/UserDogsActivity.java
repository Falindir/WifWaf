package shagold.wifwaf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.tool.WifWafColor;
import shagold.wifwaf.list.DogAdapter;

public class UserDogsActivity extends AppCompatActivity {

    private Socket mSocket;
    private User mUser;
    private Button addDog;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dogs);
        initListView();

        mSocket = SocketManager.getMySocket();
        mUser = SocketManager.getMyUser();
        mSocket.on("RGetAllMyDogs", onRGetAllMyDogs);
        mSocket.emit("getAllMyDogs", mUser.getIdUser());
        mSocket.on("RdeleteDog", onRDeleteDog);

        final Intent activityAddDog = new Intent(getApplicationContext(), AddDogActivity.class);

        addDog = (Button) findViewById(R.id.addDogButton);
        addDog.setBackgroundColor(WifWafColor.BROWN_DARK);
        addDog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                startActivity(activityAddDog);
            }
        });
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

    private List<Dog> generateDogsFromJson(JSONArray dogsJSON) {

        List<Dog> dogs = new ArrayList<Dog>();

        if(dogsJSON != null) {
            for (int i = 0; i < dogsJSON.length(); i++) {
                JSONObject currentObj = null;
                try {
                    currentObj = dogsJSON.getJSONObject(i);
                    Dog newDog = new Dog(currentObj);
                    dogs.add(newDog);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return dogs;
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.dogListView);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Dog dog = (Dog) mListView.getItemAtPosition(position);
                Intent clickedDogProfile = new Intent(getApplicationContext(), DogProfileActivity.class);
                clickedDogProfile.putExtra("DOG", dog);
                startActivity(clickedDogProfile);
            }
        });
    }

    private Emitter.Listener onRGetAllMyDogs = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            UserDogsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<Dog> dogs = generateDogsFromJson((JSONArray) args[0]);
                    DogAdapter adapter = new DogAdapter(UserDogsActivity.this, dogs);
                    mListView.setAdapter(adapter);
                }

            });
        }

    };

    private Emitter.Listener onRDeleteDog = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            UserDogsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UserDogsActivity.this.recreate();
                }
            });
        }
    };
}
