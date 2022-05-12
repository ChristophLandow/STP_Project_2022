package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.LobbyScreenController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CreateNewGamePopUpController {
    @FXML
    public VBox popUpBox;
    @FXML
    public HBox gameNameBox;
    @FXML
    public Label gameNameLabel;
    @FXML
    public TextField gameNameTextField;
    @FXML
    public HBox passwordBox;
    @FXML
    public Label passWordLabel;
    @FXML
    public TextField passWordTextField;
    @FXML
    public HBox buttonBox;
    @FXML
    public Button cancelButton;
    @FXML
    public Button createGameButton;

    private LobbyScreenController lobbyScreenController;


    public void init(LobbyScreenController lobbyScreenController) {
        this.lobbyScreenController = lobbyScreenController;
        createGameButton.setOnMouseClicked(this::createGame);
        cancelButton.setOnMouseClicked(this::closePoPUp);
    }

    private void closePoPUp(MouseEvent mouseEvent) {
        Stage stage = (Stage) popUpBox.getScene().getWindow();
        stage.close();
    }


    private void createGame(MouseEvent mouseEvent) {
    }
}
