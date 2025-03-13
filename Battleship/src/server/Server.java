package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public void setupServer(){
        try {
            serverSocket = new ServerSocket(6666);
            System.out.println("Server started");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                // Hier kannst du die Client-Verbindung verarbeiten
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
