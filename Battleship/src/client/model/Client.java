package client.model;

import common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void setupSocket(String ip, int port) throws IOException {
        try {
            this.socket = new Socket(ip, port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void request(int x, int y, boolean isHit){
        try {
            out.writeObject(new Message(x, y, isHit));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
