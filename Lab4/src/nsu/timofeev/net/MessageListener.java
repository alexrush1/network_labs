package nsu.timofeev.net;

import me.ippolitov.fit.snakes.SnakesProto;
import nsu.timofeev.core.GameBoard;
import nsu.timofeev.util.BytesConverter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class MessageListener implements Runnable{

    private int id;
    private int port;
    private DatagramSocket socket;
    private GameBoard board;
    private PlayerNode node;
    private long lastSeq = 0;

    public MessageListener(DatagramSocket socket, GameBoard board, PlayerNode node) {
        this.socket = socket;
        this.board = board;
        this.node = node;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            byte[] bytesMessage = new byte[1024];
            DatagramPacket packet = new DatagramPacket(bytesMessage, 1024);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                var msg = (SnakesProto.GameMessage) BytesConverter.getObject(packet.getData());
                if (msg.getMsgSeq() < lastSeq) {continue;}
                lastSeq = msg.getMsgSeq();
                if  (msg.hasAck()) {
                    id = msg.getReceiverId();
                    node.setID(id);
                    //node.users.add(new InetSocketAddress(packet.getAddress(), packet.getPort()));
                    node.setPort(packet.getPort());
                    System.out.println("port: "+packet.getPort());
                    node.connectGame();
                    System.out.println("ACK!!!");
                } else if (msg.hasState()) {
                    //System.out.println("recv state");
                    board.applyGameState(msg.getState().getState());
                    //update.update();
                } else if (msg.hasSteer()) {
                    System.out.println("changed");
                    board.changeDirection(msg.getSteer().getDirection(), msg.getSenderId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
