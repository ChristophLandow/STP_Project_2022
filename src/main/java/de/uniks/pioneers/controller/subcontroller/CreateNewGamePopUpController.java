package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.services.LobbyService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import static de.uniks.pioneers.Constants.FX_SCHEDULER;

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
    public PasswordField passwordTextfield;
    @FXML
    public HBox buttonBox;
    @FXML
    public Button cancelButton;
    @FXML
    public Button createGameButton;
    @FXML
    public Label nameLen;
    @FXML
    public Label passwordLen;

    private LobbyScreenController lobbyScreenController;
    private LobbyService lobbyService;

    public void init(LobbyScreenController lobbyScreenController, LobbyService lobbyService) {
        this.lobbyScreenController = lobbyScreenController;
        this.lobbyService = lobbyService;
        createGameButton.setOnMouseClicked(this::createGame);
        cancelButton.setOnMouseClicked(this::closePoPUp);

        IntegerBinding gameNameLength = Bindings.length(gameNameTextField.textProperty());
        IntegerBinding passwordLength = Bindings.length(passwordTextfield.textProperty());
        BooleanBinding invalid = Bindings.equal(passwordLen.textProperty(), nameLen.textProperty()).not();

        createGameButton.disableProperty().bind(invalid);

        passwordLen.textProperty().bind(Bindings
                .when(passwordLength.greaterThan(7)).then("")
                .otherwise("password must be at least eight characters long"));

        nameLen.textProperty().bind(Bindings
                .when(gameNameLength.greaterThan(3)).then("")
                .otherwise("game name must be at least three characters long"));
    }

    private void createGame(MouseEvent mouseEvent) {
        String name = gameNameTextField.getText();
        String password = passwordTextfield.getText();
        lobbyService.createGame(name,password)
                .observeOn(FX_SCHEDULER)
                .subscribe(game -> {
                    lobbyScreenController.showNewGameLobby(game, "test");
                    Stage stage = (Stage) popUpBox.getScene().getWindow();
                    stage.close();
                });
    }

    private void closePoPUp(MouseEvent mouseEvent) {
        Stage stage = (Stage) popUpBox.getScene().getWindow();
        stage.close();
    }
}
