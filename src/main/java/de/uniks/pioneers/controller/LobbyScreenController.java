package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.LobbyService;
import de.uniks.pioneers.services.UserService;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.LOBBY_SCREEN_TITLE;

public class LobbyScreenController implements Controller {

    @FXML public ImageView AvatarImageView;
    @FXML public Label UsernameLabel;
    @FXML public ImageView RulesButton;
    @FXML public VBox UsersVBox;
    @FXML public VBox GameVbox;
    @FXML public Button EditProfileButton;
    @FXML public Button LogoutButton;
    @FXML public Button NewGameButton;

    private App app;

    private final Provider<ChatController> chatControllerProvider;
    private final Provider<LoginScreenController> loginScreenControllerProvider;
    private final Provider<EditProfileController> editProfileControllerProvider;
    private final LobbyService lobbyService;
    private final UserService userService;

    public final SimpleStringProperty username = new SimpleStringProperty();
    public final SimpleStringProperty userid = new SimpleStringProperty();

    @Inject
    public LobbyScreenController(App app, LobbyService lobbyService, UserService userService, Provider<ChatController> chatControllerProvider, Provider<LoginScreenController> loginScreenControllerProvider, Provider<EditProfileController> editProfileControllerProvider){
        this.app = app;
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.chatControllerProvider = chatControllerProvider;
        this.loginScreenControllerProvider = loginScreenControllerProvider;
        this.editProfileControllerProvider = editProfileControllerProvider;
    }

    @Override
    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/LobbyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try{
            parent = loader.load();
        }
        catch(IOException e){
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

        this.UsersVBox.getChildren().clear();

        List<User> users = lobbyService.userList();
        for(User user : users){
            if(!user.name().equals(this.username.get())){
                renderUserlist(user);
            }
        }

        return parent;
    }

    @Override
    public void init(){ app.getStage().setTitle(LOBBY_SCREEN_TITLE); }

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
