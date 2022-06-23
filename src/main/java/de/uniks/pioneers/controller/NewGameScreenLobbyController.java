package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Constants;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.ColorPickerController;
import de.uniks.pioneers.controller.subcontroller.GameChatController;
import de.uniks.pioneers.controller.subcontroller.PlayerEntryController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.*;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class NewGameScreenLobbyController implements Controller {
    @FXML public Pane root;
    @FXML public VBox vBoxRoot;
    @FXML public HBox topLevel;
    @FXML public VBox leftBox;
    @FXML public Label gameNameLabel;
    @FXML public Label passwordLabel;
    @FXML public VBox userBox;
    @FXML public VBox rightBox;
    @FXML public VBox messageBox;
    @FXML public ScrollPane chatScrollPane;
    @FXML public HBox messageHbox;
    @FXML public TextField messageText;
    @FXML public Button sendButton;
    @FXML public HBox buttonBox;
    @FXML public Button readyButton;
    @FXML public Button startGameButton;
    @FXML public Button leaveButton;
    @FXML public ImageView RulesButton;
    @FXML public HBox clientReadyBox;
    @FXML public Label clientReadyLabel;
    @FXML public ColorPicker colorPicker;
    @FXML public SVGPath houseSVG;
    @FXML public ImageView spectatorImageView;
    @FXML public ImageView clientAvatar;
    @FXML public Label clientUserNameLabel;
    @FXML public CheckBox spectatorCheckBox;

    @Inject Provider<LobbyScreenController> lobbyScreenControllerProvider;
    @Inject Provider<GameChatController> gameChatControllerProvider;
    @Inject Provider<IngameScreenController> ingameScreenControllerProvider;
    @Inject Provider<LoginScreenController> loginScreenControllerProvider;
    @Inject ColorPickerController colorPickerController;
    @Inject PrefService prefService;

    private final EventListener eventListener;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;
    private final NewGameLobbyService newGameLobbyService;
    private final App app;
    private final UserService userService;
    private final GameService gameService;
    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    public SimpleStringProperty password = new SimpleStringProperty();
    public SimpleIntegerProperty memberCount = new SimpleIntegerProperty();
    private User currentUser;
    private final Map<String, PlayerEntryController> playerEntries = new HashMap<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private GameChatController gameChatController;
    private boolean clientReady = false;

    @Inject
    public NewGameScreenLobbyController(EventListener eventListener, Provider<RulesScreenController> rulesScreenControllerProvider,
                                        NewGameLobbyService newGameLobbyService, App app, UserService userService, GameService gameService) {
        this.eventListener = eventListener;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
        this.newGameLobbyService = newGameLobbyService;
        this.app = app;
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void init() {
        newGameLobbyService.setNewGameScreenLobbyController(this);

        if(prefService.getDarkModeState()){
            app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/NewGameScreen.css")));
            app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_NewGameScreen.css");
        } else {
            app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_NewGameScreen.css")));
            app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/NewGameScreen.css");
        }
        //set game name label and password text label
        gameNameLabel.setText(game.get().name());
        passwordLabel.setText(password.get());
        clientUserNameLabel.setText(currentUser.name());
        colorPickerController.init(colorPicker, houseSVG);
        this.reactivateReadyButton();

        // enable deleting game on close request
        Stage stage = this.app.getStage();
        stage.setOnCloseRequest(event -> {
            if (game.get().owner().equals(currentUser._id())) {
                disposable.add(gameService.deleteGame(game.get()._id())
                        .observeOn(FX_SCHEDULER)
                        .subscribe());
            }

            newGameLobbyService.logout();
            disposable.add(userService.editProfile(null, null, null, "offline")
                    .subscribe(user -> {
                        Platform.exit();
                        System.exit(0);
                            }));
        });

        try {
            clientAvatar.setImage(new Image(userService.getCurrentUser().avatar()));
        } catch (IllegalArgumentException | NullPointerException e) {
            clientAvatar.setImage(new Image(Constants.DEFAULT_AVATAR));
        }

        // when member count less than three games can not be started
        final BooleanBinding lessThanThree = Bindings.lessThan(memberCount, 0);
        startGameButton.disableProperty().bind(lessThanThree);

        // add mouse event for rules button
        this.RulesButton.setOnMouseClicked(this::openRules);

        // add listener for member observable
        newGameLobbyService.getMembers().addListener((ListChangeListener<? super Member>) c -> {
            c.next();
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(this::renderUser);
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(this::deleteUser);
            }
        });

        disposable.add(newGameLobbyService.getAll(game.get()._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(newGameLobbyService.getMembers()::setAll, Throwable::printStackTrace));

        // init game chat controller
        gameChatController = gameChatControllerProvider.get()
                .setChatScrollPane(this.chatScrollPane)
                .setMessageText(this.messageText)
                .setMessageBox(this.messageBox)
                .setSendButton(this.sendButton)
                .setGame(this.game.get())
                .setUsers(newGameLobbyService.getUsers().values().stream().toList());
        gameChatController.render();
        gameChatController.init();
    }

    private void openRules(MouseEvent mouseEvent) {
        RulesScreenController rulesController = rulesScreenControllerProvider.get();
        rulesController.init();
    }

    public void deleteUser(Member member) {
        Node removal = userBox.getChildren().stream().filter(node -> node.getId().equals(member.userId())).findAny().orElse(null);
        userBox.getChildren().remove(removal);
        playerEntries.remove(member.userId());
        newGameLobbyService.getUsers().remove(member.userId());

        if (member.userId().equals(game.get().owner()) && !userService.getCurrentUser()._id().equals(game.get().owner())) {
            app.show(lobbyScreenControllerProvider.get());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, Constants.HOST_LEFT_GAME_ALERT);
            alert.showAndWait();
        }
    }

    private void renderUser(Member member) {
        if (!newGameLobbyService.getUsers().containsKey(member.userId())) {
            User user = userService.getUserById(member.userId()).blockingFirst();
            // when we make the application multi stage, we need a userlistener or if a user dies
            newGameLobbyService.initUserListener(user);

            newGameLobbyService.getUsers().put(user._id(), user);

            Image userImage;
            try {
                userImage = new Image(user.avatar());
            } catch (IllegalArgumentException | NullPointerException e) {
                userImage = new Image(Constants.DEFAULT_AVATAR);
            }

            if (!currentUser._id().equals(member.userId())) {
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

    @Override
    public void stop() {
        gameChatController.stop();
        disposable.dispose();
    }

    @Override
    public Parent render() {
        // Parent parent;
        currentUser = userService.getCurrentUser();
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/NewGameLobbyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent view;
        try {
            view = loader.load();

            // set start button invisible if currentUser is not gameOwner
            if (!currentUser._id().equals(game.get().owner())) {
                startGameButton.setVisible(false);
                spectatorCheckBox.setVisible(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return view;
    }

    public boolean onSetReadyButton() {
        // set member "ready" true in API
        boolean difference = true;

        if(!spectatorCheckBox.isSelected()) {
            for(PlayerEntryController entry : playerEntries.values()) {
                if(entry.getReady() && !colorPickerController.checkColorDifference(entry.getPlayerColor()) && !entry.getSpectator()) {
                    difference = false;
                    break;
                }
            }
        }

        if (difference) {
            clientReady = !clientReady;
            spectatorCheckBox.setDisable(clientReady);
            disposable.add(newGameLobbyService.patchMember(game.get()._id(), currentUser._id(), clientReady, colorPickerController.getColor(), spectatorCheckBox.isSelected())
                    .observeOn(FX_SCHEDULER)
                            .doOnError(Throwable::printStackTrace)
                    .subscribe(result -> {
                        if (clientReady) {
                            clientReadyLabel.setText("Ready");
                            clientReadyBox.setBackground(Background.fill(Color.GREEN));
                            if(!spectatorImageView.isVisible()) {
                                colorPickerController.setDisable(true);
                            }
                        } else {
                            clientReadyLabel.setText("Not Ready");
                            clientReadyBox.setBackground(Background.fill(Color.RED));
                            if(spectatorImageView.isVisible()) {
                                colorPickerController.setDisable(true);
                            } else {
                                colorPickerController.setDisable(false);
                            }
                        }
                    }, Throwable::printStackTrace));
            this.reactivateReadyButton();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Selected color is too similar to another player's color!");
            alert.showAndWait();
        }

        return difference;
    }

    public void setReadyColor(String memberId, boolean ready, String hexColor, boolean spectator) {
        if(playerEntries.containsKey(memberId)) {
            playerEntries.get(memberId).setReady(ready, spectator);
            playerEntries.get(memberId).setColor(hexColor);
        }
    }

    public void startGame() {
        // check if all users are ready
        if (allUsersReady()) {
            disposable.add(newGameLobbyService.updateGame(game.get(), password.get(), true)
                    .observeOn(FX_SCHEDULER)
                    .doOnError(Throwable::printStackTrace)
                    .subscribe(response -> this.toIngame(this.game.get(), newGameLobbyService.getUsers().values().stream().toList(), colorPickerController.getColor()), Throwable::printStackTrace));
        }
    }

    public void toIngame(Game game, List<User> users, String myColor) {
        IngameScreenController ingameScreenController = ingameScreenControllerProvider.get();
        ingameScreenController.game.set(game);
        ingameScreenController.loadMap();
        ingameScreenController.setUsers(users);
        app.show(ingameScreenController);
        ingameScreenController.setPlayerColor(myColor);
    }

    public boolean allUsersReady() {
        boolean playersReady = true;

        for (PlayerEntryController entry : playerEntries.values()) {
            if (!entry.getReady()) {
                playersReady = false;
                break;
            }
        }

        // check if there is a checkmark
        if (clientReady && playersReady) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Not all players are ready yet!");
            alert.showAndWait();
            return false;
        }
    }

    public void leaveLobby() {
        if (game.get().owner().equals(currentUser._id())) {
            disposable.add(gameService.deleteGame(game.get()._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> app.show(lobbyScreenControllerProvider.get()), Throwable::printStackTrace));
        } else {
            disposable.add(newGameLobbyService.deleteMember(game.get()._id(), currentUser._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> app.show(lobbyScreenControllerProvider.get()), Throwable::printStackTrace));
        }

        newGameLobbyService.leaveLobby();
    }

    public void onColorChange() {
        colorPickerController.setColor();
    }

    public void setPlayerColor(String hexColor) {
        colorPickerController.setColor(hexColor);

        if (game.get().owner().equals(currentUser._id())) {
            disposable.add(newGameLobbyService.patchMember(game.get()._id(), currentUser._id(), clientReady, colorPickerController.getColor(), false)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                    }, Throwable::printStackTrace));
        }
    }

    private void reactivateReadyButton() {
        this.readyButton.setDisable(true);
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                readyButton.setDisable(false);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public App getApp() {
        return this.app;
    }

    public Game getGame() {
        return game.get();
    }

    public void setGame(Game game) {
        this.game.set(game);
    }

    public void setMemberCount(int count) {
        this.memberCount.set(count);
    }

    public ColorPickerController getColorPickerController() {
        return colorPickerController;
    }

    public LobbyScreenController getLobbyScreenController() {
        return lobbyScreenControllerProvider.get();
    }

    public void onCheckBoxClicked() {
        houseSVG.setVisible(!spectatorCheckBox.isSelected());
        spectatorImageView.setVisible(spectatorCheckBox.isSelected());
        colorPicker.setDisable(spectatorCheckBox.isSelected());
    }
}