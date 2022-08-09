package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.CreateNewGamePopUpController;
import de.uniks.pioneers.controller.subcontroller.LeaveGameController;
import de.uniks.pioneers.controller.subcontroller.LobbyGameListController;
import de.uniks.pioneers.controller.subcontroller.LobbyUserlistController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static de.uniks.pioneers.Constants.*;

@Singleton
public class LobbyScreenController implements Controller {
    @FXML public ImageView AvatarImageView;
    @FXML public Label UsernameLabel;
    @FXML public ImageView RulesButton;
    @FXML public VBox UsersVBox;
    @FXML public ListView<Node> listViewGames;
    @FXML public Button EditProfileButton;
    @FXML public Button LogoutButton;
    @FXML public Button NewGameButton;

    @Inject MessageService messageService;
    @Inject Provider<EditProfileController> editProfileControllerProvider;
    @Inject Provider<LobbyUserlistController> userlistControllerProvider;
    @Inject Provider<RulesScreenController> rulesScreenControllerProvider;
    @Inject Provider<NewGameScreenLobbyController> newGameScreenLobbyControllerProvider;
    @Inject PrefService prefService;
    @Inject LeaveGameController leaveGameController;
    @Inject LobbyService lobbyService;
    @Inject UserService userService;
    @Inject NewGameLobbyService newGameLobbyService;
    @Inject Provider<CreateNewGamePopUpController> createNewGamePopUpControllerProvider;
    @Inject Provider<LobbyGameListController> lobbyGameListControllerProvider;

    @Inject Provider<MapBrowserController> mapBrowserControllerProvider;

    private final App app;
    private LobbyGameListController lobbyGameListController;

    private Stage appStage;
    public final SimpleBooleanProperty isCreatingGame = new SimpleBooleanProperty(false);
    private ChangeListener<Boolean> createGameListener;
    private Stage createNewGameStage;
    private final StylesService stylesService;

    @Inject
    public LobbyScreenController(App app, StylesService stylesService) {
        this.app = app;
        this.stylesService = stylesService;
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/LobbyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;

        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // get current user from server and display name and avatar
        User currentUser = userService.getCurrentUser();
        this.UsernameLabel.setText(currentUser.name());
        if (currentUser.avatar() != null) {
            this.AvatarImageView.setImage(new Image(currentUser.avatar()));
        } else {
            this.AvatarImageView.setImage(null);
        }

        LobbyUserlistController lobbyUserlistController = userlistControllerProvider.get();
        lobbyUserlistController.usersVBox = this.UsersVBox;
        lobbyUserlistController.render();
        lobbyUserlistController.init();
        lobbyGameListController = lobbyGameListControllerProvider.get();
        lobbyGameListController.listViewGames = this.listViewGames;
        lobbyGameListController.setup();
        return parent;
    }

