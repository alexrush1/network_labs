package treechat.model.message;

import lombok.Getter;
import lombok.Setter;
import treechat.model.transfer.BytesConverter;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class Message implements Serializable {
    private final MessageType messageType;
    private final String senderName;
    private final SocketAddress destination;
    private final UUID uuid;
    private final String data;
    private SocketAddress newParent = null;
    //private final Date createTime;
    private long timeout;

    public Message(MessageType messageType, String senderName, SocketAddress destination, String data) {
        this.messageType = messageType;
        this.senderName = senderName;
        this.destination = destination;
        this.data = data;
        this.timeout = System.currentTimeMillis();
        uuid = UUID.randomUUID();
        //createTime = new Date();
    }

    public Message(MessageType messageType, SocketAddress destination, UUID uuid, String senderName) {
        this.messageType = messageType;
        this.destination = destination;
        this.senderName = senderName;
        this.data = "";
        this.timeout = System.currentTimeMillis();
        this.uuid = uuid;
    }

    public Message(SocketAddress destination, SocketAddress newParent) {
        this.messageType = MessageType.NEWPARENT;
        this.senderName = "";
        this.uuid = UUID.randomUUID();
        this.data = "";
        this.newParent = newParent;
        this.destination = destination;
    }

    public byte[] toBytes() throws IOException {
        return BytesConverter.getBytes(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return destination.equals(message.destination) &&
                uuid.equals(message.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination, uuid);
    }
}
