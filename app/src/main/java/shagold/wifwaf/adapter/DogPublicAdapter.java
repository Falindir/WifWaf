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
import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.manager.SocketManager;

public class DogPublicAdapter extends ArrayAdapter<Dog> {

    private Context c;
    private Socket mSocket;

    public DogPublicAdapter(Context context, List<Dog> dogs) {
        super(context, 0, dogs);
        c = context;
        mSocket = SocketManager.getMySocket();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_public_dog,parent, false);

        DogPublicViewHolder viewHolder = (DogPublicViewHolder) convertView.getTag();

        if(viewHolder == null){
            viewHolder = new DogPublicViewHolder();
            viewHolder.setName((TextView) convertView.findViewById(R.id.namePublicRowDog));
            viewHolder.setDescription((TextView) convertView.findViewById(R.id.descriptionPublicRowDog));
            viewHolder.setAvatar((ImageView) convertView.findViewById(R.id.avatarPublicRowDog));
            viewHolder.setGender((TextView) convertView.findViewById(R.id.genderPublicRowDog));
            convertView.setTag(viewHolder);
        }

        final Dog dog = getItem(position);
        viewHolder.getName().setText(dog.getName());
        viewHolder.getDescription().setText(dog.getDescription());
        viewHolder.getAvatar().setImageBitmap(dog.getPhotoBitmap());
        if(dog.isMale())
            viewHolder.getGender().setText(getContext().getString(R.string.male));
        else
            viewHolder.getGender().setText(getContext().getString(R.string.female));


        return convertView;
    }
}
