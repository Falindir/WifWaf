package shagold.wifwaf;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.manager.MenuManager;

/**
 * Created by jimmy on 22/11/15.
 */
public class DogProfileActivity extends AppCompatActivity {

    private Dog dog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_profile);

        dog = (Dog) getIntent().getSerializableExtra("DOG");

        EditText dogName = (EditText) findViewById(R.id.dogProfileName);
        dogName.setText(dog.getName());

        // TODO default image

        ImageView dogProfile = (ImageView) findViewById(R.id.avatarDog);
        dogProfile.setImageResource(R.drawable.dogi2);

        EditText dogDescription = (EditText) findViewById(R.id.dogProfileDescription);
        dogDescription.setText(dog.getDescription());

        Switch dogSex = (Switch) findViewById(R.id.dogProfileSex);
        if(dog.isMale())
            dogSex.setChecked(true);
        else
            dogSex.setChecked(false);

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
