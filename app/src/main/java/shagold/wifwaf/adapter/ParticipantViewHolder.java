package shagold.wifwaf.adapter;

import android.widget.ImageView;
import android.widget.TextView;

public class ParticipantViewHolder {

    private TextView userName;
    private TextView dogName;
    private ImageView avatar;

    public ParticipantViewHolder(){}

    public TextView getUserName(){ return this.userName; }

    public TextView getDogName(){ return this.dogName; }

    public void setUserName(TextView name){ this.userName = name; }

    public void setDogName(TextView name){ this.dogName = name; }

    public ImageView getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageView avatar) {
        this.avatar = avatar;
    }
}
