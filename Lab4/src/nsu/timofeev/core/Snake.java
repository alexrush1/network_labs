package nsu.timofeev.core;

import me.ippolitov.fit.snakes.SnakesProto;
import nsu.timofeev.view.SnakePanel;

import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Snake {

    private ArrayList<Vector> snakeComponents;
    private int direction;
    private int screenWidth;
    private int screenHeight;
    private boolean hasEaten;

    public String name;
    public int id;
    public InetAddress ipAddress;
    public int port;
    public int score = 0;

    public Snake(ArrayList<Vector> startComponents, int screenWidth, int screenHeight, int id, String name, InetAddress ipAddress, int port) {
        snakeComponents = startComponents;
        direction = 1;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public Snake(ArrayList<Vector> startComponents, int screenWidth, int screenHeight, int id) {
        snakeComponents = startComponents;
        direction = 1;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.id = id;
    }

    public int getDirection() {
        return direction;
    }
    public boolean getHasEaten() { return hasEaten; }
    public void setHasEaten(boolean bool) { hasEaten = bool; }
    public int getId() {return id;}


    public SnakesProto.GameState.Snake.SnakeState state = SnakesProto.GameState.Snake.SnakeState.ALIVE;

    SnakePanel panel;

    private Vector moduleVector(int x, int y) {
        //System.out.println("newX = "+(x % screenWidth + screenWidth) % screenWidth+" newY= "+(y % screenHeight + screenHeight) % screenHeight);
        return new Vector((x % screenWidth + screenWidth) % screenWidth, (y % screenHeight + screenHeight) % screenHeight);
    }

    public void move() {
        int newX;
        int newY;
        //System.out.println(direction);

        //System.out.println("oldX = "+snakeComponents.getFirst().getX()+" oldY = "+snakeComponents.getFirst().getY());

        switch (direction) {
            case 1 -> {
                newX = snakeComponents.get(0).getX();
                newY = snakeComponents.get(0).getY() - 1;
                snakeComponents.add(0, moduleVector(newX, newY));
                snakeComponents.remove(snakeComponents.size() - 1);
            }
            case 2 -> {
                newX = snakeComponents.get(0).getX();
                newY = snakeComponents.get(0).getY() + 1;
                snakeComponents.add(0, moduleVector(newX, newY));
                snakeComponents.remove(snakeComponents.size() - 1);
            }
            case 3 -> {
                newX = snakeComponents.get(0).getX() - 1;
                newY = snakeComponents.get(0).getY();
                snakeComponents.add(0, moduleVector(newX, newY));
                snakeComponents.remove(snakeComponents.size() - 1);
            }
            case 4 -> {
                newX = snakeComponents.get(0).getX() + 1;
                newY = snakeComponents.get(0).getY();
                snakeComponents.add(0, moduleVector(newX, newY));
                snakeComponents.remove(snakeComponents.size() - 1);
            }
        }
    }

    public ArrayList<Vector> getSnakeComponents() {
        return snakeComponents;
    }

    public Vector getVectorDirection() {
        switch (direction) {
            case 1 -> {
                return new Vector(0,-1);
            }
            case 2 -> {
                return new Vector(0,1);
            }
            case 3 -> {
                return new Vector(-1,0);
            }
            case 4 -> {
                return new Vector(1,0);
            }
        }
        return null;
    }

    public ArrayList<Vector> getKeyComponents() {
        var result = new ArrayList<Vector>();
        if (snakeComponents.size() == 1) { result.add(snakeComponents.get(0)); return result; }
        var head = snakeComponents.get(0);
        result.add(head);
        if (snakeComponents.size() == 2) {  result.add(head.dotsCalc(snakeComponents.get(1))); }

        Vector currentMix;
        Vector previousMix = getVectorDirection();
        int count = 1;
        for (int i = 1; i < snakeComponents.size() - 1; i++) {
            currentMix = snakeComponents.get(i).dotsCalc(snakeComponents.get(i + 1));
            if (currentMix.equals(previousMix)) {
                count++;
            } else {
                result.add(new Vector(-(previousMix.getX() * count), -(previousMix.getY() * count)));
                count = 1;
                previousMix = currentMix;
            }
        }

        result.add(new Vector(-(previousMix.getX() * count), -(previousMix.getY() * count)));
        return result;
    }

    private SnakesProto.Direction convertDirection() {
        if (direction == SnakesProto.Direction.UP_VALUE) {
            return SnakesProto.Direction.UP;
        } else if (direction == SnakesProto.Direction.DOWN_VALUE) {
            return SnakesProto.Direction.DOWN;
        } else if (direction == SnakesProto.Direction.RIGHT_VALUE) {
            return SnakesProto.Direction.RIGHT;
        } else { return SnakesProto.Direction.LEFT; }
    }

    public void setNewProtoDirection(SnakesProto.Direction newDirection) {
        if (newDirection == SnakesProto.Direction.UP) {
            direction = SnakesProto.Direction.UP_VALUE;
        } else if (newDirection == SnakesProto.Direction.DOWN) {
            direction = SnakesProto.Direction.DOWN_VALUE;
        } else if (newDirection == SnakesProto.Direction.RIGHT) {
            direction = SnakesProto.Direction.RIGHT_VALUE;
        } else { direction = SnakesProto.Direction.LEFT_VALUE; }
    }

    public SnakesProto.GameState.Snake createStateSnake() {
        var builder = SnakesProto.GameState.Snake.newBuilder();
        builder.setPlayerId(id);
        builder.setState(state);

        for (var dot: getKeyComponents()) {
            builder.addPoints(dot.coord());
        }
        builder.setHeadDirection(convertDirection());

        return builder.build();
    }

    public void printKeyDots() {
        System.out.println("DOTS:");
        var dots = getKeyComponents();
        for (var d: dots) {
            System.out.println("( "+ d.getX()+", "+d.getY()+")");
        }
        return;
    }

    public boolean isFoodHere(Vector food) {
        var head = snakeComponents.get(0);
        //System.out.println("isFoodHere() headX= "+head.getX()+" headY= "+head.getY());
        //System.out.println("isFoodHere() foodX= "+food.getX()+" foodY= "+food.getY());
        if (head.getX() == food.getX() && head.getY() == food.getY()) {
            return true;
        } else { return false; }
    }

    public void changeDirection(int a) {direction = a;}



}
