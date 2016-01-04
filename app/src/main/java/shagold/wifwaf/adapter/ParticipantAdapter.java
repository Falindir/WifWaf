package shagold.wifwaf.adapter;

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
import shagold.wifwaf.dataBase.Participant;
import shagold.wifwaf.manager.SocketManager;
import shagold.wifwaf.tool.WifWafColor;

public class ParticipantAdapter extends ArrayAdapter<Participant> {

    private Context c;
    private Socket mSocket;

    public ParticipantAdapter(Context context, List<Participant> participantList) {
        super(context, 0, participantList);
        c = context;
        mSocket = SocketManager.getMySocket();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_participant, parent, false);

        ParticipantViewHolder viewHolder = (ParticipantViewHolder) convertView.getTag();

        if(viewHolder == null){
            viewHolder = new ParticipantViewHolder();
            viewHolder.setAvatar((ImageView) convertView.findViewById(R.id.avatarRowParticipant));
            viewHolder.setDogName((TextView) convertView.findViewById(R.id.nameRowUser));
            viewHolder.setUserName((TextView) convertView.findViewById(R.id.nameRowDog));
            convertView.setTag(viewHolder);
        }

        final Participant participant = getItem(position);
        viewHolder.getAvatar().setImageBitmap(participant.getUser().getPhotoBitmap());
        viewHolder.getDogName().setText(participant.getUserName());
        viewHolder.getUserName().setText(participant.getDogName());

        if(participant.getValid() == 0) {
            viewHolder.getDogName().setTextColor(WifWafColor.GRAY_LIGHT);
            viewHolder.getUserName().setTextColor(WifWafColor.GRAY_LIGHT);
        }
        else if(participant.getValid() == 1) {
            viewHolder.getDogName().setTextColor(WifWafColor.GREEN_LIGHT);
            viewHolder.getUserName().setTextColor(WifWafColor.GREEN_LIGHT);
        }
        else if(participant.getValid() == 2) {
            viewHolder.getDogName().setTextColor(WifWafColor.RED_LIGHT);
            viewHolder.getUserName().setTextColor(WifWafColor.RED_LIGHT);
        }

        return convertView;
    }

}
