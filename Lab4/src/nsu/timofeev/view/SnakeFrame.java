package nsu.timofeev.view;

import nsu.timofeev.core.GameBoard;
import nsu.timofeev.core.KeyAction;
import nsu.timofeev.net.PlayerNode;

import javax.swing.*;
import java.awt.*;

public class SnakeFrame extends JFrame {

    private PlayerNode node;
    TextArea text;

    public SnakeFrame(PlayerNode node) {
        this.node = node;
    }

    public void createWindow() {
        text = new TextArea();
        text.setBounds(node.getGameBoard().getWidth() * node.getGameBoard().getUnitSize(), 0, 100, node.getGameBoard().getHeight() * node.getGameBoard().getUnitSize() + 30);
        text.setEditable(false);
        text.setFocusable(false);
        var panel = new SnakePanel(node, text);
        panel.add(text);
        panel.setSize(node.getGameBoard().getWidth() * node.getGameBoard().getUnitSize(), node.getGameBoard().getHeight() * node.getGameBoard().getUnitSize() + 30);
        add(panel);
        setSize(node.getGameBoard().getWidth() * node.getGameBoard().getUnitSize() + 100, node.getGameBoard().getHeight() * node.getGameBoard().getUnitSize() + 30);
        setTitle("GAME");
        setResizable(false);
        setLocationRelativeTo(null);
        this.addKeyListener(new KeyAction(node));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel.setLayout(null);
    }

}
