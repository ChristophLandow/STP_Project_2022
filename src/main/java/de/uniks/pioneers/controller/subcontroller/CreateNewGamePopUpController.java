package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.LobbyScreenController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
    public ButtonBar buttonBar;


    private LobbyScreenController lobbyScreenController;

    public void init(LobbyScreenController lobbyScreenController) {
        this.lobbyScreenController = lobbyScreenController;

        // Create the buttons to go into the ButtonBar
        Button createGameButton = new Button("create game");
        createGameButton.setOnMouseClicked(this::createGame);
        ButtonBar.setButtonData(createGameButton, ButtonBar.ButtonData.LEFT);

        Button cancelButton = new Button("cancel");
        cancelButton.setOnMouseClicked(this::closePoPUp);
        ButtonBar.setButtonData(cancelButton, ButtonBar.ButtonData.RIGHT);

        // Add buttons to the ButtonBar
        buttonBar.getButtons().addAll(createGameButton,cancelButton);
    }

    private void closePoPUp(MouseEvent mouseEvent) {
    }


    private void createGame(MouseEvent mouseEvent) {
    }
}
