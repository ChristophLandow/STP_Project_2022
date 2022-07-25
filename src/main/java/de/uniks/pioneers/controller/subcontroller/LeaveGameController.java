package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.IngameScreenController;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.controller.NewGameScreenLobbyController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class LeaveGameController {
    private final App app;
    private final NewGameScreenLobbyController newGameScreenLobbyController;
    private final NewGameLobbyService newGameLobbyService;
    private final UserService userService;
    private final PrefService prefService;
    private final GameService gameService;
    private final TimerService timerService;
    private final GameStorage gameStorage;
    public List<User> users;
    private final ObservableList<Member> members;
    private final CompositeDisposable disposable = new CompositeDisposable();
    public boolean leavedWithButton;
    public String myColor;
    @Inject Provider<IngameScreenController> ingameScreenControllerProvider;
    @Inject public Provider<LobbyScreenController> lobbyScreenControllerProvider;
    public IngameScreenController ingameScreenController;
    public GameChatController gameChatController;
    private boolean onClose;
    private boolean kicked;

    @Inject
    public LeaveGameController(App app, NewGameScreenLobbyController newGameScreenLobbyController, NewGameLobbyService newGameLobbyService,
                               UserService userService, PrefService prefService, GameService gameService, TimerService timerService, GameStorage gameStorage) {
        this.app = app;
        this.newGameScreenLobbyController = newGameScreenLobbyController;
        this.newGameLobbyService = newGameLobbyService;
        this.userService = userService;
        this.prefService = prefService;
        this.gameService = gameService;
        this.timerService = timerService;
        this.gameStorage = gameStorage;
        this.users = new ArrayList<>();
        this.members = FXCollections.observableArrayList();
        this.leavedWithButton = false;
        this.myColor = "";
        this.kicked = false;
    }

    public void init(IngameScreenController ingameScreenController, GameChatController gameChatController) {
        this.ingameScreenController = ingameScreenController;
        this.gameChatController = gameChatController;
    }

    public void saveLeavedGame(String gameID, int mapRadius, List<User> users, String myColor) {
        prefService.saveGameOnLeave(gameID);
        prefService.saveMapRadiusOnLeave(mapRadius);
        this.leavedWithButton = true;
        this.users = users;
        this.myColor = myColor;
    }

    public boolean loadLeavedGame(Game leavedGame) {
        if(leavedGame != null) {
            int mapRadius = prefService.getSavedMapRadius();
            if(leavedWithButton) {
                return toIngameScreen(leavedGame, myColor, true, mapRadius);
            } else {
                disposable.add(newGameLobbyService.getAll(leavedGame._id())
                        .observeOn(FX_SCHEDULER)
                        .subscribe(res -> {
                            this.members.addAll(res);
                            for (Member member : res) {
                                if (member.userId().equals(userService.getCurrentUser()._id())) {
                                    myColor = member.color();
                                }
                                users.add(userService.getUserById(member.userId()).blockingFirst());
                            }
                            toIngameScreen(leavedGame, myColor, true, mapRadius);
                        }, Throwable::printStackTrace));
                return true;
            }
        }
        return false;
    }

    private boolean toIngameScreen(Game leavedGame, String myColor, boolean rejoin, int mapRadius) {
        timerService.reset();
        newGameScreenLobbyController.toIngame(leavedGame, users, myColor, rejoin, mapRadius);
        return true;
    }

    public void leave() {
        LobbyScreenController newLobbyController = lobbyScreenControllerProvider.get();
        if(gameService.getGame().owner().equals(userService.getCurrentUser()._id())) {
            gameChatController.sendMessage("Host left the Game!", gameService.getGame());
            disposable.add(gameService.deleteGame(gameService.getGame()._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> {
                        ingameScreenController.stop();
                        disposable.dispose();
                        if(!onClose) {
                            app.show(newLobbyController);
                        }
                    }, Throwable::printStackTrace));
        } else {
            if(!kicked) {
                this.saveLeavedGame(gameService.getGame()._id(), gameStorage.getMapRadius(), users, myColor);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Kicked by Host");
                alert.show();
            }
            userService.setSpectator(false);
            ingameScreenController.stop();
            timerService.reset();
            disposable.dispose();
            if(!onClose) {
                app.show(newLobbyController);
            }
        }
    }

    public void leaveAfterVictory() {
        LobbyScreenController newLobbyController = lobbyScreenControllerProvider.get();
        if(gameService.getGame().owner().equals(userService.getCurrentUser()._id())) {
            disposable.add(gameService.deleteGame(gameService.getGame()._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> {
                        ingameScreenController.stop();
                        disposable.dispose();
                        app.show(newLobbyController);
                    }, Throwable::printStackTrace));
        } else {
            userService.setSpectator(false);
            ingameScreenController.stop();
            timerService.reset();
            disposable.dispose();
            app.show(newLobbyController);
        }
    }

    public void setKicked(boolean kicked) {
        this.kicked = kicked;
    }

    public void setOnClose(boolean onClose) {
        this.onClose = onClose;
    }
}
