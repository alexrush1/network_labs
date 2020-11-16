package treechat.model.transfer;

import treechat.model.message.Message;
import treechat.model.storages.Storages;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

public class Sender implements Runnable{
    private DatagramSocket socket;
    private Storages storages;

    public Sender(DatagramSocket socket, Storages storages) {
        this.socket = socket;
        this.storages = storages;
    }

    public void sendMessage(Message message) throws IOException {
        byte[] bytesMessage = message.toBytes();
        //System.out.println(bytesMessage.length);
        var packet = new DatagramPacket(bytesMessage, bytesMessage.length, message.getDestination());
        socket.send(packet);
        storages.addPendingAck(message);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (!storages.getMessagesToSend().isEmpty()) {
                try {
                    var message = storages.takeMessageToSend();
                    sendMessage(message);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