    @Override
    public void init() {
        // get app and set variables
        appStage = this.app.getStage();
        appStage.setTitle(LOBBY_SCREEN_TITLE);
        appStage.setOnCloseRequest(event -> {
            logout();
            Platform.exit();
            System.exit(0);
        });

        // add listener to handle stages
        setupCreateGameListener();
        isCreatingGame.addListener(createGameListener);

        Game leavedGame = prefService.getSavedGame();
        if(leavedGame != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Rejoin Game");
            alert.setHeaderText("Would you like to rejoin your last game?");
            Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setText("Yes");
            Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            cancelButton.setText("No");
            Optional<ButtonType> option = alert.showAndWait();
            if(option.isPresent() && option.get() == ButtonType.OK) {
                Alert alertLoading = new Alert(Alert.AlertType.CONFIRMATION);
                alertLoading.setTitle("Loading");
                alertLoading.setHeaderText("Game is loading...");
                alertLoading.getButtonTypes().clear();
                alertLoading.setGraphic(new ImageView(Objects.requireNonNull(getClass().getResource("progress.gif")).toString()));

                new Thread(() -> {
                    try {
                        Platform.runLater(alertLoading::showAndWait);
                        Thread.sleep(10000);
                        Platform.runLater(() -> {
                            alertLoading.getButtonTypes().add(ButtonType.CLOSE);
                            alertLoading.close();
                        });
                        leaveGameController.loadLeavedGame(leavedGame);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        }

        app.getStage().setTitle(LOBBY_SCREEN_TITLE);
        //this-DarkMode
        String localStyle = "/de/uniks/pioneers/styles/LobbyScreen.css";
        String localStyleDark = "/de/uniks/pioneers/styles/DarkMode_LobbyScreen.css";
        stylesService.setStyleSheets(this.app.getStage().getScene().getStylesheets(), localStyle, localStyleDark);
        // set user online after login (entering lobby)
        userService.editProfile(null, null, null, "online")
                .subscribe();
        // add mouse event to buttons
        this.RulesButton.setOnMouseClicked(mouseEvent -> openRules());
        this.EditProfileButton.setOnAction(actionEvent -> editProfile());
    }

    private void setupCreateGameListener() {
        /* when create new game pop up is openend, create new game button gets disabled
         when other game is joined, close create new game stage */
        createGameListener = (observable, oldValue, newValue) -> {
            if (newValue && !oldValue) {
                NewGameButton.disableProperty().set(true);
            } else if (oldValue && !newValue) {
                NewGameButton.disableProperty().set(false);
                assert createNewGameStage != null;
                createNewGameStage.close();
            }
        };
    }

    @Override
    public void stop() {
        lobbyGameListController.stop();
        isCreatingGame.removeListener(createGameListener);
    }

    public void editProfile() {
        EditProfileController editController = editProfileControllerProvider.get();
        app.show(editController);
    }

    private void openRules() {
        RulesScreenController rulesController = rulesScreenControllerProvider.get();
        rulesController.init();
    }

    public void logout(ActionEvent ignoredEvent) {
        //This function is only called by the logout button
        this.messageService.getchatUserList().clear();
        prefService.forget();
        logout();
    }

    public void logout() {
        //This function is called when the logout button is pressed or the stage is closed
        lobbyService.logout()
                .observeOn(FX_SCHEDULER);


        // set status offline after logout (leaving lobby)
        userService.editProfile(null, null, null, "offline")
                .subscribe();

        appStage.close();
        Platform.runLater(() -> new App().start(new Stage()));
    }

    public void showNewGameLobby(Game game, String password, String hexColor) {
        NewGameScreenLobbyController newGameScreenLobbyController = newGameScreenLobbyControllerProvider.get();
        newGameScreenLobbyController.setGame(game);
        newGameScreenLobbyController.setPassword(password);
        isCreatingGame.set(false);
        app.show(newGameScreenLobbyController);
        newGameScreenLobbyController.setPlayerColor(hexColor);
    }

    public void newGame() {
        //create pop in order to create a new game lobby
        CreateNewGamePopUpController createNewGamePopUpController = createNewGamePopUpControllerProvider.get();
        Parent node = createNewGamePopUpController.render();
        createNewGameStage = new Stage();
        createNewGameStage.setTitle("create new game pop up");
        Scene scene = new Scene(node);
        createNewGameStage.setScene(scene);
        if(prefService.getDarkModeState()){
            createNewGameStage.getScene().getStylesheets().addAll(STYLE_GLOBAL_DARK,"/de/uniks/pioneers/styles/DarkMode_NewGamePopup.css");
        } else {
            createNewGameStage.getScene().getStylesheets().addAll(STYLE_GLOBAL, "/de/uniks/pioneers/styles/NewGamePopup.css");
        }
        createNewGameStage.initOwner(appStage);
        isCreatingGame.set(true);
        createNewGamePopUpController.init();
        createNewGameStage.show();
    }

    public App getApp() {
        return this.app;
    }

    public void openMapEditor() {
        MapBrowserController mapBrowserController = mapBrowserControllerProvider.get();
        app.show(mapBrowserController);
    }
}
