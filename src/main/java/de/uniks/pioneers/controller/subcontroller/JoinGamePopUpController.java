package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.services.NewGameLobbyService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;


public class JoinGamePopUpController{
    @FXML
    public TextField passwordInputField;
    @FXML
    public Label joinGameLabel;
    @FXML
    public Label wrongPasswordLabel;
    @FXML
    public Button joinButton;
    @FXML
    public VBox popUpBox;
    private NewGameLobbyService newGameLobbyService;
    private Game game;

    private final CompositeDisposable disposable = new CompositeDisposable();
    private LobbyScreenController lobbyScreenController;

    public void init(NewGameLobbyService newGameLobbyService, LobbyScreenController lobbyScreenController, Game game) {
        this.newGameLobbyService = newGameLobbyService;
        this.game = game;
        this.lobbyScreenController = lobbyScreenController;
        wrongPasswordLabel.visibleProperty().set(false);
        BooleanBinding invalid = Bindings.equal(passwordInputField.textProperty(), "");
        joinButton.disableProperty().bind(invalid);
    }

    public void joinGame() {
        String password = passwordInputField.getText();
        disposable.add(newGameLobbyService.postMember(game._id(), true, password)
                .observeOn(FX_SCHEDULER)
                .subscribe(res -> {
                    lobbyScreenController.showNewGameLobby(game, password);
                    System.out.println(res.toString());
                    closePopUp();
                }, throwable -> {
                    this.wrongPasswordLabel.visibleProperty().set(true);
                    throwable.printStackTrace();
                }));
    }

    private void closePopUp() {
        Stage stage = (Stage) popUpBox.getScene().getWindow();
        stage.close();
        disposable.dispose();
    }
}
