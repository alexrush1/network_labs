package treechat.controller;

import treechat.model.TreeChat;

import java.net.SocketAddress;
import java.util.Scanner;

public class ConsoleController implements Runnable{
    private final Scanner in = new Scanner(System.in);
    private final TreeChat chat;

    public ConsoleController(TreeChat chat) {
        this.chat = chat;
    }

    public void readConsole() {
        String newMessage;
        while (true) {
            newMessage = in.nextLine();
            if (newMessage.equals("stop")) {chat.stop(); break;}
            chat.createBroadcastMessage(newMessage);
        }
        in.close();
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            readConsole();
        }
    }
}
