
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Receiver {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
    private ArrayList<SocketAddress> users = new ArrayList<>();
    private ArrayList<Client> clients = new ArrayList<>();
    private InetAddress group;
    private long itterationTime;
    private long previousTime = 0;

    public Receiver() throws IOException {
        group = InetAddress.getByName("230.0.0.0");
        socket = new MulticastSocket(4446);
        socket.joinGroup(group);
    }

    public void run() {
        try {
                SocketAddress pack;
                itterationTime = System.currentTimeMillis() - previousTime;
                previousTime = System.currentTimeMillis();

                for (int i = 0; i < clients.size(); i++) {
                    clients.get(i).timeoutClock(itterationTime);
                    if (clients.get(i).getTimeout() > 5000) {users.remove(clients.get(i).getSock()); clients.remove(clients.get(i));}
                }

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.setSoTimeout(1000);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                pack = packet.getSocketAddress();

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

                //System.out.print("\033[H\033[2J");
                //System.out.flush();
                System.out.printf("\n\n\nOnline clients: (%d found)\n", clients.size());
            for (Client client : clients) {
                System.out.println(client.getSock());
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
