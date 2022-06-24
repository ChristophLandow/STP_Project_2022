package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Constants;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.controller.NewGameScreenLobbyController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javax.inject.Provider;
import java.util.Map;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class NewGameLobbyUserController {
    private NewGameScreenLobbyController screenController;
    private Map<String, PlayerEntryController> playerEntries;
    private NewGameLobbyService newGameLobbyService;
    private UserService userService;
    private VBox userBox;
    private Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private EventListener eventListener;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public void init(NewGameScreenLobbyController newGameScreenLobbyController, Map<String, PlayerEntryController> playerEntries, NewGameLobbyService newGameLobbyService,
                     UserService userService, VBox userBox, Provider<LobbyScreenController> lobbyScreenControllerProvider, EventListener eventListener) {
        this.newGameLobbyService = newGameLobbyService;
        this.userService = userService;
        this.screenController = newGameScreenLobbyController;
        this.playerEntries = playerEntries;
        this.userBox = userBox;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.eventListener = eventListener;

        this.initMemberListener();
        this.initGameListener();
    }

    public void deleteUser(Member member) {
        Node removal = userBox.getChildren().stream().filter(node -> node.getId().equals(member.userId())).findAny().orElse(null);
        userBox.getChildren().remove(removal);
        playerEntries.remove(member.userId());
        newGameLobbyService.getUsers().remove(member.userId());

        if (member.userId().equals(screenController.getGame().owner()) && !userService.getCurrentUser()._id().equals(screenController.getGame().owner())) {
            screenController.getApp().show(lobbyScreenControllerProvider.get());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, Constants.HOST_LEFT_GAME_ALERT);
            alert.showAndWait();
        }
    }

    public void renderUser(Member member) {
        if (!newGameLobbyService.getUsers().containsKey(member.userId())) {
            User user = userService.getUserById(member.userId()).blockingFirst();
            // when we make the application multi stage, we need a userlistener or if a user dies
            this.initUserListener(user);

            newGameLobbyService.getUsers().put(user._id(), user);

            Image userImage;
            try {
                userImage = new Image(user.avatar());
            } catch (IllegalArgumentException | NullPointerException e) {
                userImage = new Image(Constants.DEFAULT_AVATAR);
            }

            if (!userService.getCurrentUser()._id().equals(member.userId())) {
                PlayerEntryController playerEntryController = new PlayerEntryController(userImage, user.name(), member.color(), user._id());
                playerEntryController.setReady(member.ready(), member.spectator());
                playerEntries.put(user._id(), playerEntryController);
                userBox.getChildren().add(playerEntryController.getPlayerEntry());
                if(userBox.getChildren().size() > 3) {
                    userBox.setPrefHeight(userBox.getPrefHeight() + 60);
                }
            }
        }
    }

    public void initUserListener(User user) {
        String patternToObserveGameUsers = String.format("users.%s.updated", user._id());
        disposable.add(eventListener.listen(patternToObserveGameUsers, User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(userEvent -> {
                    User userFromEvent = userEvent.data();
                    if(userFromEvent.status().equals("offline") && screenController.getGame().owner().equals(userService.getCurrentUser()._id())) {
                        disposable.add(newGameLobbyService.deleteMember(screenController.getGame()._id(), userFromEvent._id())
                                .observeOn(FX_SCHEDULER)
                                .subscribe(this::deleteUser, Throwable::printStackTrace));
                    }
                })
        );
    }

    private void initMemberListener() {
        String patternToObserveGameMembers = String.format("games.%s.members.*.*", screenController.getGame()._id());
        disposable.add(eventListener.listen(patternToObserveGameMembers, Member.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(memberEvent -> {
                    final Member member = memberEvent.data();
                    if (memberEvent.event().endsWith(".created")) {
                        newGameLobbyService.getMembers().add(member);
                    } else if (memberEvent.event().endsWith(".updated")) {
                        newGameLobbyService.getMembers().replaceAll(m -> m.userId().equals(member.userId()) ? member : m);
                        this.setReadyColor(member.userId(), member.ready(), member.color(), member.spectator());
                    } else if (memberEvent.event().endsWith(".deleted")) {
                        newGameLobbyService.getMembers().remove(member);
                    }
                }));
    }

    private void initGameListener() {
        if(screenController != null) {
            String patternToObserveGame = String.format("games.%s.*", screenController.getGame()._id());
            disposable.add(eventListener.listen(patternToObserveGame, Game.class)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(gameEvent -> {
                        screenController.setGame(gameEvent.data());
                        screenController.setMemberCount(screenController.getGame().members());
                        if (gameEvent.event().endsWith(".updated") && gameEvent.data().started()) {
                            screenController.toIngame(screenController.getGame(), newGameLobbyService.getUsers().values().stream().toList(), screenController.getColorPickerController().getColor());
                        } else if (gameEvent.event().endsWith(".deleted")) {
                            screenController.getApp().show(screenController.getLobbyScreenController());
                            screenController.stop();
                            newGameLobbyService.leave();
                        }
                    })
            );
        }
    }

    public void setReadyColor(String memberId, boolean ready, String hexColor, boolean spectator) {
        if(playerEntries.containsKey(memberId)) {
            playerEntries.get(memberId).setReady(ready, spectator);
            playerEntries.get(memberId).setColor(hexColor);
        }
    }

    public void stop() {
        disposable.dispose();
    }
}
