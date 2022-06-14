package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.CreateNewGamePopUpController;
import de.uniks.pioneers.controller.subcontroller.LobbyGameListController;
import de.uniks.pioneers.controller.subcontroller.LobbyUserlistController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.LOBBY_SCREEN_TITLE;

@Singleton
public class LobbyScreenController implements Controller {

    @FXML
    public ImageView AvatarImageView;
    @FXML
    public Label UsernameLabel;
    @FXML
    public ImageView RulesButton;
    @FXML
    public VBox UsersVBox;
    @FXML
    public ListView<Node> listViewGames;
    @FXML
    public Button EditProfileButton;
    @FXML
    public Button LogoutButton;
    @FXML
    public Button NewGameButton;

    App app;

    @Inject
    MessageService messageService;
    @Inject
    Provider<LoginScreenController> loginScreenControllerProvider;
    @Inject
    Provider<EditProfileController> editProfileControllerProvider;
    @Inject
    Provider<LobbyUserlistController> userlistControllerProvider;
    @Inject
    Provider<RulesScreenController> rulesScreenControllerProvider;
    @Inject
    Provider<NewGameScreenLobbyController> newGameScreenLobbyControllerProvider;
    @Inject
    PrefService prefService;
    @Inject
    LobbyService lobbyService;
    @Inject
    UserService userService;
    @Inject
    NewGameLobbyService newGameLobbyService;
    @Inject
    Provider<CreateNewGamePopUpController> createNewGamePopUpControllerProvider;
    @Inject
    Provider<LobbyGameListController> lobbyGameListControllerProvider;

    private LobbyGameListController lobbyGameListController;
    private Stage appStage;
    public SimpleBooleanProperty isCreatingGame = new SimpleBooleanProperty(false);
    private ChangeListener<Boolean> createGameListener;
    private Stage createNewGameStage;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private boolean darkMode = false;


    @Inject
    public LobbyScreenController(App app
    ) {
        this.app = app;
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

        LobbyUserlistController userlistController = userlistControllerProvider.get();
        if(darkMode){
            userlistController.setDarkMode();
        }
        userlistController.usersVBox = this.UsersVBox;
        userlistController.render();
        userlistController.init();

        lobbyGameListController = lobbyGameListControllerProvider.get();
        if(darkMode){
            lobbyGameListController.getApp().getStage().getScene().getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
        }
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
        app.getStage().setTitle(LOBBY_SCREEN_TITLE);
        if(darkMode){
            app.getStage().getScene().getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
        }
        // set user online after login (entering lobby)
        userService.editProfile(null, null, null, "online")
                .subscribe();
        // add mouse event to buttons
        this.RulesButton.setOnMouseClicked(this::openRules);
        this.EditProfileButton.setOnAction(this::editProfile);
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

    public void editProfile(ActionEvent actionEvent) {
        EditProfileController editController = editProfileControllerProvider.get();
        if(darkMode){
            editController.setDarkMode();
        }
        app.show(editController);
    }

    private void openRules(MouseEvent mouseEvent) {
        RulesScreenController controller = rulesScreenControllerProvider.get();
        if(darkMode){
            controller.setDarkMode();
        }
        controller.init();
    }

    public void logout(ActionEvent event) {
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
        LoginScreenController loginController = loginScreenControllerProvider.get();
        if(app.getStage().getScene().getStylesheets().isEmpty()){
            app.show(loginController);
        } else {
            loginController.setDarkMode();
            app.show(loginController);
        }
    }

    public void showNewGameLobby(Game game, String password, String hexColor) {
        NewGameScreenLobbyController newGameScreenLobbyController = newGameScreenLobbyControllerProvider.get();
        newGameScreenLobbyController.game.set(game);
        newGameScreenLobbyController.password.set(password);
        isCreatingGame.set(false);
        if(app.getStage().getScene().getStylesheets().isEmpty()){
            app.show(newGameScreenLobbyController);
        } else {
            newGameScreenLobbyController.setDarkMode();
            app.show(newGameScreenLobbyController);
        }

        newGameScreenLobbyController.setPlayerColor(hexColor);
    }

    public void newGame() {
        //create pop in order to create a new game lobby
        CreateNewGamePopUpController createNewGamePopUpController = createNewGamePopUpControllerProvider.get();
        Parent node = createNewGamePopUpController.render();
        createNewGameStage = new Stage();
        createNewGameStage.setTitle("create new game pop up");
        Scene scene = new Scene(node);
        if(darkMode){
            scene.getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
        }
        createNewGameStage.setScene(scene);
        createNewGameStage.initOwner(appStage);
        isCreatingGame.set(true);
        createNewGamePopUpController.init();
        createNewGameStage.show();
    }

    public void setDarkMode() {
        darkMode = true;
    }

    public void setBrightMode(){
        darkMode = false;
    }

    public App getApp() {
        return this.app;
    }
}
