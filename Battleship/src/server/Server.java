package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;


    public static void main(String[] args) {
        Socket clientSocket = null;
        Executor executor = Executors.newCachedThreadPool();



        try (ServerSocket serverSocket = new ServerSocket(2345)){
            System.out.println("Server is waiting for connection");

            while (true){
                clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                executor.execute(new GameHandler(clientSocket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
