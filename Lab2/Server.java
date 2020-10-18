package nsu.timofeev;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket sock;
    private ArrayList<ServerThread> threadsPerClient;
    private ExecutorService executorService = Executors.newFixedThreadPool(256);

    public Server(String port) throws IOException {
        sock = new ServerSocket(Integer.parseInt(port));
        threadsPerClient = new ArrayList<>();
    }

    public void process() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while(!sock.isClosed()) {
            try {
                sock.setSoTimeout(1000);
                Socket clientSocket = sock.accept();
                ServerThread client = new ServerThread(clientSocket);
                threadsPerClient.add(client);
                executorService.execute(client);

            } catch (Exception e) {}
            if(System.in.available() != 0) {
                String s = scanner.nextLine();
                if(s.equals("stop")) break;
            }
        }
        executorService.shutdown();
    }

}
