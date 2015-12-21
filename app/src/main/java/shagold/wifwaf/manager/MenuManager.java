package shagold.wifwaf.manager;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import shagold.wifwaf.HomeActivity;
import shagold.wifwaf.R;
import shagold.wifwaf.UserDogsActivity;
import shagold.wifwaf.UserProfileActivity;
import shagold.wifwaf.UserWalksActivity;

public class MenuManager {

    public static boolean emptyMenu(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings :
                return true;
            default:
                return false;
        }
    }

    public static boolean defaultMenu(Activity a, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings :
                return true;
            case R.id.action_home :
                final Intent home = new Intent(a.getApplicationContext(), HomeActivity.class);
                a.startActivity(home);
                return true;
            case R.id.action_profile :
                final Intent userprofile = new Intent(a.getApplicationContext(), UserProfileActivity.class);
                a.startActivity(userprofile);
                return true;
            case R.id.action_dogs :
                final Intent userDogs = new Intent(a.getApplicationContext(), UserDogsActivity.class);
                a.startActivity(userDogs);
                return true;
            case R.id.action_walks :
                final Intent userWalks = new Intent(a.getApplicationContext(), UserWalksActivity.class);
                a.startActivity(userWalks);
                return true;
            default:
                return false;
        }
    }
}
