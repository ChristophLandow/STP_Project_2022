package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.services.LobbyService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;


public class GameListDropDownController implements Controller {

    private final Provider<GameListElementController> gameListElementControllerProvider;
    private final Provider<LobbyService> lobbyServiceProvider;
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

    @Inject
    public GameListDropDownController(Provider<GameListElementController> gameListElementControllerProvider,
                                      Provider<LobbyService> lobbyServiceProvider) {
        this.gameListElementControllerProvider = gameListElementControllerProvider;
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
    }

    public void discardGame(MouseEvent mouseEvent) {
        LobbyService lobbyService = lobbyServiceProvider.get();
        lobbyService.gamesProperty.get().remove(gameListElementControllerProvider.get().game);
    }
}
