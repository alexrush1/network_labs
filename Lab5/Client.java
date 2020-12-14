import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private static int clientsAmount = 0;

    private int clientId;

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private InputStream remoteIn;
    private OutputStream remoteOut;

    private boolean connected;

    public boolean isConnected() {
        return connected;
    }

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();

        clientId = clientsAmount++;
    }

    public void connect() throws IOException {
        byte[] bytes = new byte[20];
        int readBytes = in.read(bytes);
        WelcomePacket packet = new WelcomePacket(bytes);

        out.write(packet.createAnswer());

        readBytes = in.read(bytes);

        //checking for non establish a TCP/IP stream request
        if (bytes[1] != 1) {
            byte[] ansBytes = new byte[10];
            ansBytes[0] = bytes[0];
            ansBytes[1] = 0;
            ansBytes[2] = 7;
            ansBytes[3] = 1;
            ansBytes[4] = 127;
            ansBytes[5] = 0;
            ansBytes[6] = 0;
            ansBytes[7] = 1;
            ansBytes[8] = (byte) 5000 / 256;
            ansBytes[9] = (byte) (5000 % 256);
            out.write(ansBytes);
            connected = false;
            return;
        }
        //checking for ipv6 address
        if (bytes[3] != 1) {
            byte[] ansBytes = new byte[10];
            ansBytes[0] = bytes[0];
            ansBytes[1] = 0;
            ansBytes[2] = 8;
            ansBytes[3] = 1;
            ansBytes[4] = 127;
            ansBytes[5] = 0;
            ansBytes[6] = 0;
            ansBytes[7] = 1;
            ansBytes[8] = (byte) 5000 / 256;
            ansBytes[9] = (byte) (5000 % 256);
            out.write(ansBytes);
            connected = false;
            return;
        }
        //System.out.println("third byte = " + bytes[3]);
        byte[] remoteAddress = {bytes[4], bytes[5], bytes[6], bytes[7]};
        byte[] ansBytes = new byte[10];
        ansBytes[0] = bytes[0];
        ansBytes[1] = 0;
        ansBytes[2] = 0;
        ansBytes[3] = 1;
        ansBytes[4] = 127;
        ansBytes[5] = 0;
        ansBytes[6] = 0;
        ansBytes[7] = 1;
        ansBytes[8] = (byte) 5000 / 256;
        ansBytes[9] = (byte) (5000 % 256);
        out.write(ansBytes);
        //System.out.println("answer sended!");

        //System.out.print("address = " + InetAddress.getByAddress(remoteAddress) + " port = " + ((int) (bytes[8] & 0xFF) * 256 + (int) (bytes[9] & 0xFF)) + " connecting...");
        var remoteSocket = new Socket(InetAddress.getByAddress(remoteAddress), ((int) (bytes[8] & 0xFF) * 256 + (int) (bytes[9] & 0xFF)));
        //System.out.println("DONE!");
        remoteIn = remoteSocket.getInputStream();
        remoteOut = remoteSocket.getOutputStream();

        connected = true;
    }

    public void process() throws IOException {
        if (in.available() > 0) {
            byte[] bytes4 = new byte[2048];
            //System.out.println("cli -> remote ...");
            int readBytes = in.read(bytes4);
            //System.out.println("cli -> remote done!");
            byte[] bytes2 = new byte[readBytes];
            for (int i = 0; i < readBytes; i++) {
                bytes2[i] = bytes4[i];
            }
            remoteOut.write(bytes2);
        }
        if (remoteIn.available() > 0) {
            byte[] bytes5 = new byte[2048];
            //System.out.println("remote -> cli ...");
            int readBytes = remoteIn.read(bytes5);
            //System.out.println("remote -> cli done!");
            byte[] bytes3 = new byte[readBytes];
            for (int i = 0; i < readBytes; i++) {
                bytes3[i] = bytes5[i];
            }
            out.write(bytes3);
        }
    }

    public void disconnect() {
        try {
            socket.close();
            in.close();
            out.close();
            remoteIn.close();
            remoteOut.close();
        } catch (Exception e) {}
    }
}
