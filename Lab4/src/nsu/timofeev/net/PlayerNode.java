package nsu.timofeev.net;

import me.ippolitov.fit.snakes.SnakesProto;
import nsu.timofeev.core.GameBoard;
import nsu.timofeev.net.MulticastAnnouncment.Sender;
import nsu.timofeev.util.BytesConverter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerNode {

    private SnakesProto.NodeRole nodeRole;
    private SnakesProto.PlayerType playerType = SnakesProto.PlayerType.HUMAN;
    private Sender announcementSender;
    private DatagramSocket socket;
    public ArrayList<InetSocketAddress> users;

    private GameBoard gameBoard;
    SnakesProto.GameConfig config;
    public GameBoard getGameBoard() { return gameBoard; }

    private String name;
    private String ip;
    private int port;
    private int id;
    public void setID(int id) {this.id = id;}

    public PlayerNode(String name, int port) throws IOException {
        this.name = name;
        this.port = port;
        config = createGameConfig();
        gameBoard = new GameBoard(config, name, nodeRole);
        nodeRole = SnakesProto.NodeRole.MASTER;
        users = new ArrayList<>();
    }

    public PlayerNode(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        config = createGameConfig();
        gameBoard = new GameBoard(config, name, nodeRole);
        nodeRole = SnakesProto.NodeRole.NORMAL;
    }

    private SnakesProto.GameConfig createGameConfig() {
        return SnakesProto.GameConfig.newBuilder().setStateDelayMs(700).setDeadFoodProb(0.9f).build();
    }

    public SnakesProto.GameState getGameState() {
        return gameBoard.createGameState();
    }

    public void startGame() throws IOException {
        Sender sender = new Sender();
        socket = new DatagramSocket(port);
        Thread listener = new Thread(new MessageListener(socket, gameBoard, this));
        listener.start();

        var timer = new Timer();
        var timerTask = new TimerTask() {
            @Override
            public void run() {
                gameBoard.update();
            }
        };
        timer.schedule(timerTask, 500, config.getStateDelayMs());

        var timer2 = new Timer();
        var timerTask2 = new TimerTask() {
            @Override
            public void run() {
                try {
                    sender.send(createMCastMsg());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        timer2.schedule(timerTask2, 500, 1000);

        var timer3 = new Timer();
        var timerTask3 = new TimerTask() {
            @Override
            public void run() {
                try {
                    sendStateMsg();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        timer3.schedule(timerTask3, 500, config.getStateDelayMs());
        gameBoard.startGame(nodeRole);
        //SnakesProto.GameConfig.newBuilder().build();
    }

    public void tick() {

    }

    public void changeClientDirection(SnakesProto.Direction dir) throws IOException {
        var msg = gameBoard.clientChangeDirection(dir, id);
        var buf = BytesConverter.getBytes(msg);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), port);
        socket.send(packet);
    }

    public void sendStateMsg() throws IOException {
        if (users.size() < 1) return;
        var stateMsg = SnakesProto.GameMessage.newBuilder().setState(createStateMessage()).setMsgSeq(System.currentTimeMillis()).build();
        byte[] msg = BytesConverter.getBytes(stateMsg);
        for (var u: users) {
            DatagramPacket packet = new DatagramPacket(msg, msg.length, u.getAddress(), u.getPort());
            socket.send(packet);
        }
    }

    public void connectGame() throws IOException {
        socket = new DatagramSocket();
        Thread listener = new Thread(new MessageListener(socket, gameBoard, this));
        listener.start();
        var joinMsg = SnakesProto.GameMessage.newBuilder().setJoin(createJoinMsg()).setMsgSeq(System.currentTimeMillis()).build();
        byte[] buf = BytesConverter.getBytes(joinMsg);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), port);
        socket.send(packet);

        //Thread listener = new Thread(new MessageListener(socket));
        //listener
    }

    private SnakesProto.GameMessage.AnnouncementMsg createMCastMsg() {
        var builder = SnakesProto.GameMessage.AnnouncementMsg.newBuilder();

        builder.setPlayers(gameBoard.convertArrayToProto());
        builder.setConfig(config);

        return builder.build();
    }

    private SnakesProto.GameMessage.StateMsg createStateMessage() {
        var builder = SnakesProto.GameMessage.StateMsg.newBuilder();

        builder.setState(getGameState());

        return builder.build();
    }

    private SnakesProto.GameMessage.JoinMsg createJoinMsg() {
        var builder = SnakesProto.GameMessage.JoinMsg.newBuilder();

        builder.setName(name);

        return builder.build();
    }
}
