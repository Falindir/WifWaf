package shagold.wifwaf.adapter;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import shagold.wifwaf.R;

public class DogViewHolder {

    private TextView name;
    private TextView description;
    private ImageView avatar;
    private ImageButton button;

    public DogViewHolder() {}

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getDescription() {
        return description;
    }

    public void setDescription(TextView description) {
        this.description = description;
    }

    public ImageView getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageView avatar) {
        this.avatar = avatar;
    }

    public ImageButton getButton() {
        return button;
    }

    public void setButton(ImageButton button) {
        this.button = button;
    }
}
