package treechat.model.transfer;

import treechat.model.TreeChat;
import treechat.model.message.Message;
import treechat.model.storages.Storages;

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public class Resender implements Runnable{
    private Storages storages;
    private DatagramSocket socket;
    private TreeChat chat;

    public Resender(Storages storages, DatagramSocket socket, TreeChat chat) {
        this.storages = storages;
        this.socket = socket;
        this.chat = chat;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (!storages.getPendingAcks().isEmpty()) {
                for (Message msg : storages.getPendingAcks()) {
                    if (isResend(msg)) {
                        storages.getPendingAcks().remove(msg);
                        int count = 0;
                        if (storages.getKickMap().contains(msg.getDestination())) {
                            count = storages.getKickMap().get(msg.getDestination());
                            storages.getKickMap().put(msg.getDestination(), count + 1);
                        } else {storages.getKickMap().put(msg.getDestination(), count + 1);}
                        if (count >= 15) {
                            storages.getNeighbors().remove(msg.getDestination());
                            if ((msg.getDestination() == storages.getParent()) && (storages.getNewParent()!=null)) {
                                try {
                                    socket.connect(storages.getNewParent());
                                    storages.setParent(storages.getNewParent());
                                    chat.createNewParentBroadcastMessage();
                                    storages.addNeighboor(storages.getNewParent());
                                } catch (SocketException e) {
                                    System.out.println("beda :c");
                                }
                            }
                        } else {
                            msg.setTimeout(System.currentTimeMillis());
                            storages.addMessageToSend(msg);
                        }
                    }
                }
            }
        }
    };

    private boolean isResend(Message msg) {return (System.currentTimeMillis() - msg.getTimeout()) > 3000;}

}
