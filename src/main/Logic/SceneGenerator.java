package main.Logic;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneGenerator {

    public void generateModal(String fxml, String title) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(fxml));

        Stage news = new Stage();
        Scene elo = new Scene(parent);
        news.setScene(elo);
        news.setResizable(false);
        news.setTitle(title);
        news.initModality(Modality.APPLICATION_MODAL);
        news.showAndWait();

    }

    public void generateScene(String fxml, String title, ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(fxml));
        Scene next = new Scene(parent);
        Stage current = (Stage) ((Node) event.getSource()).getScene().getWindow();

        current.hide();
        current.setScene(next);
        current.setTitle(title);
        current.setResizable(true);
        current.show();
    }

    public void generateNewInstance(String fxml, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Scene next = new Scene(root);
        Stage current = new Stage();

        current.setTitle(title);
        current.setScene(next);
        current.setResizable(false);
        current.getIcons().add(new Image(getClass().getResourceAsStream("../FXML/logo1234.png")));
        current.show();

    }

}
