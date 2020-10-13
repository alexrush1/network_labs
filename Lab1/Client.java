import java.net.SocketAddress;

public class Client {
    private long timeout;
    private SocketAddress sock;

    public Client(SocketAddress sock) {
        this.sock = sock;
    }

    public void timeoutClock(long time) {
        timeout += time;
    }

    public void cleanTimer() {
        timeout = 0;
    }

    public long getTimeout() {
        return timeout;
    }

    public SocketAddress getSock() {
        return sock;
    }
}
