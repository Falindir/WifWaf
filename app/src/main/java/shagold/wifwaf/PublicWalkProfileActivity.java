package shagold.wifwaf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import shagold.wifwaf.adapter.ParticipantAdapter;
import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.dataBase.Participant;
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
    private ArrayList<Dog> dogWalk = new ArrayList<Dog>();
    private AlertDialog dogsDialog;
    private List<Dog> dogsUser = new ArrayList<Dog>();
    private List<Dog> dogsUserForWalk = new ArrayList<Dog>();
    ArrayList<Participant> participants = new ArrayList<Participant>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_walk_profile);

        mSocket = SocketManager.getMySocket();

//        mSocket.emit("getUserById", walk.getIdUser());
        mUser = SocketManager.getMyUser();
        mSocket.on("RGetAllMyDogs", onRGetAllMyDogs);
        mSocket.emit("getAllMyDogs", mUser.getIdUser());


        walk = (Walk) getIntent().getSerializableExtra("WALK");

        // Récupération liste de chiens
        for(Dog d : walk.getDogs()) {
            System.out.println("ID-DOG- " + d.getIdDog());
            mSocket.emit("getDogById", d.getIdDog());
        }

        mSocket.on("RGetDogById", onRGetDogById);

        //Récupération liste de participants
        mSocket.emit("getAllParticipationsForIdWalk", walk.getIdWalk());
        mSocket.on("RgetAllParticipationsForIdWalk", onRGetParticipants);

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

    public void sendNotif(View view) throws JSONException {
        // Récup id communes pour tous les chiens
        final int idWalk = walk.getIdWalk();
        final int idUser = mUser.getIdUser();

        // Récup des id des chiens
        CharSequence[] items = new CharSequence[dogsUser.size()];
        final Dog[] dogTab = new Dog[dogsUser.size()];

        int i = 0;
        for(Dog dog : dogsUser) {
            items[i] = dog.getName();
            dogTab[i] = dog;
            i++;
        }

        dogsDialog = new AlertDialog.Builder(PublicWalkProfileActivity.this)
                .setTitle("My dogs")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            dogsUserForWalk.add(dogTab[indexSelected]);
                        } else if (dogsUserForWalk.contains(dogTab[indexSelected])) {
                            dogsUserForWalk.remove(dogTab[indexSelected]);
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        JSONArray myJson = new JSONArray();

                        for (Dog d: dogsUserForWalk) {
                            //pour chaque chien choisi
                            JSONObject currentDog = new JSONObject();
                            try {
                                currentDog.put("idWalk", idWalk);
                                currentDog.put("idUser", idUser);
                                currentDog.put("idDog", d.getIdDog());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            myJson.put(currentDog);
                        }
                        mSocket.emit("addParticipation", myJson);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dogsUserForWalk.clear();
                    }
                }).create();
        dogsDialog.show();
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

    public void getParticipants(View view){
        AlertDialog.Builder participantsDialog = new AlertDialog.Builder(PublicWalkProfileActivity.this);

        participantsDialog.setTitle("Participants");

        List<Participant> parts = new ArrayList<>(participants);

        ParticipantAdapter adapter = new ParticipantAdapter(PublicWalkProfileActivity.this, parts);

        final ListView modeList = new ListView(PublicWalkProfileActivity.this);
        modeList.setAdapter(adapter);
        modeList.setDividerHeight(modeList.getDividerHeight()*3);

        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Participant p = (Participant) modeList.getItemAtPosition(position);
                Intent userProfile = new Intent(getApplicationContext(), PublicUserProfileActivity.class);
                userProfile.putExtra("USER", p.getUser().getIdUser());
                startActivity(userProfile);
            }
        });

        participantsDialog.setView(modeList);

        AlertDialog alertDogs = participantsDialog.create();
        alertDogs.show();


    }

    public void walkDogs(View view) {
        AlertDialog.Builder userDogsDialog = new AlertDialog.Builder(PublicWalkProfileActivity.this);

        userDogsDialog.setTitle("Walk Dogs");

        List<Dog> dogs = new ArrayList<Dog>(dogWalk);
        DogPublicAdapter adapter = new DogPublicAdapter(PublicWalkProfileActivity.this, dogs);

        final ListView modeList = new ListView(PublicWalkProfileActivity.this);
        modeList.setAdapter(adapter);
        modeList.setDividerHeight(modeList.getDividerHeight()*3);

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

    private Emitter.Listener onRGetParticipants = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            PublicWalkProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray param = (JSONArray) args[0];
                    List<Participant> participantsParam = Participant.generateParticipantsFromJson(param);
                    participants.addAll(participantsParam); //TODO afficher correctement
                    if(!participants.isEmpty()){
                        Button buttonPariticpants = (Button) findViewById(R.id.participation);
                        buttonPariticpants.setVisibility(View.VISIBLE);
                    }
                    System.out.println(participants);
                }
            });
        }
    };

    private Emitter.Listener onRGetAllMyDogs = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            PublicWalkProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dogsUser = Dog.generateDogsFromJson((JSONArray) args[0]);

                }

            });
        }

    };
}
