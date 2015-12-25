package shagold.wifwaf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import shagold.wifwaf.R;
import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.tool.WifWafColor;
import shagold.wifwaf.tool.WifWafWalkDeparture;

public class WalkAdapter extends ArrayAdapter<Walk> {

    private boolean privateRow;

    public WalkAdapter(Context context, List<Walk> walks) {
        super(context, 0, walks);
        privateRow = false;
    }

    public WalkAdapter(Context context, List<Walk> walks, boolean privateRow) {
        super(context, 0, walks);
        this.privateRow = privateRow;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_walk, parent, false);

        WalkViewHolder viewHolder = (WalkViewHolder) convertView.getTag();

        if(viewHolder == null){
            viewHolder = new WalkViewHolder();
            viewHolder.setTitle((TextView) convertView.findViewById(R.id.titleRowWalk));
            viewHolder.setDescription((TextView) convertView.findViewById(R.id.descriptionRowWalk));
            viewHolder.setAvatar((ImageView) convertView.findViewById(R.id.avatarRowWalk));
            viewHolder.setCity((TextView) convertView.findViewById(R.id.cityRowWalk));
            viewHolder.setDate((TextView) convertView.findViewById(R.id.dateRowWalk));
            viewHolder.setTime((TextView) convertView.findViewById(R.id.timeRowWalk));
            convertView.setTag(viewHolder);
        }

        // TODO fix warning with resource android <-- ?? pour les chaines qui sont en dur plus
        // TODO bas. Comme description ou city. Pour que ce soit correcte faut regarder
        // TODO comment ça marche avec des placeholders le ressources
        final Walk walk = getItem(position);
        viewHolder.getTitle().setText(walk.getTitle());
        viewHolder.getDescription().setText("Description : " + walk.getDescription());
        //TODO default value <-- ? c'est pour la photo je met de base une image par default
        viewHolder.getAvatar().setImageResource(R.drawable.user);
        viewHolder.getCity().setText("City " + " : " + walk.getCity());
        WifWafWalkDeparture departure = new WifWafWalkDeparture(walk.getDeparture());
        viewHolder.getDate().setText("Date : " + departure.getFormattedDate());
        viewHolder.getTime().setText("Time : " + departure.getFormattedTime());

        if(privateRow) {
            if(departure.isTooLate()) {
                viewHolder.getTitle().setTextColor(WifWafColor.GRAY_LIGHT);
                viewHolder.getDescription().setTextColor(WifWafColor.GRAY_LIGHT);
                viewHolder.getCity().setTextColor(WifWafColor.GRAY_LIGHT);
                viewHolder.getDate().setTextColor(WifWafColor.GRAY_LIGHT);
                viewHolder.getTime().setTextColor(WifWafColor.GRAY_LIGHT);
            }
            else {
                viewHolder.getTitle().setTextColor(WifWafColor.BLACK);
                viewHolder.getDescription().setTextColor(WifWafColor.BLACK);
                viewHolder.getCity().setTextColor(WifWafColor.BLACK);
                viewHolder.getDate().setTextColor(WifWafColor.BLACK);
                viewHolder.getTime().setTextColor(WifWafColor.BLACK);
            }
        }

        return convertView;
    }
}
