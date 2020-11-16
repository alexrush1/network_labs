package treechat.model.transfer;

import treechat.model.TreeChat;
import treechat.model.message.Message;
import treechat.model.message.MessageType;
import treechat.model.storages.Storages;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Random;
import java.util.UUID;

public class Receiver implements Runnable{
    private DatagramSocket socket;
    private Storages storages;
    private Random generator = new Random();
    private int lossPercentage;
    private final TreeChat chat;

    public Receiver(DatagramSocket socket, Storages storages, int lossPercentage, TreeChat chat) {
        this.socket = socket;
        this.storages = storages;
        this.lossPercentage = lossPercentage;
        this.chat = chat;
    }

    private void ackPrepare(SocketAddress sender, Message message) {
        Message ackMessage = new Message(MessageType.ACK, sender, message.getUuid(), message.getSenderName());
        storages.addMessageToSend(ackMessage);
    }

    public void recvMessage() throws IOException, ClassNotFoundException {
        byte[] bytesMessage = new byte[1024];
        DatagramPacket packet = new DatagramPacket(bytesMessage, 1024);
        socket.receive(packet);
        if (generator.nextInt(100) < (100 - lossPercentage)) {
            Message message = (Message) BytesConverter.getObject(packet.getData());
            if (message.getMessageType() == MessageType.USER) {
                if (!storages.getReceivedMessages().contains(message.getUuid())) {
                    storages.getKickMap().put(packet.getSocketAddress(), 0);
                    storages.addReceivedMessage(message.getUuid());
                    ackPrepare(packet.getSocketAddress(), message);
                    chat.createResendMessage(message.getData(), message.getSenderName(), packet.getSocketAddress());
                    System.out.printf("[%s]: %s\n", message.getSenderName(), message.getData());
                } else {
                    ackPrepare(packet.getSocketAddress(), message);
                }
            } else if (message.getMessageType() == MessageType.ACK) {
                storages.getKickMap().put(packet.getSocketAddress(), 0);
                for (Message msg : storages.getPendingAcks()) {
                    var uuid = msg.getUuid();
                    var name = msg.getSenderName();
                    if (uuid.equals(message.getUuid()) && name.equals(message.getSenderName())) {
                        storages.deleteAckMessage(msg);
                    }
                }
            } else if (message.getMessageType() == MessageType.NEWPARENT) {
                storages.setNewParent(message.getNewParent());
                ackPrepare(packet.getSocketAddress(), message);
            }
        }
        if (!storages.getNeighbors().contains(packet.getSocketAddress())) {
            storages.addNeighboor(packet.getSocketAddress());
            storages.getKickMap().put(packet.getSocketAddress(), 0);
            if (storages.getParent() != null) {
                storages.addMessageToSend(new Message(packet.getSocketAddress(), storages.getParent()));
            } else {
                for (SocketAddress address: storages.getNeighbors()) {
                    if (!address.equals(packet.getSocketAddress())) {storages.addMessageToSend(new Message(packet.getSocketAddress(), address));}
                }
            }
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                recvMessage();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
