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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    public ListView listViewGames;
    @FXML
    public Button EditProfileButton;
    @FXML
    public Button LogoutButton;
    @FXML
    public Button NewGameButton;

    private final App app;

    private final Provider<LoginScreenController> loginScreenControllerProvider;
    private final Provider<EditProfileController> editProfileControllerProvider;
    private final Provider<LobbyUserlistController> userlistControlerProvider;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;
    private final Provider<NewGameScreenLobbyController> newGameScreenLobbyControllerProvider;

    private final PrefService prefService;
    private final LobbyService lobbyService;
    private final UserService userService;
    private final NewGameLobbyService newGameLobbyService;
    private final Provider<CreateNewGamePopUpController> createNewGamePopUpControllerProvider;
    private final Provider<LobbyGameListController> lobbyGameListControllerProvider;
    private final MessageService messageService;

    @Inject
    public LobbyScreenController(App app, LobbyService lobbyService, UserService userService,
                                 Provider<LoginScreenController> loginScreenControllerProvider,
                                 Provider<EditProfileController> editProfileControllerProvider,
                                 Provider<LobbyUserlistController> userlistControlerProvider,
                                 Provider<RulesScreenController> rulesScreenControllerProvider,
                                 Provider<CreateNewGamePopUpController> createNewGamePopUpControllerProvider,
                                 Provider<NewGameScreenLobbyController> newGameScreenLobbyControllerProvider,
                                 Provider<LobbyGameListController> lobbyGameListControllerProvider,
                                 MessageService messageService,
                                 NewGameLobbyService newGameLobbyService,
                                 PrefService prefService
    ) {
        this.app = app;
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.createNewGamePopUpControllerProvider = createNewGamePopUpControllerProvider;
        this.lobbyGameListControllerProvider = lobbyGameListControllerProvider;
        this.messageService = messageService;
        this.loginScreenControllerProvider = loginScreenControllerProvider;
        this.newGameScreenLobbyControllerProvider = newGameScreenLobbyControllerProvider;
        this.editProfileControllerProvider = editProfileControllerProvider;
        this.userlistControlerProvider = userlistControlerProvider;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
        this.newGameLobbyService = newGameLobbyService;
        this.prefService = prefService;
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

        LobbyUserlistController userlistController = userlistControlerProvider.get();
        userlistController.usersVBox = this.UsersVBox;
        userlistController.render();
        userlistController.init();

        LobbyGameListController lobbyGamesListController = lobbyGameListControllerProvider.get();
        lobbyGamesListController.listViewGames = this.listViewGames;
        lobbyGamesListController.init();

        return parent;
    }

    @Override
    public void init() {
        this.app.getStage().setOnCloseRequest(event -> {
            logout();
            Platform.exit();
            System.exit(0);
        });

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
    }

    public void editProfile(ActionEvent actionEvent) {
        this.app.show(editProfileControllerProvider.get());
    }

    private void openRules(MouseEvent mouseEvent) {
        RulesScreenController controller = rulesScreenControllerProvider.get();
        controller.init();
    }

    public void logout(ActionEvent event) {
        this.messageService.getchatUserList().clear();
        prefService.forget();
        logout();
    }

    public void logout() {
        lobbyService.logout()
                .observeOn(FX_SCHEDULER);
        // set status offline after logout (leaving lobby)
        userService.editProfile(null, null, null, "offline")
                .subscribe();
        app.show(loginScreenControllerProvider.get());
    }

    public void showNewGameLobby(Game game, String password) {
        NewGameScreenLobbyController newGameScreenLobbyController = newGameScreenLobbyControllerProvider.get();
        newGameScreenLobbyController.postNewMember(game, userService.getCurrentUser(), password);
        newGameLobbyService.setCurrentMemberId(userService.getCurrentUser()._id());
    }

    public void newGame(ActionEvent actionEvent) {
        //create pop in order to create a new game lobby
        CreateNewGamePopUpController createNewGamePopUpController = createNewGamePopUpControllerProvider.get();
        Parent node = createNewGamePopUpController.render();
        Stage stage = new Stage();
        stage.setTitle("create new game pop up");
        Scene scene = new Scene(node);
        stage.setScene(scene);
        stage.show();
    }
}
