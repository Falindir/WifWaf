package shagold.wifwaf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.tool.WifWafColor;

/**
 * Created by jimmy on 22/11/15.
 */
public class WalkProfileActivity extends AppCompatActivity {

    private Walk walk;
    private Button useWalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_profile);

        walk = (Walk) getIntent().getSerializableExtra("WALK");

        System.out.println("WALK : " + walk.getTitle());

        useWalk = (Button) findViewById(R.id.useWalkButton);
        useWalk.setBackgroundColor(WifWafColor.BROWN_DARK);
        final Intent actGPSWalk = new Intent(getApplicationContext(), GPSWalkActivity.class);
        useWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(actGPSWalk);
            }
        });

        ImageButton deleteWalkButton = (ImageButton) findViewById(R.id.deleteWalkButton);
        deleteWalkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    public void redirectUserProfile(View view){
        Intent resultat = new Intent(WalkProfileActivity.this, PublicUserProfileActivity.class);
        startActivity(resultat);
    }
}
