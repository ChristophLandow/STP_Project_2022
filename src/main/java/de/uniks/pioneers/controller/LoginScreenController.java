package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.services.LoginService;
import de.uniks.pioneers.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
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
    private final UserService userService;

    @Inject
    public LoginScreenController(App app, LoginService loginService, UserService userService) {
        this.app = app;
        this.loginService = loginService;
        this.userService = userService;
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
        SignUpScreenController controller = new SignUpScreenController(app, this, userService, loginService);
        controller.username.set(nicknameTextField.getText());
        controller.password.set(passwordTextField.getText());
        app.show(controller);
    }


    public void login(MouseEvent mouseEvent) {
        String nickname = nicknameTextField.getText();
        String password = passwordTextField.getText();

        if (nickname != null && !nickname.isBlank() && password != null && !password.isBlank()) {
            //RestService.login(nicknameTextField.getText(), passwordTextField.getText());
        } else {
            // we can edit this alert, function, buttons etc. !!
            new Alert(Alert.AlertType.ERROR, "need username and password").showAndWait();
        }
    }

    public void rememberMe(MouseEvent mouseEvent) {
    }

    public void goToRules(MouseEvent mouseEvent) {
    }
}
