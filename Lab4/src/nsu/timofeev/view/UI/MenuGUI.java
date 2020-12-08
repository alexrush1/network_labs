package nsu.timofeev.view.UI;

import nsu.timofeev.net.MulticastAnnouncment.Receiver;
import nsu.timofeev.net.PlayerNode;
import nsu.timofeev.view.SnakeFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class MenuGUI {
    private JFrame frame;
    //private Receiver receiver;
    JTextField smallField;
    public void Menu() {
        frame = new JFrame("Main menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        smallField = new JTextField(15);
        smallField.setToolTipText("Nickname");
        smallField.setBounds(125, 25, 150, 25);

        JPanel panel = new JPanel(); // the panel is not visible in output
        panel.setLayout(null);

        panel.add(smallField);

        JButton newGameButton = new JButton("New game");
        newGameButton.setBounds(125, 75, 150, 50);
        panel.add(newGameButton);
        newGameButton.addActionListener(this::newGameAction);

        JButton searchGameButton = new JButton("List game");
        searchGameButton.setBounds(125, 150, 150, 50);
        panel.add(searchGameButton);
        searchGameButton.addActionListener(this::listAction);

        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(125, 225, 150, 50);
        panel.add(exitButton);
        exitButton.addActionListener(this::exitAction);

        frame.setLocationRelativeTo(null);
        frame.setFocusable(true);
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    private void exitAction(ActionEvent event) {
        System.exit(0);
    }

    private void listAction(ActionEvent event) {
        frame.setVisible(false);
        PlayerNode node = new PlayerNode(smallField.getText());
        ListGameGUI list = new ListGameGUI(node);

        try {
            list.LGGUI();
        } catch (IOException e) {}
    }

    private void newGameAction(ActionEvent event) {
        frame.setVisible(false);
        PlayerNode node = new PlayerNode(smallField.getText(), 5000);
        SnakeFrame frame = new SnakeFrame(node);

        EventQueue.invokeLater(() -> {
            frame.createWindow();
            frame.setVisible(true);
        });
        try {
            node.startGame();
        } catch (IOException e) {}
    }

}
