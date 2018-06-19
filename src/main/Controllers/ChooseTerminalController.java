package main.Controllers;

import main.Logic.Core;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import main.Logic.Core;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.util.Arrays.stream;

public class ChooseTerminalController implements Initializable {
    @FXML
    ListView<String> terminalSelectionListView;
    @FXML
    Button terminalButton;

    @FXML
    public void chooseTerminals() {
        List <String> list = terminalSelectionListView.getSelectionModel().getSelectedItems();
        ArrayList <String> terminals = new ArrayList <String> (list);

        Core.getInstance().setTerminalsToPermute(terminals);
        endSession();

    }

    private void endSession() {
        Stage current = (Stage) terminalButton.getScene().getWindow();
        current.close();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        terminalSelectionListView.setFocusTraversable(false);
        terminalSelectionListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ArrayList<String> terminals = Core.getInstance().getGrammar().getTerminals();
        ObservableList<String> terminalsList = FXCollections.observableArrayList(terminals);
        terminalSelectionListView.setItems(terminalsList);
    }
}
