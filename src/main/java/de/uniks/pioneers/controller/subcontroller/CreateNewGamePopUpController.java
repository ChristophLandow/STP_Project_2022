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


import javax.inject.Inject;
import javax.inject.Provider;

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
        init();
    }

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

    private void createGame(MouseEvent mouseEvent) {
        String name = gameNameTextField.getText();
        String password = passwordTextField.getText();
        lobbyServiceProvider.get().createGame(name,password)
                .observeOn(FX_SCHEDULER)
                .subscribe(game -> {
                    /* kein lobby model, add game to model lobby -> 1) show game in list
                                                                    2) show game lobby for host

                        response -> show message in controller, because we dont have a model we could listen to
                        with lists, our lists are saved in services and controllers, which is rly bad !
                   */
                    lobbyScreenControllerProvider.get().showNewGameLobby(game);
                    Stage stage = (Stage) popUpBox.getScene().getWindow();
                    stage.close();
                });
    }

    private void closePoPUp(MouseEvent mouseEvent) {
        Stage stage = (Stage) popUpBox.getScene().getWindow();
        stage.close();
    }
}
