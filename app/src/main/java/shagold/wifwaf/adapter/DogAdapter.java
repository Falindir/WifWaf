package shagold.wifwaf.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.manager.SocketManager;

public class DogAdapter extends ArrayAdapter<Dog> {

    private Context c;
    private Socket mSocket;

    public DogAdapter(Context context, List<Dog> dogs) {
        super(context, 0, dogs);
        c = context;
        mSocket = SocketManager.getMySocket();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_dog,parent, false);

        DogViewHolder viewHolder = (DogViewHolder) convertView.getTag();

        if(viewHolder == null){
            viewHolder = new DogViewHolder();
            viewHolder.setName((TextView) convertView.findViewById(R.id.nameRowDog));
            //viewHolder.setDescription((TextView) convertView.findViewById(R.id.descriptionRowDog));
            viewHolder.setAvatar((ImageView) convertView.findViewById(R.id.avatarRowDog));
            viewHolder.setButton((ImageButton) convertView.findViewById(R.id.deleteDogButton));
            convertView.setTag(viewHolder);
        }

        final Dog dog = getItem(position);
        viewHolder.getName().setText(dog.getName());
        //viewHolder.getDescription().setText(dog.getDescription());
        viewHolder.getAvatar().setImageResource(viewHolder.getDefaultAvatar());
        viewHolder.getButton().setFocusable(false);
        viewHolder.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(c);
                dialog.setTitle("Delete Dog");
                dialog.setMessage("Are you sure to delete the dog : \n\n\t" + dog.getName());
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int id = dog.getIdDog();
                        mSocket.emit("deleteDog", id);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDeleteDogs = dialog.create();
                alertDeleteDogs.show();
            }
        });
        return convertView;
    }
}
