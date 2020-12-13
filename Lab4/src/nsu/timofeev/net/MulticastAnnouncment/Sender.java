package nsu.timofeev.net.MulticastAnnouncment;

import me.ippolitov.fit.snakes.SnakesProto;
import nsu.timofeev.net.MessageListener;
import nsu.timofeev.net.PlayerNode;
import nsu.timofeev.util.BytesConverter;

import java.io.IOException;
import java.net.*;

public class Sender {

    private MulticastSocket socket;
    private InetAddress group;
    private byte[] buf;
    private PlayerNode node;

    public Sender(PlayerNode node) throws IOException {
        socket = new MulticastSocket();
        this.node = node;
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

    public void recvJoin() throws IOException, ClassNotFoundException {
        byte[] buf = new byte[1024];
        socket.setSoTimeout(1);
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
            var msg = (SnakesProto.GameMessage) BytesConverter.getObject(packet.getData());
            if (msg.hasJoin()) {
                node.getAck(packet);
            }
        } catch (Exception e) {}
    }

    public void stop() {
        socket.close();
    }

}
