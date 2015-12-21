package shagold.wifwaf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.manager.MenuManager;
import shagold.wifwaf.tool.WifWafDatePickerFragment;
import shagold.wifwaf.tool.WifWafTimePickerFragment;

public class UseWalkActivity extends AppCompatActivity {

    private Walk walk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_walk);

        walk = (Walk) getIntent().getSerializableExtra("WALK");
        initUseWalk();

    }

    private void initUseWalk() {
        if(walk != null) {
            EditText title = (EditText) findViewById(R.id.nameUseWalk);
            title.setText(walk.getTitle());
            EditText description = (EditText) findViewById(R.id.descriptionUseWalk);
            description.setText(walk.getDescription());
        }
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

    public void choseTimeStamp(View view) {
        WifWafTimePickerFragment newFragment = new WifWafTimePickerFragment();
        TextView textView = (TextView) findViewById(R.id.timeStampUseWalk);
        newFragment.setTimeText(textView);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void choseDate(View view) {
        WifWafDatePickerFragment newFragment = new WifWafDatePickerFragment();
        TextView textView = (TextView) findViewById(R.id.dateUseWalk);
        newFragment.setDateText(textView);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void saveUseWalk(View view) {
        Intent result = new Intent(UseWalkActivity.this, UserWalksActivity.class);
        startActivity(result);
    }
}
