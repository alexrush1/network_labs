package nsu.timofeev.core;

import me.ippolitov.fit.snakes.SnakesProto;
import nsu.timofeev.net.PlayerNode;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class GameBoard {
    Timer timer;
    private Random random = new Random();

    private ArrayList<Snake> snakes = new ArrayList<Snake>();
    private SnakesProto.GamePlayers players;
    private Snake userSnake;
    private Updatable update;
    private PlayerNode node;
    private DatagramSocket socket;  //only for steer sry :c

    int foodX;
    int foodY;


    public String name;
    int port;


    private int stateOrder = 0;
    private int foodCapacity;
    private float foodPP;
    private float deadFood;
    private ArrayList<Vector> food = new ArrayList<Vector>();

    SnakesProto.GameConfig config;
    SnakesProto.NodeRole nodeRole;

    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;
    int UNIT_SIZE = 15;
    //int UNITS = (SCREEN_HEIGHT * SCREEN_WIDTH) / UNIT_SIZE;

    public int getWidth() { return SCREEN_WIDTH; }
    public int getHeight() { return SCREEN_HEIGHT; }
    public int getUnitSize() { return UNIT_SIZE; }
    public ArrayList<Vector> getFood() { return (ArrayList<Vector>) food.clone(); }
    public ArrayList<Snake> getSnakes() { return (ArrayList<Snake>) snakes.clone(); }
    public Snake getUserSnake() { return userSnake; }
    public SnakesProto.NodeRole getNodeRole() { return nodeRole; }

    public GameBoard(SnakesProto.GameConfig config, String name, SnakesProto.NodeRole nodeRole, PlayerNode node) {
        this.config = config;
        SCREEN_WIDTH = config.getWidth();
        SCREEN_HEIGHT = config.getHeight();
        foodCapacity = config.getFoodStatic();
        foodPP = config.getFoodPerPlayer();
        deadFood = config.getDeadFoodProb();
        this.nodeRole = nodeRole;
        this.name = name;
        this.node = node;
    }

    public void addUpdatable (Updatable update) {
        this.update = update;
    }

    public void newEat() {
        if (food.size() < foodCapacity + foodPP * snakes.size()) {
            foodX = random.nextInt(SCREEN_WIDTH);
            foodY = random.nextInt(SCREEN_HEIGHT);
            food.add(new Vector(foodX, foodY));
        }
    }


    public void foodKilling() {
        //System.out.println("foodKilling()");
        Iterator<Vector> foodIt = food.iterator();
        while (foodIt.hasNext()) {
            Vector s = foodIt.next();
            for (var snake : snakes) {
                if (snake.isFoodHere(s)) {
                    snake.setHasEaten(true);
                    snake.getSnakeComponents().add(0, s);
                    snake.score++;
                    foodIt.remove();
                }
            }
        }
    }

    private void createDeadFood(Snake s) {
        for (var c: s.getSnakeComponents()) {
            if (random.nextFloat() < deadFood) {
                food.add(c);
            }
        }
    }

    public void checkCollisions() {
        Iterator<Snake> i = snakes.iterator();
        while (i.hasNext()) {
            Snake s = i.next();
            Vector head = s.getSnakeComponents().get(0);
            for (var c : s.getSnakeComponents()) {
                if (c == head) continue;
                if (c.equals(head) && !s.getHasEaten()) {
                    //System.out.println("died");
                    createDeadFood(s);
                    i.remove();
                }
            }
        }
        Iterator<Snake> j = snakes.iterator();
        while (j.hasNext()) {
            Snake s = j.next();
            Vector head = s.getSnakeComponents().get(0);
            for (var snake : getSnakes()) {
                if (snake.equals(s)) {continue;}
                for (var c: snake.getSnakeComponents()) {
                    if (c.equals(head)){
                        //System.out.println("died");
                        createDeadFood(s);
                        j.remove();
                    }
                }
            }
        }
    }

    public void startGame(SnakesProto.NodeRole nodeRole) {
        this.nodeRole = nodeRole;
        newEat();
        var vec2 = new ArrayList<Vector>();
        vec2.add(new Vector(25,25));
        userSnake = new Snake(vec2, SCREEN_WIDTH, SCREEN_HEIGHT, 1, name, null, port);
        snakes.add(userSnake);
    }

    private Vector moduleVector(int x, int y) {
        //System.out.println("newX = "+(x % screenWidth + screenWidth) % screenWidth+" newY= "+(y % screenHeight + screenHeight) % screenHeight);
        return new Vector((x % SCREEN_WIDTH + SCREEN_WIDTH) % SCREEN_WIDTH, (y % SCREEN_HEIGHT + SCREEN_HEIGHT) % SCREEN_HEIGHT);
    }

    public void update() {
        for (var s: snakes) {
            s.setHasEaten(false);
            s.move();
        }
        foodKilling();
        newEat();
        checkCollisions();
        update.update();
        stateOrder++;
    }

    public SnakesProto.GamePlayer createGamePlayer(String name, int id, String ipAddress, int port, SnakesProto.NodeRole nodeRole, int score) {
        var builder = SnakesProto.GamePlayer.newBuilder();

        builder.setName(name);
        builder.setId(id);
        builder.setIpAddress(ipAddress);
        builder.setPort(port);
        builder.setRole(nodeRole);
        builder.setScore(score);

        return builder.build();
    }

    public void addNewPlayer(String name, int id, InetAddress address, int port) {
        var vec2 = new ArrayList<Vector>();
        vec2.add(new Vector(25,25));
        var snake = new Snake(vec2, SCREEN_WIDTH, SCREEN_HEIGHT, id, name, address, port);
        snakes.add(snake);
    }

    public SnakesProto.GamePlayers convertArrayToProto() {
        var builder = SnakesProto.GamePlayers.newBuilder();
        if (snakes.size() == 1) return builder.build();
        for (int i = 0; i < snakes.size(); i++) {
            var playerBuilder = SnakesProto.GamePlayer.newBuilder();
            playerBuilder.setName(snakes.get(i).name);
            playerBuilder.setScore(snakes.get(i).score);
            playerBuilder.setId(snakes.get(i).id);
            playerBuilder.setIpAddress(snakes.get(i).ipAddress.getHostAddress());
            playerBuilder.setPort(snakes.get(i).port);
            playerBuilder.setRole(node.roles.get(snakes.get(i).id));
            playerBuilder.build();
            builder.addPlayers(playerBuilder);
        }
        return builder.build();
    }

    public SnakesProto.GameState createGameState() {
        var builder = SnakesProto.GameState.newBuilder();

        builder.setStateOrder(stateOrder);

        if (getFood() != null) {
            for (var f : getFood()) {
                builder.addFoods(f.coord());
            }
        }

        for (var s: snakes) {
            builder.addSnakes(s.createStateSnake());
        }

        builder.setPlayers(convertArrayToProto());

        builder.setConfig(config);

        return builder.build();
    }

    public Vector coordToVector(SnakesProto.GameState.Coord coord) {
        return  new Vector(coord.getX(), coord.getY());
    }

    public ArrayList<Vector> keyToVector(me.ippolitov.fit.snakes.SnakesProto.GameState.Snake snake) {
        //System.out.println("RECV");
        ArrayList<Vector> result = new ArrayList<>();
        var head = coordToVector(snake.getPointsList().get(0));
        result.add(head);
        for (int i = 1; i < snake.getPointsList().size(); i++) {
            int sgn;
            var buf = coordToVector(snake.getPointsList().get(i));
            if (buf.getX() != 0) {
                if (buf.getX() > 0) {sgn = 1;} else {sgn = -1;}
                for (int j = 0; j < Math.abs(buf.getX()); j++) {
                    var newV = moduleVector(head.getX() + sgn, head.getY());
                    System.out.println("("+(head.getX() + sgn)+"; "+head.getY()+")");
                    result.add(newV);
                    head = newV;
                }
            } else if (buf.getY() != 0) {
                if (buf.getY() > 0) {sgn = 1;} else {sgn = -1;}
                for (int j = 0; j < Math.abs(buf.getY()); j++) {
                    var newV = moduleVector(head.getX(), head.getY() + sgn);
                    System.out.println("("+head.getX()+"; "+(head.getY() + sgn)+")");
                    result.add(newV);
                    head = newV;
                }
            }
        }
        return result;
    }

    public void applyGameState(SnakesProto.GameState state) {
        stateOrder = state.getStateOrder();

        snakes.clear();
        for (var s: state.getSnakesList()) {
            var vector = keyToVector(s);
            snakes.add(new Snake(vector, SCREEN_WIDTH, SCREEN_HEIGHT, s.getPlayerId()));
        }

        food.clear();
        for (var f: state.getFoodsList()) {
            food.add(new Vector(f.getX(), f.getY()));
        }

        players = state.getPlayers();

        config = state.getConfig();

        update.update();
    }

    public void printFood() {
//        System.out.println("FOOD:");
//        for (var f: food) {
//            System.out.println("("+f.getX()+"; "+f.getY()+")");
//        }
        System.out.println("snake");
        for (var s: snakes.get(0).getSnakeComponents()) {
            System.out.println("("+s.getX()+"; "+s.getY()+")");
        }
    }

    public void changeDirection(SnakesProto.Direction dir, int id) {
        System.out.println("changeDirection() dir: "+dir+"id: "+ id);
        for (var s: snakes) {
            if (s.getId() == id) {
                System.out.println("found!");
                s.setNewProtoDirection(dir);
            }
        }
    }

    public SnakesProto.GameMessage clientChangeDirection(SnakesProto.Direction dir, int id) {
        var builder = SnakesProto.GameMessage.SteerMsg.newBuilder();
        builder.setDirection(dir);

        var fullBuilder = SnakesProto.GameMessage.newBuilder();
        fullBuilder.setSteer(builder.build());
        fullBuilder.setSenderId(id);
        fullBuilder.setMsgSeq(System.currentTimeMillis());
        return fullBuilder.build();
    }
}
