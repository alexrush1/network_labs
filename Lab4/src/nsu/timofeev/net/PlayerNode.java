package nsu.timofeev.net;

import me.ippolitov.fit.snakes.SnakesProto;
import nsu.timofeev.core.GameBoard;
import nsu.timofeev.net.MulticastAnnouncment.Sender;
import nsu.timofeev.util.BytesConverter;
import nsu.timofeev.view.SnakeFrame;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerNode {

    private SnakesProto.NodeRole nodeRole;
    private SnakesProto.PlayerType playerType = SnakesProto.PlayerType.HUMAN;
    private Sender announcementSender;
    private DatagramSocket socket;
    public ArrayList<InetSocketAddress> users;
    public HashMap<Integer, SnakesProto.NodeRole> roles;

    private int joinID = 2;

    private GameBoard gameBoard;
    SnakesProto.GameConfig config;
    public GameBoard getGameBoard() { return gameBoard; }

    private String name;
    private String ip;
    private int port;
    private int id;
    public void setID(int id) {this.id = id;}
    public int getID() {return id;}

    private String serverIp;
    private int serverPort;

    public void setIp(String serverIp) {this.serverIp = serverIp;}
    public void setPort(int port) {this.serverPort = port;}

    public PlayerNode(String name, int port) {
        this.name = name;
        this.port = port;
        this.id = 1;
        config = createGameConfig();
        gameBoard = new GameBoard(config, name, nodeRole, this);
        nodeRole = SnakesProto.NodeRole.MASTER;
        roles = new HashMap<>();
        roles.put(id, nodeRole);
        users = new ArrayList<>();
    }

    public PlayerNode(String name) {
        this.name = name;
        config = createGameConfig();
        gameBoard = new GameBoard(config, name, nodeRole, this);
        nodeRole = SnakesProto.NodeRole.NORMAL;
        users = new ArrayList<>();
    }

    private SnakesProto.GameConfig createGameConfig() {
        return SnakesProto.GameConfig.newBuilder().setStateDelayMs(100).setDeadFoodProb(0.9f).build();
    }

    public SnakesProto.GameState getGameState() {
        return gameBoard.createGameState();
    }

    public void getAck(DatagramPacket packet) throws IOException, ClassNotFoundException {
        var msg = (SnakesProto.GameMessage) BytesConverter.getObject(packet.getData());
        System.out.println(msg.getJoin().getName());
        var ackMsg = SnakesProto.GameMessage.AckMsg.newBuilder().build();
        var ackFullMsg = SnakesProto.GameMessage.newBuilder().setMsgSeq(System.currentTimeMillis()).setAck(ackMsg).setReceiverId(joinID).build();
        var buf = BytesConverter.getBytes(ackFullMsg);
        DatagramPacket ackPacket = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
        this.users.add((InetSocketAddress) packet.getSocketAddress());
        socket.send(ackPacket);
        gameBoard.addNewPlayer(msg.getJoin().getName(), joinID, packet.getAddress(), packet.getPort());
        if (joinID == 2) {
            roles.put(joinID, SnakesProto.NodeRole.DEPUTY);
        } else {
            roles.put(joinID, SnakesProto.NodeRole.NORMAL);
        }
        users.add(new InetSocketAddress(packet.getAddress(), packet.getPort()));
        System.out.println("SENDED!");
        joinID++;
    }

    public void startGame() throws IOException {
        socket = new DatagramSocket(port);
        MessageListener messageListener = new MessageListener(socket, gameBoard, this);
        Thread listener = new Thread(messageListener);
        listener.start();
        Sender sender = new Sender(this);
        //socket = new DatagramSocket(port);

        var timer2 = new Timer();
        var timerTask2 = new TimerTask() {
            @Override
            public void run() {
                try {
                    sender.send(createMCastMsg());
                    sender.recvJoin();
                } catch (IOException | ClassNotFoundException e) {
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
                    gameBoard.update();
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
        DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(serverIp), serverPort);
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

    public void askConnect(String ip, int port) throws IOException {
        System.out.println(ip+" "+port);
        socket = new DatagramSocket();
        var msgL = new MessageListener(socket, gameBoard, this);
        Thread listener = new Thread(msgL);
        listener.start();
        var joinMsg = SnakesProto.GameMessage.newBuilder().setJoin(createJoinMsg()).setMsgSeq(System.currentTimeMillis()).build();
        byte[] buf = BytesConverter.getBytes(joinMsg);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), port);
        socket.send(packet);
        setIp(ip);
        System.out.println("ip: "+ip);
    }

    public void connectGame() {
        SnakeFrame frame = new SnakeFrame(this);
        EventQueue.invokeLater(() -> {
            frame.createWindow();
            frame.setVisible(true);
        });

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
