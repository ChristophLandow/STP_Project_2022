package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.GameListElementController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.LobbyService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

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
    private final EventListener eventListener;
    private final LobbyService lobbyService;
    private final UserService userService;

    // List with games from Server
    private final ObservableList<Game> games = FXCollections.observableArrayList();

    public final SimpleStringProperty username = new SimpleStringProperty();
    public final SimpleStringProperty userid = new SimpleStringProperty();
    private List<GameListElementController> gameListElementControllers;

    @Inject
    public LobbyScreenController(App app, EventListener eventListener, LobbyService lobbyService, UserService userService,
                                 Provider<ChatController> chatControllerProvider,
                                 Provider<LoginScreenController> loginScreenControllerProvider,
                                 Provider<EditProfileController> editProfileControllerProvider
                                ) {
        this.app = app;
        this.eventListener = eventListener;
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.chatControllerProvider = chatControllerProvider;
        this.loginScreenControllerProvider = loginScreenControllerProvider;
        this.editProfileControllerProvider = editProfileControllerProvider;
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

        this.UsernameLabel.setText(this.userService.getCurrentUser().name());
        this.UsersVBox.getChildren().clear();

        List<User> users = lobbyService.userList();
        for (User user : users) {
            if (!user.name().equals(this.username.get())) {
                renderUserlist(user);
            }
        }

        games.addListener((ListChangeListener<? super Game>) c -> {
            c.next();
            if (c.wasAdded()) {
                //c.getList().forEach(this::renderItem);
                c.getAddedSubList().forEach(this::renderItem);
            }
        });

        return parent;
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

    @Override
    public void init() {
        app.getStage().setTitle(LOBBY_SCREEN_TITLE);
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


    @Override
    public void stop(){
    }

    public void renderUserlist(User user){
        GridPane gridPane = new GridPane();

        Label username = new Label(user.name());
        username.setOnMouseClicked(this::openChat);

        ImageView imgView = new ImageView(new Image(Main.class.getResource("question_mark_icon.jpg").toString()));
        imgView.setFitHeight(40);
        imgView.setFitWidth(40);

        Label userid = new Label(user._id());
        userid.setVisible(false);
        userid.setFont(new Font(0));

        gridPane.addRow(0, username, imgView, userid);

        gridPane.getColumnConstraints().addAll(new ColumnConstraints(140),new ColumnConstraints(45));

        this.UsersVBox.getChildren().add(gridPane);
    }

    public void openChat(MouseEvent event){
        GridPane newChatUserParent = (GridPane) ((Node) event.getSource()).getParent();
        Label chatWithUsername = (Label) newChatUserParent.getChildren().get(0);
        Label chatWithUserid = (Label) newChatUserParent.getChildren().get(2);

        ChatController chatController = chatControllerProvider.get();
        chatController.username.set(this.username.get());
        chatController.userid.set(this.userid.get());
        chatController.newUsername.set(chatWithUsername.getText());
        chatController.newUserid.set(chatWithUserid.getText());

        app.show(chatController);
    }

    public void editProfile(ActionEvent actionEvent) {
        this.app.show(editProfileControllerProvider.get());
    }


    public void logout(ActionEvent actionEvent) {
        lobbyService.logout()
                .observeOn(FX_SCHEDULER);
        app.show(loginScreenControllerProvider.get());
    }

    public void newGame(ActionEvent actionEvent) {
        lobbyService.createGame()
                .observeOn(FX_SCHEDULER)
                .subscribe(game -> {
                    //System.out.println(game.name());
                });
    }
}
