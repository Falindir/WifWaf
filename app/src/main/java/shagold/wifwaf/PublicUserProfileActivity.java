package shagold.wifwaf;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import shagold.wifwaf.dataBase.User;

public class PublicUserProfileActivity extends AppCompatActivity {

    private TextView userProfileName;
    private TextView userProfileMail;
    private TextView userProfileBirthday;
    private TextView userProfilePhoneNumber;
    private TextView userProfileDescription;
    private User defUser =  new User("adresse@gmail.com", "A", "B", "2001-10-12", 123, "J'aime beaucoup les animaux!", null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_user_profile);

        userProfileName = (TextView) findViewById(R.id.userProfileName);
        userProfileName.setText(defUser.getNickname());

        userProfileMail = (TextView) findViewById(R.id.userProfileMail);
        userProfileMail.setText(Html.fromHtml("<a href=mailto:" + defUser.getEmail() + ">" + defUser.getEmail() + "</a>"));

        userProfileBirthday = (TextView) findViewById(R.id.userProfileBirthday);
        userProfileBirthday.setText(defUser.getBirthday());

        userProfileDescription = (TextView) findViewById(R.id.userProfileDescription);
        userProfileDescription.setText(defUser.getDescription());

        userProfilePhoneNumber = (TextView) findViewById(R.id.userProfilePhoneNumber);
        userProfilePhoneNumber.setText(Html.fromHtml("<a href=tel:" + Integer.toString(defUser.getPhoneNumber()) + ">" + Integer.toString(defUser.getPhoneNumber()) + "</a>"));

        userProfileMail.setMovementMethod(LinkMovementMethod.getInstance());
        userProfilePhoneNumber.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
