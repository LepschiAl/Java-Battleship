package client.controller;

import common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import client.model.Client;
import client.model.Game;
import server.Server;

import java.io.IOException;

public class Controller {

    @FXML
    private GridPane playerGrid, opponentGrid;

    @FXML
    private Button placeShipButton;

    @FXML
    private Button startGameButton;

    private Integer maxClicks = 9;
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

        game = new Game(playerGrid);
        client = new Client();
        try {
            client.setupSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onPlaceShip(ActionEvent event) {
        Button button = (Button) event.getSource();
        int col = GridPane.getColumnIndex(button);
        int row = GridPane.getRowIndex(button);
        System.out.println("Schiff gesetzt bei: Spalte " + col + ", Zeile " + row);

        button.setText(button.getText().equals("X") ? "" : "X");
        button.setStyle(shipStyle);
        startGameButton.setDisable(!validateInput());
    }

    @FXML
    public void onStartGame(ActionEvent event) {
        if (validateInput()) {
            client.request(-2, -2, false);
            startGameButton.setDisable(true);
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

        if (received.x == -1 && received.y == -1) {
            colorOpponentGrid(latestShot.x, latestShot.y, true);
            opponentGrid.setDisable(false);
            new Alert(Alert.AlertType.INFORMATION, "Treffer! Du bist nochmal dran.").showAndWait();
        }
        else if (checkIsHit(received.x, received.y)) {
            client.request(-1, -1, true);
            colorOwnGrid(received, true);
            opponentGrid.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "Gegner traf dein Schiff!").showAndWait();
            waitForServerShot();
        }
        else {
            colorOwnGrid(received, false);
            opponentGrid.setDisable(false);
            new Alert(Alert.AlertType.INFORMATION, "Gegner schoss daneben. Dein Zug!").showAndWait();
        }
    }

    private void colorOwnGrid(Message message, boolean isHit) {
        if (message == null) return;
        for (Node node : playerGrid.getChildren()) {
            if (GridPane.getColumnIndex(node) == message.x && GridPane.getRowIndex(node) == message.y) {
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
                button.setDisable(true);
                break;
            }
        }
    }
}