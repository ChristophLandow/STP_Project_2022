package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.AvatarSpinnerController;
import de.uniks.pioneers.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.io.IOException;

import static de.uniks.pioneers.Constants.EDIT_PROFILE_SCREEN_TITLE;

public class EditProfileController implements Controller {
    @FXML public TextField newUsernameInput;
    @FXML public Button saveLeaveButton;
    @FXML public ImageView avatarImage;
    @FXML public Button uploadAvatarButton;
    @FXML public PasswordField oldPasswordInput;
    @FXML public PasswordField newPasswordInput;
    @FXML public PasswordField repeatNewPasswordInput;
    @FXML public Text usernameLabel;
    @FXML public Spinner chooseAvatarSpinner;

    private App app;
    private UserService userService;
    private String avatarStr;

    @Inject
    public EditProfileController(UserService userService, App app) {
        this.userService = userService;
        this.app = app;
    }

    @Override
    public Parent render() {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("views/EditProfileScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent view;
        try {
            view = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return view;
    }

    @Override
    public void init() {
        app.getStage().setTitle(EDIT_PROFILE_SCREEN_TITLE);

        // Spinner Code
        AvatarSpinnerController spinnerValueFactory = new AvatarSpinnerController(this::updateAvatarString);
        spinnerValueFactory.init(avatarImage);
        chooseAvatarSpinner.setValueFactory(spinnerValueFactory);

        // add action event
        saveLeaveButton.setOnAction(this::edit);

    }

    @Override
    public void stop() {
    }

    public void edit(ActionEvent event) {
    }

    private void updateAvatarString(String newAvatar){
        avatarStr = newAvatar;
    }
}
