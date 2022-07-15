package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Constants;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.*;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.*;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.*;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class NewGameScreenLobbyController implements Controller {
    @FXML public Pane root;
    @FXML public VBox vBoxRoot, leftBox, userBox, rightBox, messageBox;
    @FXML public HBox topLevel, messageHbox, buttonBox, clientReadyBox;
    @FXML public Label clientUserNameLabel, clientReadyLabel, gameNameLabel, passwordLabel, boardSizeLabel, victoryPointsLabel;
    @FXML public ScrollPane chatScrollPane;
    @FXML public TextField messageText;
    @FXML public Button sendButton, readyButton, startGameButton, leaveButton;
    @FXML public ColorPicker colorPicker;
    @FXML public SVGPath houseSVG;
    @FXML public ImageView RulesButton, spectatorImageView, clientAvatar;
    @FXML public CheckBox spectatorCheckBox;
    @FXML public Spinner<Integer> boardSizeSpinner, victoryPointSpinner;

    @Inject Provider<LobbyScreenController> lobbyScreenControllerProvider;
    @Inject GameChatController gameChatController;
    @Inject Provider<IngameScreenController> ingameScreenControllerProvider;
    @Inject Provider<LoginScreenController> loginScreenControllerProvider;
    @Inject ColorPickerController colorPickerController;
    @Inject PrefService prefService;
    @Inject EventListener eventListener;
    @Inject Provider<RulesScreenController> rulesScreenControllerProvider;
    @Inject NewGameLobbyService newGameLobbyService;
    @Inject UserService userService;
    @Inject GameStorage gameStorage;
    @Inject GameService gameService;

    private final SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    private final SimpleStringProperty password = new SimpleStringProperty();
    private final App app;
    public final SimpleIntegerProperty memberCount = new SimpleIntegerProperty();
    private User currentUser;
    private final Map<String, PlayerEntryController> playerEntries = new HashMap<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private NewGameLobbyReadyController newGameLobbyReadyController;
    private NewGameLobbyUserController newGameLobbyUserController;

    @Inject
    public NewGameScreenLobbyController(App app) {
        this.app = app;
    }

    @Override
    public void init() {
        newGameLobbyReadyController = new NewGameLobbyReadyController();
        newGameLobbyReadyController.init(this, spectatorCheckBox, playerEntries, colorPickerController, clientReadyLabel, clientReadyBox, readyButton, startGameButton, spectatorImageView, boardSizeSpinner, victoryPointSpinner, newGameLobbyService, userService);
        newGameLobbyUserController = new NewGameLobbyUserController();
        newGameLobbyUserController.init(this, playerEntries, newGameLobbyService, userService, userBox, lobbyScreenControllerProvider, eventListener);

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
        newGameLobbyReadyController.reactivateReadyButton();

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
            if(c.wasAdded()) {
                c.getAddedSubList().forEach(newGameLobbyUserController::renderUser);
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(newGameLobbyUserController::deleteUser);
            }
        });

        disposable.add(newGameLobbyService.getAll(game.get()._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(newGameLobbyService.getMembers()::setAll, Throwable::printStackTrace));

        // init game chat controller
        gameChatController.setChatScrollPane(this.chatScrollPane);
        gameChatController.setMessageText(this.messageText);
        gameChatController.setMessageBox(this.messageBox);
        gameChatController.setSendButton(this.sendButton);
        gameChatController.setGame(this.game.get());
        gameChatController.setUsers(newGameLobbyService.getUsers().values().stream().toList());
        gameChatController.render();
        gameChatController.init();

        boardSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,10));
        boardSizeSpinner.editorProperty().get().setAlignment(Pos.CENTER);
        boardSizeSpinner.getValueFactory().setValue(2);

        victoryPointSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3,15,10));
        victoryPointSpinner.editorProperty().get().setAlignment(Pos.CENTER);

        if(!currentUser._id().equals(game.get().owner())){
            boardSizeSpinner.setVisible(false);
            boardSizeLabel.setVisible(false);
            victoryPointsLabel.setVisible(false);
            victoryPointSpinner.setVisible(false);
        }
    }

    private void openRules(MouseEvent mouseEvent) {
        RulesScreenController rulesController = rulesScreenControllerProvider.get();
        rulesController.init();
    }

    @Override
    public void stop() {
        gameChatController.stop();
        disposable.dispose();
        if(newGameLobbyUserController != null) {
            newGameLobbyUserController.stop();
        }
        if(newGameLobbyReadyController != null) {
            newGameLobbyReadyController.stop();
        }
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

    public void toIngame(Game game, List<User> users, String myColor, boolean rejoin, int mapRadius) {
        if(!rejoin) {
            gameStorage.resetRemainingBuildings();
            gameStorage.calcZoom(boardSizeSpinner.getValue());
        } else {
            gameStorage.calcZoom(mapRadius);
        }
        if(game.owner().equals(userService.getCurrentUser()._id())) {
            gameService.victoryPoints = victoryPointSpinner.getValue();
        }
        gameService.setMembers(newGameLobbyService.getMembers());
        IngameScreenController ingameScreenController = ingameScreenControllerProvider.get();
        ingameScreenController.game.set(game);
        ingameScreenController.loadMap();
        ingameScreenController.setUsers(users);
        app.show(ingameScreenController);
        this.stop();
        newGameLobbyService.leave();
        ingameScreenController.setPlayerColor(myColor);
    }

    public void leaveLobby() {
        if (game.get().owner().equals(currentUser._id())) {
            disposable.add(gameService.deleteGame(game.get()._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> {
                        app.show(lobbyScreenControllerProvider.get());
                        this.stop();
                        newGameLobbyService.leave();
                    }, Throwable::printStackTrace));
        } else {
            disposable.add(newGameLobbyService.deleteMember(game.get()._id(), currentUser._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> {
                        app.show(lobbyScreenControllerProvider.get());
                        this.stop();
                        newGameLobbyService.leave();
                    }, Throwable::printStackTrace));
        }
    }

    public void onColorChange() {
        colorPickerController.setColor();
    }

    public void setPlayerColor(String hexColor) {
        colorPickerController.setColor(hexColor);

        if (game.get().owner().equals(currentUser._id())) {
            disposable.add(newGameLobbyService.patchMember(game.get()._id(), currentUser._id(), newGameLobbyReadyController.getReady(), colorPickerController.getColor(), false)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                    }, Throwable::printStackTrace));
        }
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
        userService.setSpectator(spectatorCheckBox.isSelected());
    }

    public String getPassword() {
        return this.password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }
}