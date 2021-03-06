
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Sender sender = new Sender("ip + port");
        Receiver receiver = new Receiver();
        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                sender.run();
                receiver.run();
                if(System.in.available() != 0) {
                    String s = scanner.nextLine();
                    if(s.equals("stop")) break;
                }
            }
            receiver.stop();
            sender.stop();
        } catch (IOException e) {
            receiver.stop();
            sender.stop();
            e.printStackTrace();
        }
    }

}
