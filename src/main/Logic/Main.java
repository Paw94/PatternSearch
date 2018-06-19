package main.Logic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../../resources/Views/MainApp.fxml"));
        primaryStage.setTitle("Pattern Searching");
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setResizable(false);
        scene.getStylesheets().add("resources/CSS//bootstrap3.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}