package de.uniks.pioneers.controller;
import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.GameListElementController;
import de.uniks.pioneers.controller.subcontroller.LobbyUserlistControler;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.services.LobbyService;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.LOBBY_SCREEN_TITLE;

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
    public ListView ListViewGames;
    @FXML
    public Button EditProfileButton;
    @FXML
    public Button LogoutButton;
    @FXML
    public Button NewGameButton;

    private App app;

    private final Provider<ChatController> chatControllerProvider;
    private final Provider<LoginScreenController> loginScreenControllerProvider;
    private final Provider<EditProfileController> editProfileControllerProvider;
    private  final Provider<LobbyUserlistControler> userlistControlerProvider;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;
    private final EventListener eventListener;
    private final LobbyService lobbyService;
    private final UserService userService;
    private final MessageService messageService;
    private final ObservableList<Game> games = FXCollections.observableArrayList();
    private List<GameListElementController> gameListElementControllers;

    @Inject
    public LobbyScreenController(App app, EventListener eventListener, LobbyService lobbyService, UserService userService,
                                 Provider<ChatController> chatControllerProvider,
                                 Provider<LoginScreenController> loginScreenControllerProvider,
                                 Provider<EditProfileController> editProfileControllerProvider,
                                 Provider<LobbyUserlistControler> userlistControlerProvider,
                                 Provider<RulesScreenController> rulesScreenControllerProvider,
                                 MessageService messageService
    ) {
        this.app = app;
        this.eventListener = eventListener;
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.messageService = messageService;
        this.chatControllerProvider = chatControllerProvider;
        this.loginScreenControllerProvider = loginScreenControllerProvider;
        this.editProfileControllerProvider = editProfileControllerProvider;
        this.userlistControlerProvider = userlistControlerProvider;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
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
        this.EditProfileButton.setOnAction(this::editProfile);

        // get current user from server and display name and avatar
        this.userService.getCurrentUser()
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    this.UsernameLabel.setText(user.name());
                    if (user.avatar() != null) {
                        this.AvatarImageView.setImage(new Image(user.avatar()));
                    } else {
                        this.AvatarImageView.setImage(null);
                    }
                });

        this.app.getStage().setOnCloseRequest(event -> {
            logout();
            Platform.exit();
            System.exit(0);
        });

        LobbyUserlistControler userlistController = userlistControlerProvider.get();
        userlistController.usersVBox = this.UsersVBox;
        userlistController.render();
        userlistController.init();

        games.addListener((ListChangeListener<? super Game>) c -> {
            c.next();
            if (c.wasAdded()) {
                //c.getList().forEach(this::renderItem);
                c.getAddedSubList().forEach(this::renderItem);
            }
        });

        return parent;
    }

    @Override
    public void init() {
        app.getStage().setTitle(LOBBY_SCREEN_TITLE);

        // set user online after login (entering lobby)
        userService.editProfile(null, null, null, "online")
                .subscribe();

        // add mouse event to rules button
        this.RulesButton.setOnMouseClicked(this::openRules);

        lobbyService.getGames().observeOn(FX_SCHEDULER)
                .subscribe(this.games::setAll);

        eventListener.listen("games.*.*", Game.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(gameEvent -> {
                    // i gona change this code, when there is nothing else to do, add a map with regex
                    if (gameEvent.event().endsWith(".created")){
                        games.add(gameEvent.data());
                    }else if (gameEvent.event().endsWith(".updated")){
                        updateGame(gameEvent.data());
                    }else {
                        deleteGame(gameEvent.data());
                    }
                });
    }

    private void openRules(MouseEvent mouseEvent) {
        RulesScreenController controller = rulesScreenControllerProvider.get();
        controller.init();
    }

    @Override
    public void stop(){
    }

    private void renderItem(Game game) {
        // this code is not final, when there is time i gona use dagger, when i know how to hand over objects,
        // when creating an controller, for now i could just inject the whole game list and would not know which game
        // belongs to this controller
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/GameListElement.fxml"));
        gameListElementControllers = new ArrayList<>();
        final Node node;
        try {
            node = loader.load();
            GameListElementController gameListElementController = loader.getController();
            gameListElementController.getOrCreateGame(game);
            node.setId(game._id());
            gameListElementControllers.add(gameListElementController);
            ListViewGames.getItems().add(0,node);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void deleteGame(Game data) {
        Game toRemove =  this.games.stream().filter(game -> data._id().equals(game._id())).findAny().get();
        List<Node> removales = (List<Node>) ListViewGames.getItems().stream().toList();
        removales= removales.stream().filter(game -> game.getId().equals(data._id())).toList();
        ListViewGames.getItems().removeAll(removales);
    }

    private void updateGame(Game data) {
        Game toUpdate  = games.stream().filter(game -> game._id().equals(data._id())).findAny().get();
        toUpdate=data;
        //rerender
        GameListElementController gameListElementController = gameListElementControllers.stream().
                filter(conroller -> conroller.getGame()._id().equals(data._id())).findAny().get();
        gameListElementController.getOrCreateGame(data);
    }

    public void editProfile(ActionEvent actionEvent) {
        this.app.show(editProfileControllerProvider.get());
    }


    public void logout(ActionEvent actionEvent) {
        this.messageService.getchatUserList().clear();
        logout();
        app.show(loginScreenControllerProvider.get());
    }

    public void logout(){
        lobbyService.logout()
                .observeOn(FX_SCHEDULER);
        // set status offline after logout (leaving lobby)
        userService.editProfile(null, null, null, "offline")
                .subscribe();
    }

    public void newGame(ActionEvent actionEvent) {
        lobbyService.createGame()
                .observeOn(FX_SCHEDULER)
                .subscribe(game -> {
                    //System.out.println(game.name());
                });
    }
}
