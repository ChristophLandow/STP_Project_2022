package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.services.LobbyService;
import de.uniks.pioneers.services.NewGameLobbyService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;


public class GameListDropDownController implements Controller {
    @FXML
    public VBox dropDownRoot;
    @FXML
    public HBox joinGameLabelBox;
    @FXML
    public Label joinGameLabel;
    @FXML
    public HBox discardLabelBox;
    @FXML
    public Label discardGameLabel;

    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final Provider<NewGameLobbyService> newGameLobbyServiceProvider;
    private final Provider<LobbyGameListController> lobbyGameListControllerProvider;
    private final Provider<LobbyService> lobbyServiceProvider;
    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();

    @Inject
    public GameListDropDownController(
            Provider<LobbyScreenController> lobbyScreenControllerProvider,
            Provider<NewGameLobbyService> newGameLobbyServiceProvider,
            Provider<LobbyGameListController> lobbyGameListControllerProvider,
            Provider<LobbyService> lobbyServiceProvider) {
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.newGameLobbyServiceProvider = newGameLobbyServiceProvider;
        this.lobbyGameListControllerProvider = lobbyGameListControllerProvider;
        this.lobbyServiceProvider = lobbyServiceProvider;
    }

    @Override
    public Parent render() {
        Parent parent;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/GameListDropDown.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            parent = loader.load();
            return parent;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }

    public void joinGame(MouseEvent mouseEvent) {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/JoinGamePopUp.fxml"));
        Parent node = null;
        try {
            node = loader.load();
            JoinGamePopUpController joinGamePopUpController = loader.getController();
            joinGamePopUpController.init(newGameLobbyServiceProvider.get(), lobbyScreenControllerProvider.get(), game.get());

        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Join Game");
        Scene scene = new Scene(node);
        stage.setScene(scene);
        stage.show();
    }

    private final CompositeDisposable disposable = new CompositeDisposable();

    public void discardGame(MouseEvent mouseEvent) {
        LobbyService lobbyService = lobbyServiceProvider.get();
        LobbyGameListController lobbyGameListController = lobbyGameListControllerProvider.get();
        lobbyGameListController.deleteGame(game.get());
    }
}
