import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {

    private static ArrayList<Client> clientsArray = new ArrayList<>();

    public void run(int port) throws IOException {
        ServerSocket socket = new ServerSocket(port);
        socket.setSoTimeout(1);
        while (true) {
            try {
                Socket client = socket.accept();
                var newClient = new Client(client);
                newClient.connect();
                if(newClient.isConnected()) {
                    clientsArray.add(newClient);
                } else {
                    newClient.disconnect();
                }
            } catch (Exception e) {
            }

            Iterator<Client> clientIterator = clientsArray.iterator();

            while (clientIterator.hasNext()) {
                var client = clientIterator.next();
                try {
                    client.process();
                } catch (Exception e) {
                    client.disconnect();
                    clientIterator.remove();
                }
            }
        }

    }
}
