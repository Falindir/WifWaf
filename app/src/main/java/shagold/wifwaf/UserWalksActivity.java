package shagold.wifwaf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import java.util.List;

import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.list.WalkAdapter;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.manager.SocketManager;

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

        mSocket.on("RGetAllMyWalks", onRGetAllMyWalks);
        mSocket.emit("getAllMyWalks", mUser.getIdUser());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuManager.defaultMenu(this, item) || super.onOptionsItemSelected(item);
    }

    public void newWalk(View view){
        final Intent activityAddWalk = new Intent(getApplicationContext(), AddWalkActivity.class);
        startActivity(activityAddWalk);
    }

    private Emitter.Listener onRGetAllMyWalks = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            UserWalksActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<Walk> walks = Walk.generateWalksFromJSON((JSONArray) args[0]);
                    WalkAdapter adapter = new WalkAdapter(UserWalksActivity.this, walks);
                    mListView.setAdapter(adapter);
                }
            });
        }
    };
}
