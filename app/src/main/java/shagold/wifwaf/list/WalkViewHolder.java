package shagold.wifwaf.list;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jimmy on 22/11/15.
 */
public class WalkViewHolder {

    private TextView title;
    private TextView description;
    private ImageView avatar;

    public WalkViewHolder() {
    }

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
}
