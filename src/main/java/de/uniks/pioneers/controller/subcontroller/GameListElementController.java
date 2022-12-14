package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.EventHandlerService;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.UserService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

public class GameListElementController implements Controller {
    @FXML public HBox gameBoxRoot;
    @FXML public Label creationTime;
    @FXML public Label title;
    @FXML public Label memberCount;
    @Inject PrefService prefService;
    @Inject LeaveGameController leaveGameController;

    private final App app;
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final Provider<NewGameLobbyService> newGameLobbyServiceProvider;
    private final UserService userService;
    public final SimpleObjectProperty<User> creator = new SimpleObjectProperty<>();
    public final SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();

    @Inject
    public GameListElementController(App app, Provider<LobbyScreenController> lobbyScreenControllerProvider, Provider<NewGameLobbyService> newGameLobbyServiceProvider, UserService userService) {
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.newGameLobbyServiceProvider = newGameLobbyServiceProvider;
        this.userService = userService;
        this.game.addListener((game, oldVal, newVal) -> setDataToGameListElement());
        this.app = app;
    }

    @Override
    public Parent render() {
        Parent parent = null;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/GameListElement.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return parent;
    }

    @Override
    public void init() {
    }

    @Override
    public void stop() {
    }

    public void setDataToGameListElement() {
        // set game createdAt to creationTime label
        String createdAt = game.get().createdAt();
        int start = createdAt.indexOf("T");
        int end = createdAt.indexOf(".");
        String time = game.get().createdAt().substring(start+1, end) + " :";
        creationTime.setText(time);
        // set game title to title label
        title.setText(game.get().name());
        // set game member count to memberCount label
        String actualMemberCount = String.format("%d", game.get().members());
        memberCount.setText(actualMemberCount);
    }

    public void doubleClicked(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if(mouseEvent.getClickCount() == 2) {
                Game leavedGame = prefService.getSavedGame();
                if(leavedGame != null && leavedGame._id().equals(game.get()._id())) {
                    leaveGameController.loadLeavedGame(leavedGame);
                } else {
                    //join game
                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/JoinGamePopUp.fxml"));
                    Parent node = null;
                    try {
                        node = loader.load();
                        JoinGamePopUpController joinGamePopUpController = loader.getController();
                        joinGamePopUpController.init(this.app, newGameLobbyServiceProvider.get(), lobbyScreenControllerProvider.get(), game.get(), new EventHandlerService(), userService);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Join Game");
                    assert node != null;
                    Scene scene = new Scene(node);
                    stage.setScene(scene);
                    if(prefService.getDarkModeState()){
                        scene.getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/JoinGamePopup.css")));
                        scene.getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_JoinGamePopup.css");
                    } else {
                        scene.getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_JoinGamePopup.css")));
                        scene.getStylesheets().add("/de/uniks/pioneers/styles/JoinGamePopup.css");
                    }
                    stage.show();
                }
            }
        }
    }
    public App getApp(){
        return this.app;
    }
}
