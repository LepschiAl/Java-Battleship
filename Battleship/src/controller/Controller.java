package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.awt.event.ActionEvent;

public class Controller {

    @FXML
    private GridPane playerGrid, opponentGrid;

    @FXML
    private Button placeShipButton;

    @FXML
    private Button startGameButton;

    @FXML
    public void initialize() {
        // Initialisiere das Spielfeld mit Buttons
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button playerButton = new Button();
                Button opponentButton = new Button();
                playerGrid.add(playerButton, i, j);
                opponentGrid.add(opponentButton, i, j);
            }
        }
    }

    @FXML
    public void onPlaceShip(ActionEvent event) {

    }
}