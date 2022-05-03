package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.AvatarSpinnerController;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javax.inject.Inject;
import javax.inject.Provider;
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
    private Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private String avatarStr;

    @Inject
    public EditProfileController(UserService userService, App app, Provider<LobbyScreenController> lobbyScreenControllerProvider) {
        this.userService = userService;
        this.app = app;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
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

        // set action event for save button
        this.saveLeaveButton.setOnAction(this::edit);

        // display current username on usernameLabel
        this.usernameLabel.setText(this.userService.getCurrentUser().name());
        // TODO: display current avatar in imageView
        // this.avatarImage.setImage(new Image("data:" + this.userService.getCurrentUser().avatar()));

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
        String newUsername = this.newUsernameInput.getText();
        String newPassword = this.newPasswordInput.getText();
        String repeatPassword = this.repeatNewPasswordInput.getText();

        // set new username null if there is no input
        if (newUsername.isEmpty()) {
            newUsername = null;
        }

        // send patch request to server
        this.userService.editProfile(newUsername, null, null)
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(result -> app.show(lobbyScreenControllerProvider.get()));

    }

    private void updateAvatarString(String newAvatar){
        avatarStr = newAvatar;
    }
}
