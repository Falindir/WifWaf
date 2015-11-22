package shagold.wifwaf.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import shagold.wifwaf.R;
import shagold.wifwaf.dataBase.Walk;

/**
 * Created by jimmy on 22/11/15.
 */
public class WalkAdapter extends ArrayAdapter<Walk> {

    public WalkAdapter(Context context, List<Walk> walks) {
        super(context, 0, walks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_walk,parent, false);

        WalkViewHolder viewHolder = (WalkViewHolder) convertView.getTag();

        if(viewHolder == null){
            viewHolder = new WalkViewHolder();
            viewHolder.setTitle((TextView) convertView.findViewById(R.id.titleRowWalk));
            viewHolder.setDescription((TextView) convertView.findViewById(R.id.descriptionRowWalk));
            viewHolder.setAvatar((ImageView) convertView.findViewById(R.id.avatarRowWalk));
            convertView.setTag(viewHolder);
        }

        Walk walk = getItem(position);
        viewHolder.getTitle().setText(walk.getTitle());
        viewHolder.getDescription().setText(walk.getDescription());
        viewHolder.getAvatar().setImageDrawable(new ColorDrawable(walk.getDifficulty().getTemplate()));

        return convertView;
    }

}
