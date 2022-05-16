package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.LobbyService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;


public class GameListElementController implements Controller {
    @FXML
    public HBox gameBoxRoot;
    @FXML
    public Label creationTime;
    @FXML
    public Label title;
    @FXML
    public Label memberCount;


    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final Provider<LobbyService> lobbyServiceProvider;
    private final Provider<GameListDropDownController> gameListDropDownControllerProvider;

    public SimpleObjectProperty<User> creator = new SimpleObjectProperty<>();
    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();

    @Inject
    public GameListElementController(Provider<LobbyScreenController> lobbyScreenControllerProvider,
                                     Provider<LobbyService> lobbyServiceProvider,
                                     Provider<GameListDropDownController> GameListDropDownControllerProvider) {
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.lobbyServiceProvider = lobbyServiceProvider;
        this.gameListDropDownControllerProvider = GameListDropDownControllerProvider;
        //this.game.addListener((ChangeListener) (game, oldVal, newVal) -> setDataToGameListElement());
    }

    public void setDataToGameListElement() {
        // might be better with reg ex, i gona update this

        // set game createdAt to creationTime label
        String createdAt = game.get().createdAt();
        int start = createdAt.indexOf("T");
        int end = createdAt.indexOf(".");
        String time = game.get().createdAt().substring(start + 1, end) + " :";
        creationTime.setText(time);
        // set game title to title label
        title.setText(game.get().name());
        // set game member count to memberCount label
        String actualMemberCount = String.format("%d/4", game.get().members());
        memberCount.setText(actualMemberCount);
    }


    private void joinGame(MouseEvent mouseEvent) {
        lobbyScreenControllerProvider.get().showNewGameLobby(game.get());
    }

    private void discardGame(MouseEvent mouseEvent) {
        ObservableList<Game> games = lobbyServiceProvider.get().gamesProperty.get();
        games.remove(game.get());
    }


    public void showDropDown(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                //join game
                lobbyScreenControllerProvider.get().showNewGameLobby(game.get());
            }
        } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
            final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/GameListDropDown.fxml"));
            final VBox gameOptions;
            try {
                gameOptions = loader.load();
                GameListDropDownController gameListDropDownController = gameListDropDownControllerProvider.get();
                gameOptions.setBackground(new Background(new BackgroundFill(Color.SILVER, null, null)));
                Scene scene = gameBoxRoot.getScene();
                Pane pane = (Pane) scene.lookup("#root");
                pane.getChildren().add(gameOptions);
                double xPos = mouseEvent.getSceneX();
                double yPos = mouseEvent.getSceneY();
                gameOptions.setLayoutX(xPos - 10);
                gameOptions.setLayoutY(yPos - 10);
                gameOptions.setOnMouseExited(event -> {
                    pane.getChildren().remove(gameOptions);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showGameInfo(MouseEvent mouseEvent) {
        Tooltip tt = new Tooltip();
        if (this.creator != null ) {
            String toolTipTxt = String.format("Created by: %s  id: %s", creator.get().name(), game.get()._id());
            tt.setText(toolTipTxt);
        } else {
            String toolTipTxt = String.format("Created by: %s  id: %s", "unknown", game.get()._id());
            tt.setText(toolTipTxt);
        }
        // style not final
        tt.setStyle("-fx-font: normal bold 4 Langdon; "
                + "-fx-base: #AE3522; "
                + "-fx-text-fill: orange;"
                + "-fx-font-size: 14");

        tt.setShowDelay(Duration.seconds(0.3));
        title.setTooltip(tt);
    }

    public void dontShowGameInfo(MouseEvent mouseEvent) {
        title.setTooltip(null);
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Parent render() {
        Parent parent=null;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/GameListElement.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return parent;
    }
}