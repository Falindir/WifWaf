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
import shagold.wifwaf.dataBase.Participant;
import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.fragment.WifWafWalkChangeFragment;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.tool.WifWafColor;
import shagold.wifwaf.fragment.WifWafDatePickerFragment;
import shagold.wifwaf.fragment.WifWafTimePickerFragment;
import shagold.wifwaf.tool.WifWafWalkDeparture;
import shagold.wifwaf.view.ErrorMessage;
import shagold.wifwaf.view.TextValidator;
import shagold.wifwaf.view.ValidateMessage;
import shagold.wifwaf.view.filter.text.SizeFilter;
import shagold.wifwaf.view.filter.textview.PersonalizedBlankFilter;
import shagold.wifwaf.view.filter.textview.TextViewFilter;

public class WalkProfileActivity extends AppCompatActivity {

    private Walk walk;
    private User mUser;
    private Socket mSocket;
    private ArrayList<Dog> dogWalk = new ArrayList<Dog>();
    private List<Dog> userDogs = new ArrayList<Dog>();
    ArrayList<Participant> participants = new ArrayList<Participant>();
    private ArrayList<Participant> participantsWalkRefused = new ArrayList<Participant>();
    private ArrayList<Participant> participantsWalkAccepted = new ArrayList<Participant>();

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
        mSocket.on("RdeleteWalk", onRredirect);
        mSocket.on("RGetAllMyDogs", onRGetAllMyDogs);
        // redirection en cas de suppression ou de màj
        mSocket.on("RdeleteWalk", onRredirect);
        mSocket.on("RUpdateWalk", onRredirect);
        mSocket.emit("getAllMyDogs", mUser.getIdUser());
        mSocket.emit("getAllParticipationsForIdWalk", walk.getIdWalk());
        mSocket.on("RgetAllParticipationsForIdWalk", onRGetParticipants);

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
        dialog.setTitle(getString(R.string.delete_walk));
        dialog.setMessage(getString(R.string.sure_delete_walk) +": \n\n\t" + walk.getTitle());
        dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id = walk.getIdWalk();
                mSocket.emit("deleteWalk", id);
            }
        });
        dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
                    mSocket.off("RGetAllMyDogs");

                    List<Integer> dogWalkId = new ArrayList<Integer>();
                    for (Dog d : walk.getDogs()) {
                        dogWalkId.add(d.getIdDog());
                    }

                    JSONArray dogsJSON = (JSONArray) args[0];
                    userDogs = Dog.generateDogsFromJson(dogsJSON);
                    int index = 1;
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

                        LinearLayout layout = (LinearLayout) findViewById(R.id.checkboxDogs);
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

    private Emitter.Listener onRredirect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            WalkProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Intent getWalks = new Intent(getApplicationContext(), UserWalksActivity.class);
                    startActivity(getWalks);
                }
            });
        }
    };

    private boolean filter() {

        boolean result = true;

        TextValidator textValidator = new TextValidator();

        SizeFilter sizeFilter = new SizeFilter();

        TextViewFilter filterDate = new PersonalizedBlankFilter(ErrorMessage.DATE);
        TextViewFilter filterTime = new PersonalizedBlankFilter(ErrorMessage.TIME);

        EditText nameWalk = (EditText) findViewById(R.id.walkTitle);
        EditText descriptionWalk = (EditText) findViewById(R.id.walkDescription);

        TextView dateText = (TextView) findViewById(R.id.walkDateDeparture);
        TextView timeText = (TextView) findViewById(R.id.walkTimeDeparture);
        TextView date = (TextView) findViewById(R.id.walkDateDepartureMaster);
        TextView time = (TextView) findViewById(R.id.walkTimeDepartureMaster);
        TextView dogs = (TextView) findViewById(R.id.walkSelectDogs);

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

        vm = textValidator.validate(dateText, filterDate);
        if(!vm.getValue()) {
            date.setError(vm.getError().toString());
            result =  false;
        }
        else {
            date.setError(null);
        }

        vm = textValidator.validate(timeText, filterTime);
        if(!vm.getValue()) {
            time.setError(vm.getError().toString());
            result =  false;
        }
        else {
            time.setError(null);
        }

        if (dogWalk.size() == 0) {
            dogs.setError(getString(R.string.not_enough_dogs));
            result =  false;
        }
        else {
            dogs.setError(null);
        }

        return result;
    }

    public void useWalk(View view) {
        if(!filter())
            return;

        Intent result = new Intent(WalkProfileActivity.this, UseWalkActivity.class);
        Walk newWalk = getWalk();

        if(walk.equals(newWalk)) {
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

    public void saveChangeWalk(View view) throws JSONException {

        if(!filter())
            return;

        Walk newWalk = getWalk();

        if(newWalk.getDogs().size() == 0) {
            AlertDialog alertDialog = new AlertDialog.Builder(WalkProfileActivity.this).create();
            alertDialog.setTitle(getString(R.string.save));
            alertDialog.setMessage(getString(R.string.error_no_dogs_selected));
            alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            alertDialog.show();
            return;
        }

        //s'il y a des participants à accepter
        if(!participantsWalkAccepted.isEmpty()){
            System.out.println("J'accepte des participations");
            JSONArray listParticipantsAccepted = Participant.generateJsonArrayWithIdFromListParticipants(participantsWalkAccepted);
            mSocket.emit("validateParticipations", listParticipantsAccepted);
        }

        //s'il y a des participants à refuser
        if(!participantsWalkRefused.isEmpty()){
            System.out.println("Je refuse des participations");
            JSONArray listParticipantsRefused = Participant.generateJsonArrayWithIdFromListParticipants(participantsWalkRefused);
            mSocket.emit("refuseParticipations", listParticipantsRefused);
        }

        if(!walk.equals(newWalk)) {
            mSocket.emit("updateWalk", newWalk.toJsonWithId());
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

        Walk newWalk = new Walk(walk.getIdWalk(), walk.getIdUser(), name, description, walk.getCity(), departure, dogWalk);

        for(Location location : walk.getPath())
            newWalk.addLocationToWalk(location.getLattitude(), location.getLongitude());

        return newWalk;
    }

    private Emitter.Listener onRGetParticipants = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            WalkProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    LinearLayout layout = (LinearLayout) findViewById(R.id.checkboxParticipants);
                    JSONArray participantsJSON = (JSONArray) args[0];
                    ArrayList<Participant> participants = new ArrayList<Participant>();
                    participants.addAll(Participant.generateParticipantsFromJson(participantsJSON));

                    if (!Participant.allInvalid(participants)){
                        TextView tvparticipants = (TextView) findViewById(R.id.walkSelectParticipants);
                        tvparticipants.setVisibility(View.VISIBLE);
                    }

                    for (final Participant p : participants) {
                        if (p.getValid() == 0) {
                            TextView tvUser = new TextView(getApplicationContext());
                            tvUser.setText(getString(R.string.userName) + ": " + p.getUserName());
                            tvUser.setTextColor(WifWafColor.BLACK);
                            tvUser.setOnClickListener(new View.OnClickListener() {
                                                          @Override
                                                          public void onClick(View v) {
                                      Intent i = new Intent(getApplicationContext(), PublicUserProfileActivity.class);
                                      i.putExtra("USER", p.getUser().getIdUser());
                                      startActivity(i);
                                  }
                              }

                            );
                            layout.addView(tvUser, 1);


                            TextView tvDog = new TextView(getApplicationContext());
                            tvDog.setText(getString(R.string.dogName) + ": " + p.getDogName());
                            tvDog.setTextColor(WifWafColor.BLACK);
                            tvDog.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                         Intent i = new Intent(getApplicationContext(), PublicDogProfileActivity.class);
                                         i.putExtra("DOG", p.getDog());
                                         startActivity(i);
                                     }
                                 }

                            );
                            layout.addView(tvDog, 2);

                            //si la participation n'avait jamais été vue encore
                            CheckBox cb = new CheckBox(WalkProfileActivity.this);
                            cb.setText(getString(R.string.accept));
                            cb.setTextColor(WifWafColor.BLACK);
                            final Participant participantCB = p;
                            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked)
                                        participantsWalkAccepted.add(participantCB);
                                    else {
                                        participantsWalkAccepted.remove(participantCB);
                                    }
                                }
                            });
                            layout.addView(cb, 3);

                            CheckBox cbR = new CheckBox(WalkProfileActivity.this);
                            cbR.setText(getString(R.string.refuse));
                            cbR.setTextColor(WifWafColor.BLACK);
                            final Participant participantCBR = p;
                            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked)
                                        participantsWalkRefused.add(participantCBR);
                                    else {
                                        participantsWalkRefused.remove(participantCBR);
                                    }
                                }
                            });
                            layout.addView(cbR, 4);
                        }
                    }
                }
            });
        }
    };
}
