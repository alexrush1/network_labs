package nsu.timofeev.net;

import me.ippolitov.fit.snakes.SnakesProto;
import nsu.timofeev.core.GameBoard;
import nsu.timofeev.util.BytesConverter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class MessageListener implements Runnable{

    private int joinID = 2;
    private int id;
    private int port;
    private DatagramSocket socket;
    private GameBoard board;
    private PlayerNode node;

    public MessageListener(DatagramSocket socket, GameBoard board, PlayerNode node) throws IOException {
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
                if (msg.hasJoin()) {
                    System.out.println(msg.getJoin().getName());
                    var ackMsg = SnakesProto.GameMessage.AckMsg.newBuilder().build();
                    var ackFullMsg = SnakesProto.GameMessage.newBuilder().setMsgSeq(System.currentTimeMillis()).setAck(ackMsg).setReceiverId(joinID).build();
                    var buf = BytesConverter.getBytes(ackFullMsg);
                    DatagramPacket ackPacket = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                    node.users.add((InetSocketAddress) packet.getSocketAddress());
                    socket.send(ackPacket);
                    board.addNewPlayer(msg.getJoin().getName(), joinID, packet.getAddress(), packet.getPort());
                    System.out.println("SENDED!");
                    joinID++;
                } else if (msg.hasAck()) {
                    id = msg.getReceiverId();
                    node.setID(id);
                    System.out.println("ACK!!!");
                } else if (msg.hasState()) {
                    //System.out.println("recv state");
                    board.applyGameState(msg.getState().getState());
                    //update.update();
                } else if (msg.hasSteer()) {
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
