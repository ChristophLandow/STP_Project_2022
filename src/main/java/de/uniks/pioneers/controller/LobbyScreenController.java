package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.CreateNewGamePopUpController;
import de.uniks.pioneers.controller.subcontroller.LobbyGameListController;
import de.uniks.pioneers.controller.subcontroller.LobbyUserlistController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
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
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.xml.stream.EventFilter;
import javax.xml.stream.events.XMLEvent;
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
    public SimpleBooleanProperty isCreatingGame = new SimpleBooleanProperty(false);

    @Inject
    MessageService messageService;
    private Stage appStage;

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
        userlistController.usersVBox = this.UsersVBox;
        userlistController.render();
        userlistController.init();

        lobbyGameListController = lobbyGameListControllerProvider.get();
        lobbyGameListController.listViewGames = this.listViewGames;
        lobbyGameListController.init();

        return parent;
    }

    @Override
    public void init() {
        appStage = this.app.getStage();
        appStage.setOnCloseRequest(event -> {
            logout();
            Platform.exit();
            System.exit(0);
        });

        // when create new game pop up is openend, create new game button gets disabled
        NewGameButton.disableProperty().bind(isCreatingGame);


        EventHandler<InputEvent> mouseEventFilter = new EventHandler<>() {
            @Override
            public void handle(InputEvent event) {
                event.consume();
            }
        };

        ChangeListener<BooleanProperty> changeListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends BooleanProperty> observable, BooleanProperty oldValue, BooleanProperty newValue) {
                if (!oldValue.get() && newValue.get()){
                    NewGameButton.disableProperty().set(true);
                }else if (oldValue.get() && !newValue.get()){
                    NewGameButton.disableProperty().set(false);
                }

            }
        };


        // set title to stage
        app.getStage().setTitle(LOBBY_SCREEN_TITLE);
        // set user online after login (entering lobby)
        userService.editProfile(null, null, null, "online")
                .subscribe();
        // add mouse event to buttons
        this.RulesButton.setOnMouseClicked(this::openRules);
        this.EditProfileButton.setOnAction(this::editProfile);
    }

    @Override
    public void stop() {
        lobbyGameListController.stop();
    }

    public void editProfile(ActionEvent actionEvent) {
        this.app.show(editProfileControllerProvider.get());
    }

    private void openRules(MouseEvent mouseEvent) {
        RulesScreenController controller = rulesScreenControllerProvider.get();
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
        app.show(loginScreenControllerProvider.get());
    }

    public void showNewGameLobby(Game game, String password, String hexColor) {
        NewGameScreenLobbyController newGameScreenLobbyController = newGameScreenLobbyControllerProvider.get();
        newGameScreenLobbyController.game.set(game);
        newGameScreenLobbyController.password.set(password);
        app.show(newGameScreenLobbyController);
        newGameScreenLobbyController.setPlayerColor(hexColor);
    }

    public void newGame() {
        //create pop in order to create a new game lobby
        CreateNewGamePopUpController createNewGamePopUpController = createNewGamePopUpControllerProvider.get();
        Parent node = createNewGamePopUpController.render();
        Stage stage = new Stage();
        stage.setTitle("create new game pop up");
        Scene scene = new Scene(node);
        stage.setScene(scene);
        stage.initOwner(appStage);
        isCreatingGame.set(true);
        createNewGamePopUpController.init();
        stage.show();
    }
}
