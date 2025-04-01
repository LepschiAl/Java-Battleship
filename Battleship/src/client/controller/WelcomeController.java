package client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {
    @FXML
    private Button startButton;

    @FXML
    private void startGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/view/view.fxml"));
            Parent root = loader.load();

            // Neue Spielinstanz erstellen
            Controller controller = loader.getController();
            controller.initializeNewGame();

            Main.getPrimaryStage().setScene(new Scene(root, 900, 600));
        } catch (IOException e) {
            // Fehlerbehandlung
        }
    }
}
