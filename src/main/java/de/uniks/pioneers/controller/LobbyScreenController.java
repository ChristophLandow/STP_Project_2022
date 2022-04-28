package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;

import static de.uniks.pioneers.Constants.LOBBY_SCREEN_TITLE;

public class LobbyScreenController implements Controller {

    @FXML public ImageView AvatarImageView;
    @FXML public Label UsernameLabel;
    @FXML public ImageView RulesButton;
    @FXML public Button EditProfileButton;
    @FXML public Button LogoutButton;
    @FXML public Button NewGameButton;

    private App app;

    public LobbyScreenController(App app){
        this.app = app;
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

        return parent;
    }

    @Override
    public void init(){
        app.getStage().setTitle(LOBBY_SCREEN_TITLE);
    }

    @Override
    public void stop(){
    }

    public void editProfile(ActionEvent actionEvent) {
    }


    public void logout(ActionEvent actionEvent) {
    }

    public void newGame(ActionEvent actionEvent) {
    }
}
