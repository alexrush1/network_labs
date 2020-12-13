package nsu.timofeev.view;

import nsu.timofeev.core.GameBoard;
import nsu.timofeev.core.KeyAction;
import nsu.timofeev.core.Updatable;
import nsu.timofeev.net.PlayerNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SnakePanel extends JPanel implements ActionListener, Updatable {

    private PlayerNode node;
    private GameBoard board;
    TextArea text;

    SnakePanel(PlayerNode node, TextArea text) {
        this.text = text;
        this.node = node;
        board = node.getGameBoard();
        //this.setPreferredSize(new Dimension(node.getGameBoard().getWidth(), node.getGameBoard().getHeight()));
        this.setBackground(Color.GRAY);
        this.setFocusable(true);
        this.addKeyListener(new KeyAction(node));
        node.getGameBoard().addUpdatable(this);
    }

    public void scoreWindow() {
        text.setText("");
        var gamePlayers = node.getGameBoard().getPlayers();
        if (gamePlayers == null) return;
        for (var player: gamePlayers.getPlayersList()) {
            text.append(player.getId()+" "+player.getName()+" "+player.getScore()+"\n");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.setColor(new Color(180,155,0));
        for (var food : board.getFood()) {
            g.fillOval(food.getX() * board.getUnitSize(), food.getY() * board.getUnitSize(), board.getUnitSize(), board.getUnitSize());
        }

        for (var s : board.getSnakes()) {
            if (node.getID() == s.getId()) {
                g.setColor(new Color(150, 250, 70));
            } else {g.setColor(new Color(150, 50, 140));}
            var head = s.getSnakeComponents().get(0);
            g.fillRect(head.getX() * board.getUnitSize(), head.getY() * board.getUnitSize(), board.getUnitSize(), board.getUnitSize());
            if (node.getID() == s.getId()) {
                g.setColor(new Color(0, 150, 0));
            } else {g.setColor(new Color(100, 50, 140));}
            for (var c : s.getSnakeComponents()) {
                if (c != head) {
                    g.fillRect(c.getX() * board.getUnitSize(), c.getY() * board.getUnitSize(), board.getUnitSize(), board.getUnitSize());
                    //}
                }
            }

            //g.setColor(Color.orange);
            //g.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
            //FontMetrics metrics = getFontMetrics(g.getFont());
            //g.drawString("Score: " + board.getSnakes()., 0, metrics.getHeight());
        }
    }


    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        //g.drawString("GAME OVER!", (Core.SCREEN_WIDTH - metrics.stringWidth("GAME OVER!")) / 2, Core.SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void update() {
        repaint();
        scoreWindow();
    }
}
