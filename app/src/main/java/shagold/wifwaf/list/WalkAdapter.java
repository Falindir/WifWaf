package shagold.wifwaf.list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Socket;

import java.util.List;

import shagold.wifwaf.R;
import shagold.wifwaf.dataBase.User;
import shagold.wifwaf.dataBase.Walk;
import shagold.wifwaf.manager.SocketManager;

/**
 * Created by jimmy on 22/11/15.
 */
public class WalkAdapter extends ArrayAdapter<Walk> {

    private Context c;
    private Socket mSocket;
    private User mUser;

    public WalkAdapter(Context context, List<Walk> walks) {
        super(context, 0, walks);
        c = context;
        mSocket = SocketManager.getMySocket();
        mUser = SocketManager.getMyUser();
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
            //viewHolder.setButton((ImageButton) convertView.findViewById(R.id.deleteWalkButton));
            viewHolder.setCity((TextView) convertView.findViewById(R.id.cityRowWalk));
            viewHolder.setLength((TextView) convertView.findViewById(R.id.lengthRowWalk));
            convertView.setTag(viewHolder);
        }

        final Walk walk = getItem(position);
        viewHolder.getTitle().setText(walk.getTitle());
        viewHolder.getDescription().setText(walk.getDescription());
        //viewHolder.getAvatar().setImageDrawable();

        viewHolder.getCity().setText(walk.getCity());
        //viewHolder.getLength().setText(String.valueOf(walk.getLength()));
        //TODO enlever length de la vue

        /* // TODO need fix probleme
        if(!walk.isCreator(mUser.getIdUser())) {
            System.out.println("INV : " + walk.getTitle());
            viewHolder.getButton().setVisibility(View.INVISIBLE);
        }*/

        // viewHolder.getButton().setFocusable(false);
        /*viewHolder.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(c);
                dialog.setTitle("Delete Walk");
                dialog.setMessage("Are you sure to delete the walk : \n\n\t" + walk.getTitle());
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int id = walk.getId();
                        //mSocket.emit("deleteWalk", id);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDeleteWalk = dialog.create();
                alertDeleteWalk.show();
            }
        });
*/
        return convertView;
    }

}
