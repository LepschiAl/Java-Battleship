package client.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showWelcomeScreen();
        primaryStage.show();
    }

    public static void showWelcomeScreen() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/client/view/welcome.fxml"));
        primaryStage.setTitle("Schiffe Versenken");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.setResizable(false);
    }
}