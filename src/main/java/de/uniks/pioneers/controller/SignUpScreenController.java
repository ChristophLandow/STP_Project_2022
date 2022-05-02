package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.AvatarSpinnerController;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.UserService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static de.uniks.pioneers.Constants.SIGNUP_SCREEN_TITLE;

public class SignUpScreenController implements Controller{

    @FXML
    public AnchorPane root;
    @FXML
    public VBox vBoxParent;
    @FXML
    public TextField newUsernameTextField;
    @FXML
    public TextField passwordTextField;
    @FXML
    public TextField repeatPasswordTextField;
    @FXML
    public HBox avatarHbox;
    @FXML
    public VBox uploadAvatarVbox;
    @FXML
    public Spinner chooseAvatarSpinner;
    @FXML
    public Button uploadAvatarButton;
    @FXML
    public ImageView avatarImageView;
    @FXML
    public Button signUpButton;
    @FXML
    public Label errorLabel;

    private App app;
    private final Provider<LoginScreenController> loginScreenControllerProvider;

    private UserService userService;


    public final SimpleStringProperty userName = new SimpleStringProperty();
    public final SimpleStringProperty password = new SimpleStringProperty();

    private String avatarStr;
    private Alert alert;
    private int signUpStatus;

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
        stage.setOnCloseRequest(event -> {
            if (stage.getTitle().equals(SIGNUP_SCREEN_TITLE)) {
                event.consume();
                app.show(loginScreenControllerProvider.get());
            }
        });
        //Spinner Code
        AvatarSpinnerController spinnerValueFactory = new AvatarSpinnerController(this::updateAvatarString);
        spinnerValueFactory.init(avatarImageView);
        chooseAvatarSpinner.setValueFactory(spinnerValueFactory);


    }

    @Override
    public Parent render() {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/SignUpScreen.fxml"));
            loader.setControllerFactory(c -> this);
            final Parent view;
        try {
            view = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        newUsernameTextField.textProperty().bindBidirectional(userName);
        passwordTextField.textProperty().bindBidirectional(password);

        final BooleanBinding match = Bindings.equal(passwordTextField.textProperty(),repeatPasswordTextField.textProperty());
        errorLabel.textProperty().bind(
                Bindings.when(match)
                        .then("")
                        .otherwise("passwords do not match"));
        signUpButton.disableProperty().bind(match.not());

        return view;
    }

    @Override
    public void stop() {
    }


    public void signUp(MouseEvent mouseEvent) throws IOException {
        // we can do this with a binding, might be more clean
        if (!newUsernameTextField.getText().isBlank() && !passwordTextField.getText().isBlank()
                &&!repeatPasswordTextField.getText().isBlank()){
            userService.register(newUsernameTextField.getText(),passwordTextField.getText(), userResponse -> {
                Platform.runLater(() -> {
                    try {
                        checkResponseCode(userResponse);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }
    private void checkResponseCode(Response<User> response) throws IOException {
        if (response.code() == 201) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Sign Up Succesfull");
            alert.setContentText("Welcome to the pioneers community");
            alert.setOnCloseRequest(event -> app.show(loginScreenControllerProvider.get()));
            alert.showAndWait();
        } else if (response.code() == 400) {
            new Alert(Alert.AlertType.ERROR, response.errorBody().string()).showAndWait();
        } else if (response.code() == 401) {
            new Alert(Alert.AlertType.ERROR, response.errorBody().string()).showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, response.errorBody().string()).showAndWait();
        }
    }

    private void updateAvatarString(String newAvatar){
        avatarStr = newAvatar;
    }

    public void uploadAvatar(MouseEvent mouseEvent) {
    }
}
