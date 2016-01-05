package shagold.wifwaf.adapter;

import android.content.Context;
import android.graphics.Typeface;
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
            viewHolder.setCity((TextView) convertView.findViewById(R.id.cityRowWalk));
            viewHolder.setDate((TextView) convertView.findViewById(R.id.dateRowWalk));
            viewHolder.setTime((TextView) convertView.findViewById(R.id.timeRowWalk));
            convertView.setTag(viewHolder);
        }

        final Walk walk = getItem(position);
        viewHolder.getTitle().setText(walk.getTitle());
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Kindergarten.ttf");
        viewHolder.getTitle().setTypeface(tf);
        viewHolder.getTitle().setTextSize(35.0f);
        viewHolder.getTitle().setTextColor(WifWafColor.BROWN);

        String desc = getContext().getString(R.string.description);
        viewHolder.getDescription().setText(desc + " : " + walk.getDescription());

        String city = getContext().getString(R.string.city);
        viewHolder.getCity().setText(city + " : " + walk.getCity());

        WifWafWalkDeparture departure = new WifWafWalkDeparture(walk.getDeparture());
        String date = getContext().getString(R.string.date_WA);
        viewHolder.getDate().setText(date + " : " + departure.getFormattedDate());
        String time = getContext().getString(R.string.time);
        viewHolder.getTime().setText(time + " : " + departure.getFormattedTime());

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
