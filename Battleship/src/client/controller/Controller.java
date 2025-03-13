package client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
    @FXML
    public void initialize() {
        // Größe der Buttons anpassen, um das Grid quadratisch zu machen
        double buttonSize = 50.0; // Größe der Buttons

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button playerButton = new Button();
                playerButton.setPrefSize(buttonSize, buttonSize);
                playerButton.setOnAction(this::onPlaceShip);
                playerGrid.add(playerButton, i, j);

                Button opponentButton = new Button();
                opponentButton.setPrefSize(buttonSize, buttonSize); // Quadratische Buttons
                opponentGrid.add(opponentButton, i, j);
            }
        }

        game = new Game(playerGrid);
        server = new Server();
        server.setupServer();
        client = new Client();
        try {
            client.setupSocket("localhost", 6666);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onPlaceShip(ActionEvent event) {
        System.out.println(event.getSource());
        System.out.println(GridPane.getRowIndex((Button) event.getSource()));
        System.out.println(GridPane.getColumnIndex((Button) event.getSource()));


        Button eventButton = (Button) event.getSource();

        if (eventButton.getText().equals("X")) {
            eventButton.setText("");
        }
        else {
            eventButton.setText("X");
        }

        if (validateInput()) {
            startGameButton.setDisable(false);
        }
    }

    @FXML public void onStartGame(ActionEvent event) {
        if (validateInput()) {
            // Start Game
        }
    }

    private void colorNeighborBtns(int x, int y) {

    }

    private boolean validateInput(){
        int counter = 0;
        for (Node node : playerGrid.getChildren()){
            Button btn = (Button) node;
            if (btn.getText().equals("X")){
                counter++;
            }
        }

        System.out.println("Ships placed: " +counter + "Ships required: " +maxClicks);
        if (counter == maxClicks){
            return true;
        }
        return false;
    }


}