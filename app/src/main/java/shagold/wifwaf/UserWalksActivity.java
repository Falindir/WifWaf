package shagold.wifwaf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.list.WalkAdapter;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.tool.WifWafColor;

/**
 * Created by jimmy on 13/12/15.
 */
public class UserWalksActivity extends AppCompatActivity {

    private Socket mSocket;
    private User mUser;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_walks);

        initListView();

        mSocket = SocketManager.getMySocket();
        mUser = SocketManager.getMyUser();

        //TODO fix in server
        //mSocket.on("RGetAllMyWalks", onRGetAllMyWalks);
        //mSocket.emit("getAllMyWalks", mUser.getIdUser());
        manualEvent();

        final Intent activityAddWalk = new Intent(getApplicationContext(), AddWalkActivity.class);

        Button addNewWalk = (Button) findViewById(R.id.addNewWalkButton);
        addNewWalk.setBackgroundColor(WifWafColor.BROWN_DARK);
        addNewWalk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                startActivity(activityAddWalk);
            }
        });

    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.walkUserListView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Walk walk = (Walk) mListView.getItemAtPosition(position);
                Intent clickedWalkProfile = new Intent(getApplicationContext(), WalkProfileActivity.class);
                clickedWalkProfile.putExtra("WALK", walk);
                startActivity(clickedWalkProfile);
            }
        });
    }

    private List<Walk> generateWalks(){
        List<Walk> walks = new ArrayList<Walk>();

        walks.add(new Walk(0, mUser.getIdUser(), "T1", "D1"));
        walks.add(new Walk(1, mUser.getIdUser(), "T2", "D2"));
        walks.add(new Walk(2, mUser.getIdUser(), "T3", "D3"));
        walks.add(new Walk(3, mUser.getIdUser(), "T4", "D4"));

        return walks;
    }

    private List<Walk> generateDogsFromJson(JSONArray json) {

        List<Walk> walks = new ArrayList<Walk>();

        if(json != null) {
            for (int i = 0; i < json.length(); i++) {
                JSONObject currentObj = null;
                try {
                    currentObj = json.getJSONObject(i);
                    Walk newWalk = new Walk(currentObj);
                    walks.add(newWalk);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return walks;
    }

    private Emitter.Listener onRGetAllMyWalks = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            UserWalksActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<Walk> walks = generateDogsFromJson((JSONArray) args[0]);
                    WalkAdapter adapter = new WalkAdapter(UserWalksActivity.this, walks);
                    mListView.setAdapter(adapter);
                }

            });
        }

    };

    private void manualEvent() {
        List<Walk> walks = generateWalks();
        WalkAdapter adapter = new WalkAdapter(UserWalksActivity.this, walks);
        mListView.setAdapter(adapter);
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
