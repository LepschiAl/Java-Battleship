package client.controller;

import common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import client.model.Client;
import client.model.Game;
import server.Server;

import java.io.IOException;
import java.util.Objects;

public class Controller {

    @FXML
    private GridPane playerGrid, opponentGrid;

    @FXML
    private Button placeShipButton;

    @FXML
    private Button startGameButton;

    @FXML
    private Label shipCounterLabel;

    private Integer maxClicks = 9;
    private Integer ships4x1 = 1;
    private Integer ships3x1 = 1;
    private Integer ships2x1 = 1;

    private Integer playerShipsHit = 0;
    private Integer opponentShipsHit = 0;
    private Game game;
    private Client client;
    private Server server;

    private Message latestShot;
    private boolean isHit;

    private String defaultButtonStyle = "-fx-base: #d0d0d0;";
    private String shipStyle = "-fx-background-color: #6495ED;";
    private String hitStyle = "-fx-background-color: #FF4500;";
    private String missStyle = "-fx-background-color: #A9A9A9;";
    private String opponentHitStyle = "-fx-background-color: #FF8C00;";

    @FXML
    public void initialize() {
        double buttonSize = 50.0;

        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {
                Button playerButton = new Button();
                playerButton.setPrefSize(buttonSize, buttonSize);
                playerButton.setOnAction(this::onPlaceShip);
                playerButton.setId(col + ";" + row);
                playerGrid.add(playerButton, col, row);

                Button opponentButton = new Button();
                opponentButton.setPrefSize(buttonSize, buttonSize);
                opponentButton.setOnAction(this::onFire);
                opponentButton.setId(col + ";" + row);
                opponentGrid.add(opponentButton, col, row);
            }
        }
        shipCounterLabel.setText("Bitte Schiffe setzen...");

