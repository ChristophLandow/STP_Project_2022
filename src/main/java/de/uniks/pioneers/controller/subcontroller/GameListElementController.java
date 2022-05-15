package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;

public class GameListElementController {
    @FXML
    public HBox gameBoxRoot;
    @FXML
    public Label creationTime;
    @FXML
    public Label title;
    @FXML
    public Label memberCount;

    private LobbyScreenController lobbyScreenController;
    private ObservableList<Game> games;
    private Game game;
    private User creator;


    public void createOrUpdateGame(Game game, ObservableList<Game> games,
                                   ObservableList<User> users, LobbyScreenController lobbyScreenController) {

        initGameAndUser(game, games, users, lobbyScreenController);
        // might be better with rex ex, i gona update this
        String createdAt = game.createdAt();
        int start = createdAt.indexOf("T");
        int end = createdAt.indexOf(".");
        String time = game.createdAt().substring(start + 1, end) + " :";
        creationTime.setText(time);
        // set title to textfield
        title.setText(game.name());
        // set member count to textfield
        String actualMemberCount = String.format("%d/4", game.members());
        memberCount.setText(actualMemberCount);
    }


    private void initGameAndUser(Game game, ObservableList<Game> games,
                                 ObservableList<User> users, LobbyScreenController lobbyScreenController) {
        if (this.lobbyScreenController == null) {
            this.game = game;
            this.games = games;
            this.lobbyScreenController = lobbyScreenController;
            try {
                User creator = users.stream().filter(user -> user._id().equals(game.owner())).findAny().get();
                this.creator = creator;
            } catch (Exception e) {
                this.creator = null;
            }
        } else {
            return;
        }
    }


    private void joinGame(MouseEvent mouseEvent) {
        lobbyScreenController.showNewGameLobby(game);
    }

    private void discardGame(MouseEvent mouseEvent) {
        games.remove(game);
    }

    public Game getGame() {
        return game;
    }

    public void showDropDown(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                //join game
                lobbyScreenController.showNewGameLobby(game);
            }
        } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
            final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/GameListDropDown.fxml"));
            final VBox gameOptions;
            try {
                gameOptions = loader.load();
                GameListDropDownController gameListDropDownController = loader.getController();
                gameListDropDownController.init(this, lobbyService);
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
        if (this.creator != null && this.creator.name() != null) {
            String toolTipTxt = String.format("Created by: %s  id: %s", creator.name(), game._id());
            tt.setText(toolTipTxt);
        } else {
            String toolTipTxt = String.format("Created by: %s  id: %s", "unknown", game._id());
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
}
