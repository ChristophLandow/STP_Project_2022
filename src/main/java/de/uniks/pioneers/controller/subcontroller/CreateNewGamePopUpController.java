package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.services.LobbyService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import javax.inject.Inject;
import javax.inject.Provider;

import java.io.IOException;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class CreateNewGamePopUpController implements Controller {
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
    public PasswordField passwordTextField;
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

    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final Provider<LobbyService> lobbyServiceProvider;

    @Inject
    public CreateNewGamePopUpController(Provider<LobbyScreenController> lobbyScreenControllerProvider,
                                        Provider<LobbyService> lobbyServiceProvider) {
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.lobbyServiceProvider = lobbyServiceProvider;
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/CreateNewGamePopUp.fxml"));
        loader.setControllerFactory(c -> this);
        Parent node;
        try {
            node = loader.load();
        } catch (IOException e) {
            node = null;
        }
        init();
        return node;
    }

    @Override
    public void init() {
        createGameButton.setOnMouseClicked(this::createGame);
        cancelButton.setOnMouseClicked(this::closePoPUp);

        IntegerBinding gameNameLength = Bindings.length(gameNameTextField.textProperty());
        IntegerBinding passwordLength = Bindings.length(passwordTextField.textProperty());
        BooleanBinding invalid = Bindings.equal(passwordLen.textProperty(), nameLen.textProperty()).not();

        createGameButton.disableProperty().bind(invalid);

        passwordLen.textProperty().bind(Bindings
                .when(passwordLength.greaterThan(7)).then("")
                .otherwise("password must be at least eight characters long"));

        nameLen.textProperty().bind(Bindings
                .when(gameNameLength.greaterThan(3)).then("")
                .otherwise("game name must be at least three characters long"));
    }

    @Override
    public void stop() {

    }

    private void createGame(MouseEvent mouseEvent) {
        String name = gameNameTextField.getText();
        String password = passwordTextField.getText();
        lobbyServiceProvider.get().createGame(name,password)
                .observeOn(FX_SCHEDULER)
                .subscribe(game -> {
                    LobbyScreenController lobbyScreenController = lobbyScreenControllerProvider.get();
                    lobbyScreenController.showNewGameLobby(game, password);
                    Stage stage = (Stage) popUpBox.getScene().getWindow();
                    stage.close();
                });
    }

    private void closePoPUp(MouseEvent mouseEvent) {
        Stage stage = (Stage) popUpBox.getScene().getWindow();
        stage.close();
    }
}
