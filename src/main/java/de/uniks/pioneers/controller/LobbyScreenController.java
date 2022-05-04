package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
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
    public VBox GameVbox;
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

    @Inject
    public LobbyScreenController(App app, EventListener eventListener, LobbyService lobbyService, UserService userService, Provider<ChatController> chatControllerProvider, Provider<LoginScreenController> loginScreenControllerProvider, Provider<EditProfileController> editProfileControllerProvider) {
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

        // get current user from server and display name
        this.userService.getCurrentUser()
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> this.UsernameLabel.setText(user.name()));

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
                c.getList().forEach(this::renderItem);
            }
        });

        return parent;
    }

    private void renderItem(Game game) {
        // fxml erstellen
        String createdAt = game.createdAt();
        int start = createdAt.indexOf("T");
        int end = createdAt.indexOf(".");
        String creationTime = game.createdAt().substring(start + 1, end) + " :";
        Label time = new Label(creationTime);
        Label name = new Label(game.name());
        String memberCount = String.format("            %d/4", game.members());
        Label playerCount = new Label(memberCount);

        HBox gameBox = new HBox();
        gameBox.setSpacing(10);
        gameBox.getChildren().add(time);
        gameBox.getChildren().add(name);
        gameBox.getChildren().add(playerCount);
        GameVbox.getChildren().add(gameBox);
    }

    @Override
    public void init() {
        app.getStage().setTitle(LOBBY_SCREEN_TITLE);
        lobbyService.getGames().observeOn(FX_SCHEDULER)
                .subscribe(this.games::setAll);

        eventListener.listen("games.*.*", Game.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(gameEvent -> {
                    System.out.println(gameEvent.data().toString());
                });

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
    }
}
