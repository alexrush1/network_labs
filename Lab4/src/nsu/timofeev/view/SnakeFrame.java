package nsu.timofeev.view;

import nsu.timofeev.core.GameBoard;
import nsu.timofeev.core.KeyAction;
import nsu.timofeev.net.PlayerNode;

import javax.swing.*;
import java.awt.*;

public class SnakeFrame extends JFrame {

    private PlayerNode node;
    TextField text;

    public SnakeFrame(PlayerNode node) {
        this.node = node;
    }

    public void createWindow() {
        add(new SnakePanel(node));
        setSize(node.getGameBoard().getWidth() * node.getGameBoard().getUnitSize() + 100, node.getGameBoard().getHeight() * node.getGameBoard().getUnitSize() + 30);
        setTitle("GAME");
        setResizable(false);
        setLocationRelativeTo(null);
        this.addKeyListener(new KeyAction(node));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //text = new TextField();
        //text.setBounds(node.getGameBoard().getWidth() * node.getGameBoard().getUnitSize(), node.getGameBoard().getHeight() * node.getGameBoard().getUnitSize(), 100, node.getGameBoard().getHeight() * node.getGameBoard().getUnitSize() + 30);
        //add(text);
    }

    public void scoreWindow() {

    }
}
