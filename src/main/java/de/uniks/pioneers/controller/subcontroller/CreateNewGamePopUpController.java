package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.services.LobbyService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.Consumer;

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
    public TextField passWordTextField;
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
    public Consumer<Game> createGameLobby;

    final IntegerBinding gameNameLength = Bindings.length(gameNameTextField.textProperty());
    final IntegerBinding passwordLength = Bindings.length(passWordTextField.textProperty());


    public void init(LobbyScreenController lobbyScreenController, LobbyService lobbyService) {
        this.lobbyScreenController = lobbyScreenController;
        this.lobbyService = lobbyService;
        createGameButton.setOnMouseClicked(this::createGame);
        cancelButton.setOnMouseClicked(this::closePoPUp);

        passwordLen.textProperty().bind(Bindings
                .when(passwordLength.greaterThan(7)).then("")
                .otherwise("password must be at least 8 characters long"));

        nameLen.textProperty().bind(Bindings
                .when(gameNameLength.greaterThan(3)).then("")
                .otherwise("to make sense use more than three characters"));


    }

    private void createGame(MouseEvent mouseEvent) {
        String name = gameNameTextField.getText();
        String password = passWordTextField.getText();
        lobbyService.createGame(name,password)
                .observeOn(FX_SCHEDULER)
                .subscribe(game -> {
                    lobbyScreenController.showNewGameLobby(game);
                });
    }

    private void closePoPUp(MouseEvent mouseEvent) {
        Stage stage = (Stage) popUpBox.getScene().getWindow();
        stage.close();
    }

    public void setCreateGameLobby(Consumer<Game> createGameLobby) {
        this.createGameLobby = createGameLobby;
    }

}
