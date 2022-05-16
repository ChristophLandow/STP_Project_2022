package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.NewGameLobbyService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class GameListElementController {
    @FXML
    public HBox gameBoxRoot;
    @FXML
    public TextField creationTime;
    @FXML
    public TextField title;
    @FXML
    public TextField memberCount;

    private LobbyScreenController lobbyScreenController;

    private NewGameLobbyService newGameLobbyService;
    private ObservableList<Game> games;
    private Game game;
    private User creator;


    public void createOrUpdateGame(Game game, ObservableList<Game> games,
                                   ObservableList<User> users, LobbyScreenController lobbyScreenController, NewGameLobbyService newGameLobbyService) {
        // needs to be fixed
        // now mouse hovering is blocked !
        title.setMouseTransparent(true);

        initGameAndUser(game,games,users,lobbyScreenController, newGameLobbyService);
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
                                 ObservableList<User> users, LobbyScreenController lobbyScreenController, NewGameLobbyService newGameLobbyService) {
        if (this.lobbyScreenController==null){
            this.game=game;
            this.games=games;
            this.lobbyScreenController=lobbyScreenController;
            this.newGameLobbyService = newGameLobbyService;
            try {
                User creator = users.stream().filter(user -> user._id().equals(game.owner())).findAny().get();
                this.creator=creator;
            } catch (Exception e) {
                this.creator=null;
            }
        }else {
            return;
        }
    }


    public void onMouseClicked(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
            if(mouseEvent.getClickCount() == 2){
                //join game
                joinGame(mouseEvent);
            }
        } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)){
            // show options in a small list (join game, discard from list, show more game details
            // need a fxml !
            VBox gameOption = new VBox();
            gameOption.setMinSize(200,200);
            gameOption.setVisible(true);
            gameOption.setBackground(new Background(new BackgroundFill(Color.SILVER,null,null)));


            HBox joinGameBox = new HBox();
            Label joinGame = new Label("join game");
            joinGameBox.getChildren().add(joinGame);
            joinGameBox.setOnMouseClicked(this::joinGame);

            HBox discardGameBox = new HBox();
            Label discardGame = new Label("discard game");
            discardGameBox.getChildren().add(discardGame);
            discardGameBox.setOnMouseClicked(this::discardGame);

            gameOption.getChildren().add(0,joinGameBox);
            gameOption.getChildren().add(1,discardGameBox);

            Scene scene = gameBoxRoot.getScene();

            Pane pane  = (Pane) scene.lookup("#root");
            pane.getChildren().add(gameOption);
            double xPos = mouseEvent.getSceneX();
            double yPos = mouseEvent.getSceneY();
            gameOption.setLayoutX(xPos-10);
            gameOption.setLayoutY(yPos-10);

            gameOption.setOnMouseExited(event -> pane.getChildren().remove(gameOption));

        }
    }

    private void joinGame(MouseEvent mouseEvent) {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/JoinGamePopUp.fxml"));
        Parent node = null;
        try {
            node = loader.load();
            JoinGamePopUpController joinGamePopUpController = loader.getController();
            joinGamePopUpController.init(newGameLobbyService, lobbyScreenController, game);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Join Game");
        Scene scene = new Scene(node);
        stage.setScene(scene);
        stage.show();
    }

    private void discardGame(MouseEvent mouseEvent) {
        games.remove(game);
    }

    public void showGameInfo(MouseEvent mouseEvent) {
        Tooltip tt = new Tooltip();
        if (this.creator!=null && this.creator.name()!=null){
            String toolTipTxt = String.format("Created by: %s  id: %s", creator.name(),game._id());
            tt.setText(toolTipTxt);
        } else {
            String toolTipTxt = String.format("Created by: %s  id: %s", "unknown",game._id());
            tt.setText(toolTipTxt);
        }

        tt.setStyle("-fx-font: normal bold 4 Langdon; "
                + "-fx-base: #AE3522; "
                + "-fx-text-fill: orange;"
                + "-fx-font-size: 14");

        tt.setShowDelay(Duration.seconds(0.3));
        title.setTooltip(tt);
    }

    public void disableGameInfo(MouseEvent mouseEvent) {
        title.setTooltip(null);
    }


    public Game getGame() {
        return game;
    }

}
