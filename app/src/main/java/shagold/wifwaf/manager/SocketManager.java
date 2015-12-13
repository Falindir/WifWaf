package shagold.wifwaf.manager;

import com.github.nkzawa.socketio.client.Socket;

import shagold.wifwaf.dataBase.User;

public class SocketManager {
    private static Socket mySocket;
    private static User myUser;

    public static synchronized Socket getMySocket(){
        return SocketManager.mySocket;
    }

    public static synchronized User getMyUser(){ return SocketManager.myUser; }

    public static synchronized void setMySocket(Socket s){
        SocketManager.mySocket = s;
    }

    public static synchronized void setMyUser(User myUser){ SocketManager.myUser = myUser;}

}
