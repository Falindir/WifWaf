package shagold.wifwaf.adapter;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class WalkViewHolder {

    private TextView title;
    private TextView description;
    private ImageView avatar;
    private ImageButton button;
    private TextView city;
    private TextView date;
    private TextView time;

    public WalkViewHolder() {}

    public TextView getDescription() {
        return description;
    }

    public void setDescription(TextView description) {
        this.description = description;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
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

    public TextView getCity() {
        return city;
    }

    public void setCity(TextView city) {
        this.city = city;
    }

    public TextView getDate() {
        return date;
    }

    public TextView getTime() {
        return time;
    }

    public void setDate(TextView date) {
        this.date = date;
    }

    public void setTime(TextView time) {
        this.time = time;
    }
}
