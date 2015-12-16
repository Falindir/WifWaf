package shagold.wifwaf;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.list.WalkAdapter;
import shagold.wifwaf.manager.SocketManager;

import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jimmy on 07/11/15.
 */
public class HomeActivity extends AppCompatActivity {

    private ListView mListView;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mSocket = SocketManager.getMySocket();
        mSocket.emit("getAllWalks");
        initListView();

        List<Walk> walks = generateWalks();
        WalkAdapter adapter = new WalkAdapter(this, walks);
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

    private List<Walk> generateWalks() {
        List<Walk> walks = new ArrayList<Walk>();
//(int id, int idDog, int idUser, String wN, String description, String city, String dep)
        walks.add(new Walk(0, 23, 10, "Balade 1", "Ma premiere balade !", "Montpellier", "desc"));
        walks.add(new Walk(1, 23, 10, "Balade 2", "Ma premiere balade !", "Montpellier", "desc"));
        walks.add(new Walk(2, 23, 10, "Balade 3", "Ma premiere balade !", "Montpellier", "desc"));
        walks.add(new Walk(3, 23, 10, "Balade 4", "Ma premiere balade !", "Montpellier", "desc"));
        walks.add(new Walk(4, 23, 10, "Balade 5", "Ma premiere balade !", "Montpellier", "desc"));
        walks.add(new Walk(5, 23, 10, "Balade 6", "Ma premiere balade !", "Montpellier", "desc"));
        walks.add(new Walk(6, 23, 10, "Balade 1", "Ma premiere balade !", "Montpellier", "desc"));
        walks.add(new Walk(7, 23, 10, "Balade 2", "Ma premiere balade !", "Montpellier", "desc"));
        walks.add(new Walk(8, 23, 10, "Balade 3", "Ma premiere balade !", "Montpellier", "desc"));
        walks.add(new Walk(9, 23, 10, "Balade 4", "Ma premiere balade !", "Montpellier", "desc"));
        walks.add(new Walk(10, 23, 10, "Balade 5", "Ma premiere balade !", "Montpellier", "desc"));
        walks.add(new Walk(11, 23, 10, "Balade 6", "Ma premiere balade !", "Montpellier", "desc"));

        return walks;
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.walkListView);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Walk walk = (Walk) mListView.getItemAtPosition(position);
                Intent clickedWalkProfile = new Intent(getApplicationContext(), PublicWalkProfileActivity.class);
                clickedWalkProfile.putExtra("WALK", walk);
                startActivity(clickedWalkProfile);
            }
        });
    }

}
