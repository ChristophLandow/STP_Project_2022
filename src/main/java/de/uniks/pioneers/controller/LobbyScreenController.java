package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.LobbyService;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
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

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.List;

import static de.uniks.pioneers.Constants.LOBBY_SCREEN_TITLE;

public class LobbyScreenController implements Controller {

    @FXML public ImageView AvatarImageView;
    @FXML public Label UsernameLabel;
    @FXML public ImageView RulesButton;
    @FXML public VBox UsersVBox;
    @FXML public VBox GameVbox;
    @FXML public Button EditProfileButton;
    @FXML public Button LogoutButton;
    @FXML
    public Button NewGameButton;

    private App app;

    private final Provider<ChatController> chatControllerProvider;

    private final LobbyService lobbyService;

    public final SimpleStringProperty username = new SimpleStringProperty();
    public final SimpleStringProperty userid = new SimpleStringProperty();

    @Inject
    public LobbyScreenController(App app, LobbyService lobbyService, Provider<ChatController> chatControllerProvider){
        this.app = app;
        this.lobbyService = lobbyService;
        this.chatControllerProvider = chatControllerProvider;
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
    public void init(){
        app.getStage().setTitle(LOBBY_SCREEN_TITLE);
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

        gridPane.addRow(0, username, imgView);

        gridPane.getColumnConstraints().addAll(new ColumnConstraints(140),new ColumnConstraints(45));

        this.UsersVBox.getChildren().add(gridPane);
    }

    public void openChat(MouseEvent event){
        Label chatWithUsername = (Label) event.getSource();

        ChatController chatController = chatControllerProvider.get();
        chatController.username.set(this.username.get());
        chatController.userid.set(this.userid.get());
        chatController.newchatusername.set(chatWithUsername.getText());
        app.show(chatController);
    }

    public void editProfile(ActionEvent actionEvent) {
    }


    public void logout(ActionEvent actionEvent) {
    }

    public void newGame(ActionEvent actionEvent) {
    }
}
