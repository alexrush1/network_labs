import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        System.out.println("[PROXY]: SOCKS5 ONLY proxy starting on "+args[0]+" port");
        server.run(Integer.parseInt(args[0]));
    }
}
