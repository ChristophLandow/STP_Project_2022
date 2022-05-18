package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.EditAvatarSpinnerController;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.services.LoginService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;

import static de.uniks.pioneers.Constants.*;

public class EditProfileController implements Controller {
    @FXML public TextField newUsernameInput;
    @FXML public Button saveLeaveButton;
    @FXML public ImageView avatarImage;
    @FXML public Button uploadAvatarButton;
    @FXML public PasswordField oldPasswordInput;
    @FXML public PasswordField newPasswordInput;
    @FXML public PasswordField repeatNewPasswordInput;
    @FXML public Text usernameLabel;
    @FXML public Spinner<Integer> chooseAvatarSpinner;
    @FXML public Text usernameStatusText;
    @FXML public Text newPasswordStatusText;
    @FXML public Text oldPasswordStatusText;
    @FXML public Text avatarStatusText;

    private final App app;
    private final UserService userService;
    private final LoginService loginService;
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private String avatarStr;
    private String customAvatar = "";

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
            final BooleanBinding avatarSizeOK = Bindings.equal(this.avatarStatusText.textProperty(), "");

            this.newPasswordStatusText.textProperty().bind(Bindings
                    .when(passwordLength.greaterThan(7)).then(Bindings.when(passwordMatch).then("")
                            .otherwise("Passwords do not match")).otherwise("Password must be at least 8 characters long"));

            this.saveLeaveButton.disableProperty().bind(
                    oldPasswordEmpty.not().and(
                            passwordMatch.not().or(
                                    passwordLength.greaterThan(7).not()
                            )
                    ).or(
                            avatarSizeOK.not()
                    )
            );

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return view;
    }

    @Override
    public void init() {
        app.getStage().setTitle(EDIT_PROFILE_SCREEN_TITLE);

        // get currentUser from Server and display name
        this.usernameLabel.setText(userService.getCurrentUser().name());

        // Spinner Code
        EditAvatarSpinnerController spinnerValueFactory = new EditAvatarSpinnerController(this::updateAvatarString);
        spinnerValueFactory.setUserService(this.userService);
        spinnerValueFactory.init(avatarImage);
        chooseAvatarSpinner.setValueFactory(spinnerValueFactory);
    }

    @Override
    public void stop() {
    }

    public void edit(ActionEvent event) throws URISyntaxException, IOException {
        // set defaults
        String newUsername = null;
        String newAvatar = null;

        if (avatarImage.getImage() != null) {
            newAvatar = avatarImage.getImage().getUrl();
        }

        boolean changePassword = false;
        final boolean[] oldPasswordCorrect = {true}; // array to access in onComplete-lambda
        String newPassword = this.newPasswordInput.getText();

        // set new avatar if spinner value changed
        if (chooseAvatarSpinner.getValue() > 0) {
            getClass().getResource("subcontroller/" + avatarStr);
            byte[] data = Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("subcontroller/" + avatarStr)).toURI()));
            newAvatar = "data:image/png;base64," + Base64.getEncoder().encodeToString(data);
        }
        // or if user uploaded custom avatar
        if (!customAvatar.equals("")) {
            newAvatar = customAvatar;
        }

        // set new username if input not empty
        if (!newUsernameInput.getText().isEmpty()) {
            newUsername = newUsernameInput.getText();
        }

        // check if there is input for new password
        if (!newPassword.isEmpty()) {
            changePassword = true;

            // check old password via API call
            LoginResult result = loginService.checkPassword(usernameLabel.getText(), oldPasswordInput.getText());
            if (result == null) {
                oldPasswordCorrect[0] = false;
            }
        }

        if (changePassword && oldPasswordCorrect[0]) {
            // send patch request with changing password
            disposable.add(this.userService.editProfile(newUsername, newAvatar, newPasswordInput.getText(), null)
                    .observeOn(FX_SCHEDULER)
                    .doOnError(e -> {
                        this.usernameStatusText.setText("Username already taken. Choose another one!");
                        e.printStackTrace();
                    })
                    .subscribe(result -> app.show(lobbyScreenControllerProvider.get())));

        } else if (changePassword) {
            // dont send patch request
            oldPasswordStatusText.setText("Incorrect password");
        } else {
            // send patch request without new password
            disposable.add(this.userService.editProfile(newUsername, newAvatar, null, null)
                    .observeOn(FX_SCHEDULER)
                    .doOnError(e -> {
                        this.usernameStatusText.setText("Username already taken. Choose another one!");
                        e.printStackTrace();
                    })
                    .subscribe(result -> {
                        app.show(lobbyScreenControllerProvider.get());
                    }));
        }

    }

    private void resetAvatar() {
        this.avatarStatusText.setText("");
        this.customAvatar = "";
    }

    public void uploadAvatar() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Choose Avatar", "*.PNG", "*.jpg"));
        File avatarURL = fileChooser.showOpenDialog(null);
        if(avatarURL != null) {
            resetAvatar();
            byte[] data = Files.readAllBytes(Paths.get(avatarURL.toURI()));
            String avatarB64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(data);

            Image image = new Image("file:" + avatarURL.getAbsolutePath());
            this.avatarImage.setImage(image);

            if (avatarB64.length() > AVATAR_CHAR_LIMIT) {
                this.avatarStatusText.setText("Image exceeds file size limit");
            } else {
                this.customAvatar = avatarB64;
            }
        }
    }

    private void updateAvatarString(String newAvatar){
        avatarStr = newAvatar;
        resetAvatar();
    }
}
