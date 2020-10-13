package nsu.timofeev;

import java.io.*;
import java.net.Socket;

import static nsu.timofeev.MD5.getMD5Checksum;

public class ServerThread implements Runnable {
    private Socket sock;
    private String fileName;
    private long fileSize;
    private int fileNameLength;
    private DataInputStream in;
    private FileOutputStream out;
    private long currentTime = 0;
    private long previousTime = 0;
    private long startTime = 0;
    private File f;
    private byte buffer[];
    private String clientHash;
    private long bytesPerTick;

    public ServerThread(Socket sock) throws IOException {
        this.sock = sock;
        in = new DataInputStream(sock.getInputStream());
    }

    public void readSize() throws IOException {
        fileSize = in.readLong();
    }

    public void readName() throws IOException {
        fileNameLength = in.readInt();
        byte bufName[] = new byte[fileNameLength];
        in.read(bufName,0,fileNameLength);
        fileName = new String(bufName);
        while (true) {
            f = new File(fileName);
            if (!f.isFile()) {
                out = new FileOutputStream(fileName);
                break;
            } else {
                fileName = "1" + fileName;
            }
        }
    }

    public void closeSocket() throws IOException {
        in.close();
        sock.close();
    }

    private float getCurrentSpeed() {
        previousTime = currentTime;
        currentTime = System.currentTimeMillis();
        float currentSpeed = bytesPerTick / (((currentTime - previousTime) / 1000f));
        bytesPerTick = 0;
        return  currentSpeed;
    }

    private void hashCompare() throws Exception {
        String serverHash = getMD5Checksum(f.getName());
        if (serverHash.equals(clientHash)) {
            System.out.println("equals hash!");
        } else {
            System.out.println("bad hash. deleting file.");
            f.delete();
        }

    }

    private int getBytesLeft(long bytes_read) {
        return ((fileSize - bytes_read) > (long)(buffer.length)) ? buffer.length : (int)(fileSize - bytes_read);
    }

    @Override
    public void run() {
        try {
            buffer = new byte[1024];
            byte hashBuffer[] = new byte[32];
            readSize();
            readName();
            in.read(hashBuffer, 0, 32);
            clientHash = new String(hashBuffer);
            long recvBytes = 0;
            bytesPerTick += Integer.BYTES + fileName.length() + Long.BYTES;
            int toRead = getBytesLeft(recvBytes);
            while (recvBytes < fileSize) {
                int recvTick = in.read(buffer, 0, toRead);
                recvBytes += recvTick;
                bytesPerTick += recvTick;
                toRead = getBytesLeft(recvBytes);
                out.write(buffer,0, recvTick);
                if(bytesPerTick > 10000) {
                    System.out.println("Current speed = " + getCurrentSpeed()+ "B/s");
                }
            }
            hashCompare();
            closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
            out.flush();
            out.close();
            in.close();
            } catch (IOException e) {

            }
        }
    }
}
