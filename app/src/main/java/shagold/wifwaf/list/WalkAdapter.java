package shagold.wifwaf.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Socket;

import java.util.List;

import shagold.wifwaf.R;
import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.manager.SocketManager;
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
            if(departure.isTooLate())
                convertView.setBackgroundColor(WifWafColor.GRAY_LIGHT);
            else
                convertView.setBackgroundColor(WifWafColor.WHITE);
        }

        return convertView;
    }
}