        game = new Game(playerGrid);
        client = new Client();
        try {
            client.setupSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initializeNewGame() {

        maxClicks = 9;
        playerShipsHit = 0;
        opponentShipsHit = 0;


        resetGrid(playerGrid);
        resetGrid(opponentGrid);


        client = new Client();
        try {
            client.setupSocket();
        } catch (IOException e) {

        }
    }

    private void updateShipCounter() {
        long placedShips = playerGrid.getChildren().stream()
                .filter(node -> ((Button) node).getText().equals("X"))
                .count();
        shipCounterLabel.setText("Schiffe: " + placedShips + "/" + maxClicks);
    }

    private void resetGrid(GridPane grid) {
        for (Node node : grid.getChildren()) {
            Button btn = (Button) node;
            btn.setDisable(false);
            btn.setText("");
            btn.setStyle(defaultButtonStyle);
        }
    }

    @FXML
    public void onPlaceShip(ActionEvent event) {
        Button button = (Button) event.getSource();
        int col = GridPane.getColumnIndex(button);
        int row = GridPane.getRowIndex(button);
        System.out.println("Schiff gesetzt bei: Spalte " + col + ", Zeile " + row);


        button.setText(button.getText().equals("X") ? "" : "X");
        button.setStyle(button.getText().equals("X") ? shipStyle : defaultButtonStyle);

        updateShipCounter();
        startGameButton.setDisable(!validateInput());
    }

    @FXML
    public void onStartGame(ActionEvent event) {
        if (validateInput()) {
            //hasShipXxY(4,1,1);
            shipCounterLabel.setText("Spiel läuft...");
            client.request(-2, -2, false);
            startGameButton.setDisable(true);
            startGameButton.setVisible(false);

            playerGrid.setDisable(true);
            waitForServerShot();
        }
    }

    @FXML
    public void onFire(ActionEvent event) {
        Button button = (Button) event.getSource();
        String[] coords = button.getId().split(";");
        int x = Integer.parseInt(coords[0]);
        int y = Integer.parseInt(coords[1]);
        System.out.println("Feuer auf: Spalte " + x + ", Zeile " + y);

        button.setDisable(true);
        client.request(x, y, false);
        this.latestShot = new Message(x, y, false);

        waitForServerShot();
    }

    private boolean validateInput() {
        int counter = 0;
        for (Node node : playerGrid.getChildren()) {
            if (((Button) node).getText().equals("X")) counter++;
        }

        return counter == maxClicks;
    }

    private boolean hasShipXxY(int x, int y,int  count){
        int ships4x1 = 0;
        int ships3x1 = 0;
        int ships2x1 = 0;
        for(Node n: playerGrid.getChildren()){
            Button b = (Button) n;
            if (b.getText() == "X") {
                int xCoord = GridPane.getColumnIndex(n);
                int yCoord = GridPane.getRowIndex(n);
                System.out.println("Ship placed on "+xCoord + " and "+yCoord);
                for (int i = 0; i < x; i++) {
                    // Check right
                    if (isShip(xCoord++, yCoord));
                    // Check left
                    if (isShip(xCoord--, yCoord));
                    // Check up

                    // Check down
                }
            }
        }
        return true;
    }

    private boolean isShip(int x, int y){
        for(Node n: playerGrid.getChildren()){
            if (GridPane.getColumnIndex(n) == x && GridPane.getRowIndex(n) == y) {
                Button b = (Button) n;
                return b.getText().equals("X");
            }
        }
        return false;
    }

    private boolean checkIsHit(int x, int y) {
        for (Node node : playerGrid.getChildren()) {
            if (GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y) {
                boolean hit = ((Button) node).getText().equals("X");
                System.out.println("Überprüfe " + x + "," + y + ": " + (hit ? "TREFFER" : "DANEBEN"));
                return hit;
            }
        }
        return false;
    }

    private void waitForServerShot() {
        Message received = client.receiveFire();

        if (received.getX() == -1 && received.getY() == -1) {
            this.opponentShipsHit++;
            if (!checkIfGameOver()) {
                colorOpponentGrid(latestShot.getX(), latestShot.getY(), true);
                opponentGrid.setDisable(false);
                new Alert(Alert.AlertType.CONFIRMATION, "Treffer! Du bist nochmal dran.").showAndWait();
            }
        }
        else if (checkIsHit(received.getX(), received.getY())) {
            this.playerShipsHit++;
            if (!checkIfGameOver()) {
                client.request(-1, -1, true);
                colorOwnGrid(received, true);
                opponentGrid.setDisable(true);
                new Alert(Alert.AlertType.ERROR, "Dein Schuss ging daneben. \nUnd der Gegner traf dein Schiff! Gegner nochmal am Zug").showAndWait();
                waitForServerShot();
            }
        }
        else {
            colorOwnGrid(received, false);
            if (latestShot != null) {
                colorOpponentGrid(latestShot.getX(), latestShot.getY(), false);
            }
            opponentGrid.setDisable(false);
            new Alert(Alert.AlertType.INFORMATION, "Dein Schuss ging daneben \nAber der Gegner schoss ebenfalls daneben. Dein Zug!").showAndWait();
        }
    }

    private void colorOwnGrid(Message message, boolean isHit) {
        if (message == null) return;
        for (Node node : playerGrid.getChildren()) {
            if (GridPane.getColumnIndex(node) == message.getX() && GridPane.getRowIndex(node) == message.getY()) {
                Button button = (Button) node;
                if (isHit) {
                    button.setStyle(hitStyle);
                } else {
                    button.setStyle(button.getText().equals("X") ? shipStyle : defaultButtonStyle);
                }
                break;
            }
        }
    }

    private void colorOpponentGrid(int x, int y, boolean isHit) {
        for (Node node : opponentGrid.getChildren()) {
            if (GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y) {
                Button button = (Button) node;
                button.setStyle(isHit ? opponentHitStyle : missStyle);
                button.setText(isHit ? "X" : "O");
                button.setDisable(true);
                break;
            }
        }
    }

    private boolean checkIfGameOver() {
        if (Objects.equals(this.playerShipsHit, maxClicks)){
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Game Over - You lost");
            a.showAndWait();
            client.endGame();
            returnToWelcomeScreen();
            return true;
        }
        else if (Objects.equals(this.opponentShipsHit, maxClicks)) {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Game Over - You won");
            a.showAndWait();
            client.endGame();
            returnToWelcomeScreen();
            return true;
        }
        return false;
    }

    private void returnToWelcomeScreen() {
        try {
            Main.showWelcomeScreen();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Fehler beim Zurückkehren zum Hauptmenü").showAndWait();
        }
    }
}