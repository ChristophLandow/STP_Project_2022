package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.EditAvatarSpinnerController;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.LoginService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.concurrent.Executor;

import static de.uniks.pioneers.Constants.EDIT_PROFILE_SCREEN_TITLE;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;

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
    @FXML public Text usernameStatusText;
    @FXML public Text newPasswordStatusText;
    @FXML public Text oldPasswordStatusText;

    private App app;
    private UserService userService;
    private LoginService loginService;
    private Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private String avatarStr;

    private final ObservableList<User> users = FXCollections.observableArrayList();

    @Inject
    public EditProfileController(UserService userService, LoginService loginService, App app, Provider<LobbyScreenController> lobbyScreenControllerProvider) {
        this.userService = userService;
        this.loginService = loginService;
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

            final BooleanBinding oldPasswordEmpty = Bindings.equal(this.oldPasswordInput.textProperty(), "");
            final IntegerBinding passwordLength = Bindings.length(this.newPasswordInput.textProperty());
            final BooleanBinding passwordMatch = Bindings.equal(this.newPasswordInput.textProperty(), this.repeatNewPasswordInput.textProperty());

            this.newPasswordStatusText.textProperty().bind(Bindings
                    .when(passwordLength.greaterThan(7)).then(Bindings.when(passwordMatch).then("")
                            .otherwise("Passwords do not match")).otherwise("Password must be at least 8 characters long"));

            this.saveLeaveButton.disableProperty().bind(oldPasswordEmpty.or(passwordMatch.not()).or(passwordLength.greaterThan(7).not()));

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

        // get currentUser from Server and display name
        this.userService.getCurrentUser()
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> this.usernameLabel.setText(user.name()));

        // Spinner Code
        EditAvatarSpinnerController spinnerValueFactory = new EditAvatarSpinnerController(this::updateAvatarString);
        spinnerValueFactory.setUserService(this.userService);
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
        String newAvatar = null;

        if (this.avatarImage.getImage() != null) {
            newAvatar = this.avatarImage.getImage().getUrl();
        }

        // set new username null if there is no input
        if (newUsername.isEmpty()) {
            newUsername = null;
        }

        // send patch request to server
        this.userService.editProfile(newUsername, newAvatar, null)
                .observeOn(FX_SCHEDULER)
                .doOnError(e -> this.usernameStatusText.setText("Username already taken. Choose another one!"))
                .subscribe(result -> app.show(lobbyScreenControllerProvider.get()));

    }

    private void updateAvatarString(String newAvatar){
        avatarStr = newAvatar;
    }
}
