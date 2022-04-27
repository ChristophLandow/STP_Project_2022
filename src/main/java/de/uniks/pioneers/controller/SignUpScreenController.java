package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.SignUpSpinnerController;
import de.uniks.pioneers.services.LoginService;
import de.uniks.pioneers.services.UserService;
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


    public final SimpleStringProperty username = new SimpleStringProperty();
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
        SignUpSpinnerController spinnerValueFactory = new SignUpSpinnerController(this::updateAvatarString);
        spinnerValueFactory.init(avatarImageView);
        chooseAvatarSpinner.setValueFactory(spinnerValueFactory);
        //signUpButton
        signUpButton.setOnMouseClicked(this::signUp);

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

        newUsernameTextField.textProperty().bindBidirectional(username);
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

    private void checkResponseCode(int signUpStatus) {
        if (signUpStatus == 201) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Sign Up Succesfull");
            alert.setContentText("Welcome to the pioneers community");
            alert.setOnCloseRequest(event -> app.show(loginScreenControllerProvider.get()));
            alert.showAndWait();
        } else if (signUpStatus == 400) {
            new Alert(Alert.AlertType.ERROR, "Validation failed!").showAndWait();
        } else if (signUpStatus == 429) {
            new Alert(Alert.AlertType.ERROR, "Rate limit reached").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, "Rate limit reached").showAndWait();
        }
    }


    public void signUp(MouseEvent mouseEvent) {
        if (!newUsernameTextField.getText().isBlank() && !passwordTextField.getText().isBlank()
                &&!repeatPasswordTextField.getText().isBlank()
                && repeatPasswordTextField.getText().equals(passwordTextField.getText())) {
            userService.register(newUsernameTextField.getText(),passwordTextField.getText());
            checkResponseCode(signUpStatus);
        }
    }

    private void updateAvatarString(String newAvatar){
        avatarStr = newAvatar;
    }

    public void uploadAvatar(MouseEvent mouseEvent) {
    }
}
