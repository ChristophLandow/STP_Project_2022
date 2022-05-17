package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.services.LobbyService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;


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
    public SimpleObjectProperty <Game> game = new SimpleObjectProperty<>();

    @Inject
    public GameListDropDownController(
            Provider<LobbyScreenController> lobbyScreenControllerProvider) {
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
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
        LobbyScreenController lobbyScreenController = lobbyScreenControllerProvider.get();
        lobbyScreenController.deleteGame(game.get());
    }
}
