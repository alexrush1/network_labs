package nsu.timofeev.net.MulticastAnnouncment;

import me.ippolitov.fit.snakes.SnakesProto;
import nsu.timofeev.util.BytesConverter;

import java.io.IOException;
import java.net.*;

public class Sender {

    private MulticastSocket socket;
    private InetAddress group;
    private byte[] buf;

    public Sender() throws IOException {
        socket = new MulticastSocket();
        group = InetAddress.getByName("239.192.0.4");
        socket.joinGroup(group);
    }

    public void send(SnakesProto.GameMessage.AnnouncementMsg msg) throws IOException {
        byte[] buf = BytesConverter.getBytes(msg);

        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 9192);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        socket.close();
    }

}
