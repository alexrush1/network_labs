package nsu.timofeev;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SMain {
    public static void main(String[] args) throws IOException {
        Server serv = new Server("4446");
        System.out.printf("Current address: %s\n", InetAddress.getLocalHost().getHostAddress());
        serv.process();
    }
}
