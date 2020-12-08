package nsu.timofeev.core;

import me.ippolitov.fit.snakes.SnakesProto;
import nsu.timofeev.net.PlayerNode;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class KeyAction extends KeyAdapter {
    PlayerNode node;

    public KeyAction(PlayerNode node) {
        this.node = node;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("ojkdaojsdiasoidija");
        if (node.getGameBoard().getNodeRole() == SnakesProto.NodeRole.MASTER) {
            switch (e.getKeyCode()) {
                case 87:
                    if (node.getGameBoard().getUserSnake().getDirection() != 2) node.getGameBoard().getUserSnake().changeDirection(1);
                    break;
                case 83:
                    if (node.getGameBoard().getUserSnake().getDirection() != 1) node.getGameBoard().getUserSnake().changeDirection(2);
                    break;
                case 65:
                    if (node.getGameBoard().getUserSnake().getDirection() != 4) node.getGameBoard().getUserSnake().changeDirection(3);
                    break;
                case 68:
                    if (node.getGameBoard().getUserSnake().getDirection() != 3) node.getGameBoard().getUserSnake().changeDirection(4);
                    break;
                case 81:
                    node.getGameBoard().getUserSnake().printKeyDots();
                case 82:
                    node.getGameBoard().printFood();
            }
        } else {switch (e.getKeyCode()) {
            case 87 -> {
                try {
                    node.changeClientDirection(SnakesProto.Direction.UP);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            case 83 -> {
                try {
                    node.changeClientDirection(SnakesProto.Direction.DOWN);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            case 65 -> {
                try {
                    System.out.println("LEFT!");
                    node.changeClientDirection(SnakesProto.Direction.LEFT);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            case 68 -> {
                try {
                    node.changeClientDirection(SnakesProto.Direction.RIGHT);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }}
    }
}
