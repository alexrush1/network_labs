import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Server {

    private static ArrayList<Client> clientsArray = new ArrayList<>();

    ServerSocket socket;

    public void run(int port) throws IOException {
        Scanner scanner = new Scanner(System.in);
        socket = new ServerSocket(port);
        socket.setSoTimeout(1);
        while (true) {
            if(System.in.available() != 0) {
                String s = scanner.nextLine();
                if(s.equals("stop")) break;
            }
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
        stop();
    }

    private void stop() throws IOException {
        try {
            for (var c: clientsArray) {
                c.disconnect();
            }
            socket.close();
            System.out.println("Proxy stopped");
        } catch (Exception e) {}
    }
}
