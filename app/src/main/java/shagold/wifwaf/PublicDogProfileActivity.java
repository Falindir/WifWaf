package shagold.wifwaf;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.manager.MenuManager;

public class PublicDogProfileActivity extends AppCompatActivity {

    private Dog dog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_dog_profile);

        dog = (Dog) getIntent().getSerializableExtra("DOG");

        TextView nameDog = (TextView) findViewById(R.id.namePublicDog);
        nameDog.setText(dog.getName());

        TextView description = (TextView) findViewById(R.id.dogPublicProfileDescription);
        description.setText(dog.getDescription());

        ImageView avatar = (ImageView) findViewById(R.id.avatarPublicDog);
        avatar.setImageResource(R.drawable.dogi2);

        TextView gender = (TextView) findViewById(R.id.genderDogPublicProfile);
        gender.setText(dog.getSexe());

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
