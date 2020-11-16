package treechat.model.storages;


import lombok.Getter;
import lombok.Setter;
import treechat.model.message.Message;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Getter
@Setter
public class Storages {
    private final BlockingDeque<Message> messagesToSend = new LinkedBlockingDeque<>();
    private final Deque<Message> pendingAcks = new ConcurrentLinkedDeque<>();
    private final BlockingDeque<UUID> receivedMessages = new LinkedBlockingDeque<>();
    private final List<SocketAddress> neighbors = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<SocketAddress, Integer> kickMap = new ConcurrentHashMap<>();
    private SocketAddress newParent = null;
    private SocketAddress Parent = null;

    public Message takeMessageToSend() throws InterruptedException {
        return messagesToSend.take();
    }

    public void forNeighbors(Consumer<? super SocketAddress> action) {
        neighbors.forEach(action);
    }

    public void addMessageToSend(Message message) {
        messagesToSend.add(message);
    }

    public void addPendingAck(Message message) {
        pendingAcks.add(message);
    }

    public void forPendingAcks(Consumer <? super Message> action) {
        pendingAcks.forEach(action);
    }

    public void addReceivedMessage(UUID uuid) { receivedMessages.add(uuid); }

    public void deleteAckMessage(Message message) {
        pendingAcks.remove(message);
    }

    public void addNeighboor(SocketAddress sock) {neighbors.add(sock);}
    /*private static Storages getInstance() {
        return Handler.INSTANCE;
    }
    private static class Handler {
        static final Storages INSTANCE = new Storages();
    }*/
}
