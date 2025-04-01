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
    private Config config;

    public void setupSocket() throws IOException {
        try {
            config = new Config();
            config.readConfigFile();
            String ip = config.getValue("Server_IP");
            int port = Integer.parseInt(config.getValue("Server_PORT"));

            this.socket = new Socket(ip,port);
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

    public Message receiveFire(){
        while (true) {
            try {
                Message message = (Message) in.readObject();
                System.out.println("Message received: " +message.toString());
                return message;
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void endGame(){

    }
}
