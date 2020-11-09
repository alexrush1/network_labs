package treechat.model;

import lombok.Getter;
import lombok.Setter;
import treechat.controller.ConsoleController;
import treechat.model.message.Message;
import treechat.model.message.MessageType;
import treechat.model.storages.Storages;
import treechat.model.transfer.*;

import java.net.*;

public class TreeChat {
    private Storages storages = new Storages();
    private DatagramSocket socket;
    @Getter private String senderName;
    private int lossPercentage;
    private Thread sender;
    private Thread receiver;
    private Thread resender;
    //private Thread console;
    //@Getter private SocketAddress parent;
    //@Setter private SocketAddress newParent;



    public TreeChat(String senderName, String port, String lossPercentage) throws SocketException {
        this.senderName = senderName;
        socket = new DatagramSocket(Integer.parseInt(port));
        this.lossPercentage = Integer.parseInt(lossPercentage);
    }

    public TreeChat(String senderName, String port, String lossPercentage, String serverIP, String serverPort) throws SocketException, UnknownHostException {
        this.senderName = senderName;
        this.lossPercentage = Integer.parseInt(lossPercentage);
        this.socket = new DatagramSocket(Integer.parseInt(port));
        connectToNode(serverIP, serverPort);
    }

    public void start() {
        sender = new Thread(new Sender(socket, storages));
        sender.start();

        receiver = new Thread(new Receiver(socket, storages, lossPercentage, this));
        receiver.start();

        resender = new Thread(new Resender(storages, socket, this));
        resender.start();

    }

    public void stop() {
        try {
            sender.interrupt();
            receiver.interrupt();
            resender.interrupt();
            //console.interrupt();
            socket.close();
        } catch (Exception e) {}
    }

    private void connectToNode(String serverIP, String serverPort) throws SocketException {
        var remoteAddress = new InetSocketAddress(serverIP, Integer.parseInt(serverPort));
        storages.setParent(remoteAddress);
        this.socket.connect(remoteAddress);
        storages.addNeighboor(remoteAddress);
        createWelcomeBroadcastMessage();
    }

    public void createBroadcastMessage(String messageData) {
        storages.forNeighbors(neighbor->{
                var newMessage = new Message(MessageType.USER, senderName, neighbor, messageData);
                storages.addMessageToSend(newMessage);
        });
    }

    public void createWelcomeBroadcastMessage() {
        storages.forNeighbors(neighbor->{
            var newMessage = new Message(MessageType.WELCOME, senderName, neighbor, "");
            storages.addMessageToSend(newMessage);
        });
    }

    public void createResendMessage(String messageData, String senderName, SocketAddress senderSocket) {
        storages.forNeighbors(neighbor->{
            if (!neighbor.equals(senderSocket)) {
                var newMessage = new Message(MessageType.USER, senderName, neighbor, messageData);
                storages.addMessageToSend(newMessage);
            }
        });
    }

    public void createNewParentBroadcastMessage() {
        storages.forNeighbors(neighbor->{
            var newMessage = new Message(neighbor, storages.getParent());
            storages.addMessageToSend(newMessage);
        });
    }

}
