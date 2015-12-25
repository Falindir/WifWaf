package shagold.wifwaf;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import shagold.wifwaf.dataBase.Behaviour;
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

        TextView ageDog = (TextView) findViewById(R.id.dogPublicProfileAge);
        String age = String.valueOf(Double.parseDouble(dog.getAge()));
        ageDog.setText(age);

        TextView sizeDog = (TextView) findViewById(R.id.dogPublicProfileSize);
        sizeDog.setText(Integer.toString(dog.getSize()));

        TextView breedDog = (TextView) findViewById(R.id.dogPublicProfileBreed);
        breedDog.setText(dog.getBreed());

        TextView description = (TextView) findViewById(R.id.dogPublicProfileDescription);
        description.setText(dog.getDescription());

        ImageView avatar = (ImageView) findViewById(R.id.avatarPublicDog);
        avatar.setImageResource(R.drawable.dogi2);

        TextView gender = (TextView) findViewById(R.id.genderDogPublicProfile);
        gender.setText(dog.getSex());

        TextView GetAlongWithMales = (TextView) findViewById(R.id.dogPublicProfileGetAlongWithMales);
        GetAlongWithMales.setText(dog.getGetAlongWithMales());

        TextView GetAlongWithFemales = (TextView) findViewById(R.id.dogPublicProfileGetAlongWithFemales);
        GetAlongWithFemales.setText(dog.getGetAlongWithFemales());

        TextView GetAlongWithKids = (TextView) findViewById(R.id.dogPublicProfileGetAlongWithKids);
        GetAlongWithKids.setText(dog.getGetAlongWithKids());

        TextView GetAlongWithHumans = (TextView) findViewById(R.id.dogPublicProfileGetAlongWithHumans);
        GetAlongWithHumans.setText(dog.getGetAlongWithHumans());

        TextView Behaviours = (TextView) findViewById(R.id.behaviour);
        String listB = "";
        for (Behaviour b : dog.getBehaviours()) {
            listB += " " + b.getDescription();
        }
        Behaviours.setText(listB);

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
