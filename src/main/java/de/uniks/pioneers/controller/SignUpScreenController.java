package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.AvatarSpinnerController;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.uniks.pioneers.Constants.*;

public class SignUpScreenController implements Controller{

    public final SimpleStringProperty userName = new SimpleStringProperty();

    public final SimpleStringProperty password = new SimpleStringProperty();

    @FXML
    public TextField textFieldUserName;
    @FXML
    public TextField passwordField;
    @FXML
    public TextField passwordFieldConfirmation;
    @FXML
    public Spinner<Integer> avatarSelector;
    @FXML
    public Button buttonUploadAvatar;
    @FXML
    public ImageView imageViewAvatar;
    @FXML
    public Button buttonRegister;
    @FXML
    public Button leaveButton;
    @FXML
    public Text userNameStatusText;
    @FXML
    public Text passwordStatusText;
    @FXML
    public Text avatarStatusText;

    private final App app;
    private final Provider<LoginScreenController> loginScreenControllerProvider;
    private final UserService userService;

    private String avatar;
    private String customAvatar = "";

    @Inject
    public SignUpScreenController(UserService userService, Provider<LoginScreenController> loginScreenControllerProvider,App app) {
        this.userService = userService;
        this.loginScreenControllerProvider = loginScreenControllerProvider;
        this.app = app;
    }

    @Override
    public void init() {

        Stage stage = app.getStage();
        stage.setTitle(SIGNUP_SCREEN_TITLE);

        // Spinner Code
        AvatarSpinnerController spinnerValueFactory = new AvatarSpinnerController(this::updateAvatarString);
        spinnerValueFactory.init(imageViewAvatar);
        avatarSelector.setValueFactory(spinnerValueFactory);
    }

    @Override
    public Parent render() {

        Parent parent;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/SignUpScreen.fxml"));
        loader.setControllerFactory(c -> this);
        try {

            parent = loader.load();
            textFieldUserName.textProperty().bindBidirectional(userName);
            passwordField.textProperty().bindBidirectional(password);


            //Bindings displaying status information on password validity. Password length takes precedent over match with confirmation field.
            final IntegerBinding userNameLength = Bindings.length(textFieldUserName.textProperty());

            final IntegerBinding passwordLength = Bindings.length(passwordField.textProperty());
            final BooleanBinding avatarInvalid = Bindings.length(this.avatarStatusText.textProperty()).greaterThan(0);
            final BooleanBinding passwordMatch = Bindings.equal(passwordField.textProperty(), passwordFieldConfirmation.textProperty());
            passwordStatusText.textProperty().bind(Bindings
                    .when(passwordLength.greaterThan(7)).then(Bindings.when(passwordMatch).then("")
                            .otherwise("Passwords do not match")).otherwise("Password must be at least 8 characters long"));

            buttonRegister.disableProperty().bind(
                    passwordMatch.not()
                            .or(passwordLength.greaterThan(7).not()
                                    .or(userNameLength.greaterThan(0).not()
                                            .or(avatarInvalid))));

            this.textFieldUserName.setOnMouseClicked(this::resetStatus);

            return parent;

        } catch (Exception e) {
            System.err.println("Error loading Register Screen.");
            return null;
        }
    }
    @Override
    public void stop() {
    }

    private void updateAvatarString(String newAvatar){

        avatar = newAvatar;
        resetAvatar();
    }
    public void uploadAvatar() throws IOException {

        resetAvatar();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Choose Avatar", "*.PNG", "*.jpg"));
        File avatarURL = fileChooser.showOpenDialog(null);
        if(avatarURL != null) {
            byte[] data = Files.readAllBytes(Paths.get(avatarURL.toURI()));
            String avatarB64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(data);

            Image image = new Image("file:" + avatarURL.getAbsolutePath());
            this.imageViewAvatar.setImage(image);

            if (avatarB64.length() > AVATAR_CHAR_LIMIT) {
                this.avatarStatusText.setText("Image exceeds file size limit");
            } else {
                this.customAvatar = avatarB64;
            }
        }
    }

    private void resetStatus(MouseEvent mouseEvent) {

        this.userNameStatusText.setText("");
    }
    private void resetAvatar() {

        this.avatarStatusText.setText("");
        this.customAvatar = "";
    }

    public void register() throws IOException, URISyntaxException {

        String avatarB64 = customAvatar;
        //load default avatar if no custom one was selected
        if(customAvatar.equals("")){
            if(getClass().getResource("subcontroller/" + avatar).toString().contains("!"))
            {
                final Map<String, String> env = new HashMap<>();
                String[] array = getClass().getResource("subcontroller/" + avatar).toString().split("!");
                FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env);
                byte[] data = Files.readAllBytes(Objects.requireNonNull(fs.getPath(array[1])));
                avatarB64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(data);
                fs.close();
            }
            else
            {
                byte[] data = Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("subcontroller/" + avatar)).toURI()));
                avatarB64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(data);
            }
        }

        this.userService.register(this.textFieldUserName.getText(), avatarB64, this.passwordField.getText())
                .observeOn(FX_SCHEDULER)
                .doOnError(e -> this.userNameStatusText.setText("Username already taken"))
                .doOnComplete(this::registrationComplete)
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}
                    @Override
                    public void onNext(@NonNull User user) {}
                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println(e.getMessage());
                    }
                    @Override
                    public void onComplete() {}
                });
    }
    private void registrationComplete(){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Successful!");
        alert.setHeaderText(null);
        alert.setContentText("You can now log into your new account.");

        alert.showAndWait();
        toLogin();
    }
    public void toLogin() {

        LoginScreenController loginController = this.loginScreenControllerProvider.get();
        loginController.userName.set(textFieldUserName.getText());
        this.app.show(loginController);
    }

    public void leave() {
        this.app.show(loginScreenControllerProvider.get());
    }
}