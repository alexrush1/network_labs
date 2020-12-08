package nsu.timofeev;

import nsu.timofeev.core.GameBoard;
import nsu.timofeev.net.PlayerNode;
import nsu.timofeev.view.SnakeFrame;

import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        PlayerNode node;

        if (args.length == 2) {
            node = new PlayerNode(args[0], Integer.parseInt(args[1]));
        } else {
            node = new PlayerNode(args[0], args[1], Integer.parseInt(args[2]));
        }
        SnakeFrame frame = new SnakeFrame(node);

        EventQueue.invokeLater(() -> {
            frame.createWindow();
            frame.setVisible(true);
        });
        if (args.length == 2) {
            node.startGame();
        } else {
            node.connectGame();
        }
    }
}
