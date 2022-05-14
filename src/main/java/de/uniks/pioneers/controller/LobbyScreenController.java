package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.CreateNewGamePopUpController;
import de.uniks.pioneers.controller.subcontroller.GameListElementController;
import de.uniks.pioneers.controller.subcontroller.LobbyUserlistControler;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.LobbyService;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
    private final Provider<NewGameScreenLobbyController> newGameScreenLobbyControllerProvider;

    private final PrefService prefService;
    private final EventListener eventListener;
    private final LobbyService lobbyService;
    private final UserService userService;
    private final MessageService messageService;

    // List with games from Server
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<Game> games = FXCollections.observableArrayList();

    private List<GameListElementController> gameListElementControllers;



    @Inject
    public LobbyScreenController(App app, EventListener eventListener, LobbyService lobbyService, UserService userService,
                                 Provider<ChatController> chatControllerProvider,
                                 Provider<LoginScreenController> loginScreenControllerProvider,
                                 Provider<EditProfileController> editProfileControllerProvider,
                                 Provider<LobbyUserlistControler> userlistControlerProvider,
                                 Provider<RulesScreenController> rulesScreenControllerProvider,
                                 Provider<NewGameScreenLobbyController> newGameScreenLobbyControllerProvider,
                                 MessageService messageService,
                                 PrefService prefService
    ) {
        this.app = app;
        this.eventListener = eventListener;
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.messageService = messageService;
        this.chatControllerProvider = chatControllerProvider;
        this.loginScreenControllerProvider = loginScreenControllerProvider;
        this.newGameScreenLobbyControllerProvider = newGameScreenLobbyControllerProvider;
        this.editProfileControllerProvider = editProfileControllerProvider;
        this.userlistControlerProvider = userlistControlerProvider;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
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
        this.EditProfileButton.setOnAction(this::editProfile);

        // get current user from server and display name and avatar
        this.userService.getCurrentUser()
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    removeUser(user);
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
                c.getAddedSubList().stream().filter(game -> isGameValid(game))
                                            .forEach(game -> renderGame(game));
            }else if (c.wasRemoved()){
                c.getRemoved().forEach(this::deleteGame);
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

        userService.findAll().observeOn(FX_SCHEDULER)
                .subscribe(this.users::setAll);


        eventListener.listen("users.*.*", User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(userEvent -> {
                    final User user = userEvent.data();
                    if (userEvent.event().endsWith(".created") && user.status().equals("online")){
                        users.add(user);
                    }
                    else if (userEvent.event().endsWith(".deleted")){
                        users.removeIf(u->u._id().equals(user._id()));
                    }
                    else if(userEvent.event().endsWith(".updated")){
                        if(user.status().equals("online")){
                            users.removeIf(u->u._id().equals(user._id()));
                            users.add(user);
                        }
                        else{
                            users.removeIf(u->u._id().equals(user._id()));
                        }
                    }
                });

        eventListener.listen("games.*.*", Game.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(gameEvent -> {
                    // i gona change this code, when there is nothing else to do, add a map with regex
                    if (gameEvent.event().endsWith(".created")) {
                        games.add(gameEvent.data());
                    } else if (gameEvent.event().endsWith(".deleted")) {
                        games.remove(gameEvent.data());
                    } else {
                        updateGame(gameEvent.data());
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

    private void renderGame(Game game) {
        //not final
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/GameListElement.fxml"));
        gameListElementControllers = new ArrayList<>();
        final Node node;
        try {
            node = loader.load();
            GameListElementController gameListElementController = loader.getController();
            gameListElementController.createOrUpdateGame(game, games, users, this);
            node.setId(game._id());
            gameListElementControllers.add(gameListElementController);
            ListViewGames.getItems().add(0, node);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean isGameValid(Game game) {
        // a game is valid as long his creator is online, we can update this if needed
        //return users.stream().anyMatch(user -> user._id().equals(game.owner()));
        return true;
    }

    private void deleteGame(Game data) {
        List<Node> removales = (List<Node>) ListViewGames.getItems().stream().toList();
        removales = removales.stream().filter(game -> game.getId().equals(data._id())).toList();
        ListViewGames.getItems().removeAll(removales);
    }

    private void updateGame(Game data) {
        //rerender
        try {
            GameListElementController gameListElementController = gameListElementControllers.stream()
                    .filter(conroller -> conroller.getGame()._id().equals(data._id())).findAny().get();
            gameListElementController.createOrUpdateGame(data, games, users,this);
        } catch (Exception e) {
            return;
        }
    }

    public User returnUserById(String id){
        return users.stream().filter(user -> user._id().equals(id)).findAny().get();
    }

    public void renderUser(User user){
        GridPane gridPane = new GridPane();

        Label username = new Label(user.name());
        username.setOnMouseClicked(this::openChat);

        ImageView imgView;
        try {
            imgView = new ImageView(new Image(user.avatar()));
        } catch (NullPointerException e) {
            imgView = new ImageView(new Image(App.class.getResource("user-avatar.svg").toString()));
        }

        imgView.setOnMouseClicked(this::openChat);
        imgView.setFitHeight(40);
        imgView.setFitWidth(40);

        Label userid = new Label(user._id());
        userid.setVisible(false);
        userid.setFont(new Font(0));

        gridPane.addRow(0, username, imgView, userid);

        gridPane.getColumnConstraints().addAll(new ColumnConstraints(200), new ColumnConstraints(45));

        this.UsersVBox.getChildren().add(gridPane);
    }

    public void removeUser(User user){
        UsersVBox.getChildren().removeIf(n -> {
            GridPane gpane = (GridPane) n;
            return ((Label) gpane.getChildren().get(2)).getText().equals(user._id());
        });
    }

    public void updateUser(User user){
        for(Node n: UsersVBox.getChildren()){
            GridPane gpane = (GridPane) n;
            Label chatWithUserid = ((Label) gpane.getChildren().get(2));

            if(chatWithUserid.getText().equals(user._id())){
                ((Label) gpane.getChildren().get(0)).setText(user.name());

                try {
                    ((ImageView) gpane.getChildren().get(1)).setImage(new Image(user.avatar()));
                }catch(NullPointerException e){
                    ((ImageView) gpane.getChildren().get(1)).setImage(new Image(App.class.getResource("user-avatar.svg").toString()));
                }
            }
        }
    }

    public void openChat(MouseEvent event){
        GridPane newChatUserParent = (GridPane) ((Node) event.getSource()).getParent();
        Label chatWithUsername = (Label) newChatUserParent.getChildren().get(0);
        ImageView chatWithAvatar = (ImageView) newChatUserParent.getChildren().get(1);
        Label chatWithUserid = (Label) newChatUserParent.getChildren().get(2);

        this.messageService.getchatUserList().removeIf(u->u.name().equals(chatWithUsername.getText()));
        this.messageService.addUserToChatUserList(
                new User(chatWithUserid.getText(), chatWithUsername.getText(),"", chatWithAvatar.getImage().getUrl()));
        app.show(chatControllerProvider.get());
    }

    public void editProfile(ActionEvent actionEvent) {
        this.app.show(editProfileControllerProvider.get());
    }


    public void logout(ActionEvent ignoredActionEvent) {
        this.messageService.getchatUserList().clear();
        prefService.forget();
        logout();
        app.show(loginScreenControllerProvider.get());
    }

    public void logout(){
        lobbyService.logout()
                .observeOn(FX_SCHEDULER);
        // set status offline after logout (leaving lobby)
        userService.editProfile(null, null, null, "offline")
                .subscribe();
        app.show(loginScreenControllerProvider.get());
    }

    public void showNewGameLobby (Game game){
        NewGameScreenLobbyController newGameScreenLobbyController = newGameScreenLobbyControllerProvider.get();
        newGameScreenLobbyController.game.set(game);
        app.show(newGameScreenLobbyController);
    }

    public void newGame(ActionEvent actionEvent) {
        //create pop in order to create a new game lobby
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/CreateNewGamePopUp.fxml"));
        Parent node = null;
        try {
            node = loader.load();
            CreateNewGamePopUpController createNewGamePopUpController = loader.getController();
            createNewGamePopUpController.init(this,lobbyService);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("create new game pop up");
        Scene scene = new Scene(node);
        stage.setScene(scene);
        stage.show();
    }
}
