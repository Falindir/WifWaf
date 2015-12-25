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
import java.util.List;

import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.adapter.WalkAdapter;
import shagold.wifwaf.manager.SocketManager;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;

public class HomeActivity extends AppCompatActivity {

    private ListView mListView;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mSocket = SocketManager.getMySocket();
        mSocket.on("RGetAllWalks", onRGetAllWalks);
        mSocket.emit("getAllWalks");
        initListView();
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

    private Emitter.Listener onRGetAllWalks = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            HomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<Walk> walks = Walk.generateWalksFromJSON((JSONArray) args[0]);
                    WalkAdapter adapter = new WalkAdapter(HomeActivity.this, walks);
                    mListView.setAdapter(adapter);
                }

            });
        }

    };

}
