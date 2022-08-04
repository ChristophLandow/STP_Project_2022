package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.services.EventHandlerService;
import de.uniks.pioneers.services.NewGameLobbyService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import retrofit2.HttpException;

import java.util.Random;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class JoinGamePopUpController{
    @FXML public TextField passwordInputField;
    @FXML public Label joinGameLabel;
    @FXML public Label wrongPasswordLabel;
    @FXML public Button joinButton;
    @FXML public VBox popUpBox;
    private NewGameLobbyService newGameLobbyService;
    private Game game;

    private App app;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private LobbyScreenController lobbyScreenController;
    private String randomColor;

    public JoinGamePopUpController() {
    }

    public void init(App app, NewGameLobbyService newGameLobbyService, LobbyScreenController lobbyScreenController, Game game, EventHandlerService eventHandlerService) {
        this.newGameLobbyService = newGameLobbyService;
        this.game = game;
        this.lobbyScreenController = lobbyScreenController;
        this.randomColor = this.getRandomColor();
        this.app = app;
        wrongPasswordLabel.visibleProperty().set(false);
        BooleanBinding invalid = Bindings.equal(passwordInputField.textProperty(), "");
        joinButton.disableProperty().bind(invalid);
        Node passwordInputNode = this.passwordInputField;
        eventHandlerService.setEnterEventHandler(passwordInputNode, this.joinButton);
    }

    public void joinGame() {
        String password = passwordInputField.getText();
        disposable.add(newGameLobbyService.postMember(game._id(), false, randomColor, password)
                .observeOn(FX_SCHEDULER)
                .subscribe(res -> {
                    lobbyScreenController.showNewGameLobby(game, password, randomColor);
                    closePopUp();
                }, throwable -> {
                    if (throwable instanceof HttpException) {
                        this.wrongPasswordLabel.visibleProperty().set(true);
                    }
                    else{
                        throwable.printStackTrace();
                    }
                }));
    }

    private void closePopUp() {
        Stage stage = (Stage) popUpBox.getScene().getWindow();
        stage.close();
        disposable.dispose();
    }

    public String getRandomColor() {
        Random obj = new Random();
        int rand_num = obj.nextInt(0xffffff + 1);
        return String.format("#%06x", rand_num);
    }

    public App getApp(){
        return this.app;
    }
}
