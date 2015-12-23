package shagold.wifwaf;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import shagold.wifwaf.adapter.DogPublicAdapter;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.tool.WifWafWalkDeparture;

public class PublicWalkProfileActivity extends AppCompatActivity {

    private Walk walk;
    private Socket mSocket;
    private User mUser;
    private User SendToUser;
    private ArrayList<Dog> dogWalk = new ArrayList<Dog>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_walk_profile);

        //on veut récupérer les infos du user propriétaire de la balade pour pouvoir lui envoyer un sms
        mSocket = SocketManager.getMySocket();
//        mSocket.emit("getUserById", walk.getIdUser());
        mUser = SocketManager.getMyUser();
        mSocket.on("RGetUser", onRGetUser);

        walk = (Walk) getIntent().getSerializableExtra("WALK");

        for(Dog d : walk.getDogs()) {
            System.out.println("ID-DOG- " + d.getIdDog());
            mSocket.emit("getDogById", d.getIdDog());
        }

        mSocket.on("RGetDogById", onRGetDogById);

        TextView titleWalk = (TextView) findViewById(R.id.walkPublicTitle);
        titleWalk.setText(walk.getTitle());

        TextView cityWalk = (TextView) findViewById(R.id.walkPublicCity);
        cityWalk.setText(walk.getCity());

        TextView descriptionWalk = (TextView) findViewById(R.id.walkPublicDescription);
        descriptionWalk.setText(walk.getDescription());

        WifWafWalkDeparture departure = new WifWafWalkDeparture(walk.getDeparture());

        TextView dateDepartureWalk = (TextView) findViewById(R.id.walkPublicDateDeparture);
        dateDepartureWalk.setText(departure.getFormattedDate());

        TextView timeDepartureWalk = (TextView) findViewById(R.id.walkPublicTimeDeparture);
        timeDepartureWalk.setText(departure.getFormattedTime());
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
        Intent resultat = new Intent(PublicWalkProfileActivity.this, PublicUserProfileActivity.class);
        resultat.putExtra("USER", walk.getIdUser());
        startActivity(resultat);
    }

    public void sendNotif(View view){
        //SmsManager mySms = null;

        /*int hisAddress = SendToUser.getPhoneNumber();
        String hisAddressString =  Integer.toString(hisAddress);
        int myAddress = mUser.getPhoneNumber();
        String myAddressString = Integer.toString(myAddress);
        String text = "Hello " + mUser.getNickname() + ", the user " + SendToUser.getNickname() + " would like to join you on " + walk.getTitle() + ". " + "He will surely contact you soon" ;
        mySms.sendTextMessage(hisAddressString, myAddressString, text, null, null);*/

    }

    public void viewPath(View view) {
        Intent resultat = new Intent(PublicWalkProfileActivity.this, PublicPathProfileActivity.class);
        resultat.putExtra("WALK", walk);
        startActivity(resultat);
    }

    public void useWalk(View view) {
        Intent resultat = new Intent(PublicWalkProfileActivity.this, UseWalkActivity.class);
        resultat.putExtra("WALK", walk);
        startActivity(resultat);
    }

    public void walkDogs(View view) {
        AlertDialog.Builder userDogsDialog = new AlertDialog.Builder(PublicWalkProfileActivity.this);

        userDogsDialog.setTitle("Walk Dogs");

        List<Dog> dogs = new ArrayList<Dog>(dogWalk);
        DogPublicAdapter adapter = new DogPublicAdapter(PublicWalkProfileActivity.this, dogs);

        final ListView modeList = new ListView(PublicWalkProfileActivity.this);
        modeList.setAdapter(adapter);

        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Dog dog = (Dog) modeList.getItemAtPosition(position);
                Intent clickedWalkProfile = new Intent(getApplicationContext(), PublicDogProfileActivity.class);
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
            PublicWalkProfileActivity.this.runOnUiThread(new Runnable() {
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

    private Emitter.Listener onRGetUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            PublicWalkProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        SendToUser = new User((JSONObject) args[0]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    };
}
