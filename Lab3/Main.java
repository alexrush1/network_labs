import treechat.controller.ConsoleController;
import treechat.model.TreeChat;
import treechat.model.message.Message;

import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) throws SocketException, UnknownHostException {
        ConsoleController console;
        if (args.length == 3) {
            TreeChat chat = new TreeChat(args[0], args[1], args[2]);
            console = new ConsoleController(chat);
            try {
                chat.start();
                console.run();
            } catch (Exception e) {
                System.out.println("$CHAT: error! exit...\n");
            } finally {
                try {
                    chat.stop();
                } catch (Exception e) {}
            }
        } else if (args.length == 5) {
            TreeChat chat = new TreeChat(args[0], args[1], args[2], args[3], args[4]);
            console = new ConsoleController(chat);
            try {
                chat.start();
                console.run();
            } catch (Exception e) {
                System.out.println("$CHAT: error! exit...\n");
            } finally {
                try {
                    chat.stop();
                } catch (Exception e) {}
            }
        }
    }
}
