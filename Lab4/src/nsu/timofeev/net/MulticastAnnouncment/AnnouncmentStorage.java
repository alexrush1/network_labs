package nsu.timofeev.net.MulticastAnnouncment;

import me.ippolitov.fit.snakes.SnakesProto;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class AnnouncmentStorage {
    private HashMap<InetSocketAddress, SnakesProto.GameMessage.AnnouncementMsg> msgArray;
    private ArrayList<InetSocketAddress> servers;
    public AnnouncmentStorage() {
        msgArray = new HashMap<>();
        servers = new ArrayList<>();
    };
    public HashMap<InetSocketAddress, SnakesProto.GameMessage.AnnouncementMsg> getMsgStorage() {return msgArray;}
    public ArrayList<InetSocketAddress> getServers() { return servers; }
}
