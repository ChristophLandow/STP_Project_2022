package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.services.LoginService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static de.uniks.pioneers.Constants.LOGIN_SCREEN_TITLE;


public class LoginScreenController implements Controller {
    @FXML
    public AnchorPane root;
    @FXML
    public VBox vBoxParent;
    @FXML
    public Label pioneersLabel;
    @FXML
    public TextField nicknameTextField;
    @FXML
    public TextField passwordTextField;
    public Label IncorrectInputLabel;
    @FXML
    public CheckBox rememberMeCheckBox;
    @FXML
    public Label signUpLabel;
    @FXML
    public Label goToRulesLabel;
    @FXML
    public Button loginButton;

    private App app;
    private final LoginService loginService;
    private final Provider<SignUpScreenController> signUpScreenControllerProvider;

    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;


    @Inject
    public LoginScreenController(App app, LoginService loginService, Provider<SignUpScreenController> signUpScreenControllerProvider,
                                 Provider<LobbyScreenController> lobbyScreenControllerProvider) {
        this.app = app;
        this.loginService = loginService;
        this.signUpScreenControllerProvider = signUpScreenControllerProvider;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
    }

    @Override
    public Parent render() {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("views/LoginScreen.fxml"));
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
        app.getStage().setTitle(LOGIN_SCREEN_TITLE);
    }


    @Override
    public void stop() {
    }

    public void signUp(MouseEvent mouseEvent) {
        SignUpScreenController controller = signUpScreenControllerProvider.get();
        controller.username.set(nicknameTextField.getText());
        controller.password.set(passwordTextField.getText());
        app.show(controller);
    }


    public void login(MouseEvent mouseEvent) {
        String nickname = nicknameTextField.getText();
        String password = passwordTextField.getText();

        if (nickname != null && !nickname.isBlank() && password != null && !password.isBlank()) {
            loginService.login(nicknameTextField.getText(), passwordTextField.getText(), loginResult -> {
                Platform.runLater(() -> {
                    Response<LoginResult> response = loginResult;
                    int code = response.code();
                    if (code == 201) {
                        //build model Data from response body and show Lobby screen
                        // this code is for test purpose
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Login successfull !");
                        alert.setContentText("here comes the lobby Screen");
                        alert.setOnCloseRequest(event -> app.show(this));
                        alert.showAndWait();
                    } else {
                        try {
                            displayInvalidLogin(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            });
        } else {
            // we can edit this alert, function, buttons etc. !!
            new Alert(Alert.AlertType.ERROR, "need username and password").showAndWait();
        }
    }

    private void displayInvalidLogin(Response<LoginResult> response) throws IOException {
        new Alert(Alert.AlertType.ERROR, response.errorBody().string()).showAndWait();
    }


    public void rememberMe(MouseEvent mouseEvent) {
    }

    public void goToRules(MouseEvent mouseEvent) {
    }
}
