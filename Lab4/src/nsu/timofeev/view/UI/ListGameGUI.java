package nsu.timofeev.view.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import nsu.timofeev.net.MulticastAnnouncment.AnnouncmentStorage;
import nsu.timofeev.net.MulticastAnnouncment.Client;
import nsu.timofeev.net.MulticastAnnouncment.Receiver;
import nsu.timofeev.net.PlayerNode;

public class ListGameGUI {

    private Receiver receiver;
    private AnnouncmentStorage storage;
    private JFrame frame;
    private JTable table;
    DefaultTableModel model;
    PlayerNode node;

    public void updateTable() {
        model.setRowCount(0);
        var msgStorage = storage.getMsgStorage();
        var serversStorage = storage.getServers();
        for (var server: serversStorage) {
            var msg = msgStorage.get(server);
            Object[] row = new Object[4];
            row[1] = msg.getPlayers().getPlayersCount();
            row[0] = msg.getConfig().getWidth()+"x"+msg.getConfig().getHeight();
            row[2] = server.getHostName();
            row[3] = server.getPort();
            model.addRow(row);
        }
    }

    public ListGameGUI(PlayerNode node) {
        this.node = node;
    }

    public void LGGUI() throws IOException {
        storage = new AnnouncmentStorage();
        Thread recv = new Thread(new Receiver(storage));
        recv.start();
        frame = new JFrame("List of games");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        table = new JTable();

        Object[] columnNames = {"Field size", "Players", "IP", "PORT"};
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);
        table.setModel(model);

        table.setEnabled(false);

        table.setBackground(Color.cyan);
        table.setForeground(Color.white);
        table.setRowHeight(30);
        JButton button = new JButton("connect");
        button.setBounds(250, 325, 150, 50);
        JScrollPane pane = new JScrollPane(table);

        button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //var row = Math.abs(table.getSelectedRow());
                    var row = 0;
                    node.askConnect((String) table.getValueAt(row, 2), (int) table.getValueAt(row, 3));
                    frame.setVisible(false);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        frame.add(button);

        var ti = new Timer();
        var tiT = new TimerTask() {
            @Override
            public void run() {
                updateTable();
            }
        };
        ti.schedule(tiT, 500, 1000);

        //JList<Client> list1 = new JList<Client>(clients);
        //JPanel panel = new JPanel(); // the panel is not visible in output
        //panel.setLayout(null);
        //panel.add(table);
        frame.add(pane);
        frame.setLocationRelativeTo(null);
        //frame.setFocusable(true);
        //frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
}