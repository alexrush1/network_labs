package nsu.timofeev;

import java.io.IOException;

public class CMain {

    public static void main(String[] args) throws Exception {
        Client cli = new Client(args[0], args[1], args[2]);
        try {
            cli.sendSize();
            cli.sendName();
            cli.createAndSendHash();
            cli.sendFile();
            cli.closeSocket();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            cli.closeSocket();
        }
    }
}
