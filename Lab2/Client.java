package nsu.timofeev;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

import static nsu.timofeev.MD5.getMD5Checksum;

public class Client {
    private InetAddress addr;
    private Socket sock;
    private DataOutputStream out;
    private FileInputStream in;
    private String fileName;
    private long fileSize;

    public Client(String address, String port, String fileName) throws IOException {
        addr = InetAddress.getByName(address);
        sock = new Socket(addr, Integer.parseInt(port));
        out = new DataOutputStream(sock.getOutputStream());
        this.fileName = fileName;
        fileSize = Files.size(Path.of(fileName));
        in = new FileInputStream(fileName);
    }

    public void sendSize() throws IOException {
        out.writeLong(fileSize);
    }

    public void sendName() throws IOException {
        out.writeInt(fileName.length());
        out.writeBytes(fileName);
    }

    public void closeSocket() throws IOException {
        out.close();
        in.close();
        sock.close();
        out.flush();
    }

    public void sendFile() throws IOException {
        byte[] buffer = new byte[1024];
        int readed;
        long hash = 0;
        while (true) {
            readed = in.read(buffer);
            if (readed == -1) break;
            for(int i = 0; i < readed; i++) {
                hash += buffer[i] / 2;
            }
            out.write(buffer, 0, readed);
        }
        out.writeLong(hash);
    }

    public void createAndSendHash() throws Exception {
        String hash = getMD5Checksum(fileName);
        out.write(hash.getBytes(), 0,32);
    }
}
