package nsu.timofeev.net.MulticastAnnouncment;

import me.ippolitov.fit.snakes.SnakesProto;
import nsu.timofeev.util.BytesConverter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Receiver implements Runnable{
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[1024];
    private ArrayList<InetSocketAddress> users;
    private ArrayList<Client> clients = new ArrayList<>();
    private InetAddress group;
    private long itterationTime;
    private long previousTime = 0;
    private AnnouncmentStorage storage;

    public Receiver(AnnouncmentStorage storage) throws IOException {
        group = InetAddress.getByName("239.192.0.4");
        socket = new MulticastSocket(9192);
        socket.joinGroup(group);
        this.storage = storage;
        users = storage.getServers();
    }

    @Override
    public void run() {
        try {
            InetSocketAddress pack;
            itterationTime = System.currentTimeMillis() - previousTime;
            previousTime = System.currentTimeMillis();

            for (int i = 0; i < clients.size(); i++) {
                clients.get(i).timeoutClock(itterationTime);
                if (clients.get(i).getTimeout() > 5000) {
                    users.remove(clients.get(i).getSock());
                    clients.remove(clients.get(i));
                    storage.getMsgStorage().remove(clients.get(i).getSock());
                }
            }

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                //socket.setSoTimeout(1000);
                socket.receive(packet);

                var ip = packet.getAddress();
                var port = packet.getPort();
                pack = new InetSocketAddress(ip, port);
                SnakesProto.GameMessage.AnnouncementMsg msg = (SnakesProto.GameMessage.AnnouncementMsg) BytesConverter.getObject(packet.getData());
                storage.getMsgStorage().put(pack, msg);
                System.out.println(pack);
                if (!users.contains(pack)) {
                    users.add(pack);
                    Client cli = new Client(pack);
                    clients.add(cli);
                } else {
                    for (Client c: clients) {
                        if (c.getSock().toString().equals(pack.toString())) {
                            c.cleanTimer();
                            break;
                        }
                    }
                }

            } catch (SocketTimeoutException e) { } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            //System.out.printf("\n\n\nOnline clients: (%d found)\n", clients.size());
            for (Client client : clients) {
                System.out.println(client.getSock().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() throws IOException {
        socket.leaveGroup(group);
        socket.close();
    }
}