package server;

import common.Message;
import java.io.*;
import java.net.Socket;
import java.util.Random;

public class GameHandler implements Runnable {
    private final Socket clientSocket;
    private final Boolean[][] field; // field[row][column]
    private static final Random random = new Random();

    public GameHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.field = new Boolean[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                this.field[i][j] = false;
            }
        }
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            while (true) {
                Message message = (Message) in.readObject();
                System.out.println("Nachricht empfangen: " + message);

                if (message.x == -2 && message.y == -2) { // Spielstart
                    placeShips();
                    fireRandomShot(out);
                }
                else if (message.x == -1 && message.y == -1) {
                    fireRandomShot(out);
                }
                else if (message.x >= 0 && message.y >= 0) { // Schuss des Clients
                    boolean hit = checkIsHit(message.x, message.y);
                    if (hit) {out.writeObject(new Message(-1, -1, hit));}
                    if (!hit) fireRandomShot(out);
                }
            }
        } catch (Exception e) {
            System.err.println("Fehler: " + e.getMessage());
        }
    }

    private void placeShips() {
        placeShip(2);
        placeShip(3);
        placeShip(4);
        debugPrintField();
    }

    private void placeShip(int size) {
        boolean placed = false;
        while (!placed) {
            boolean horizontal = random.nextBoolean();
            int x = random.nextInt(10 - (horizontal ? size : 0));
            int y = random.nextInt(10 - (!horizontal ? size : 0));

            if (canPlaceShip(x, y, size, horizontal)) {
                for (int i = 0; i < size; i++) {
                    if (horizontal) field[y][x + i] = true;
                    else field[y + i][x] = true;
                }
                placed = true;
            }
        }
    }

    private boolean canPlaceShip(int x, int y, int size, boolean horizontal) {
        for (int i = 0; i < size; i++) {
            int cx = horizontal ? x + i : x;
            int cy = horizontal ? y : y + i;

            if (cy >= 10 || cx >= 10 || field[cy][cx]) return false;

            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    int ny = cy + dy;
                    int nx = cx + dx;
                    if (ny >= 0 && ny < 10 && nx >= 0 && nx < 10 && field[ny][nx]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkIsHit(int x, int y) {
        System.out.println("Prüfe Treffer bei Spalte " + x + ", Zeile " + y);
        boolean hit = field[y][x];
        if (hit) field[y][x] = false;
        System.out.println(hit ? "Treffer" : "Kein Treffer");
        return hit;
    }

    private void fireRandomShot(ObjectOutputStream out) throws IOException {
        int x = random.nextInt(10);
        int y = random.nextInt(10);
        System.out.println("Server schießt auf: Spalte " + x + ", Zeile " + y);
        out.writeObject(new Message(x, y, false));
    }

    private void debugPrintField() {
        System.out.println("Server Spielfeld:");
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                System.out.print(field[y][x] ? "X " : ". ");
            }
            System.out.println();
        }
    }
}

