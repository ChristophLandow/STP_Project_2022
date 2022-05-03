package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.services.LoginService;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.pioneers.Constants.*;


public class LoginScreenController implements Controller {


    public final SimpleStringProperty userName = new SimpleStringProperty();
    public final SimpleStringProperty password = new SimpleStringProperty();
    private final App app;
    private final LoginService loginService;
    private final Provider<SignUpScreenController> signUpScreenControllerProvider;
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;

    @FXML
    public TextField textFieldUserName;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button buttonLogin;
    @FXML
    public Text userNameStatusText;
    @FXML
    public Text passwordStatusText;
    @FXML
    public CheckBox checkRememberMe;

    @FXML
    public Text textRegister;
    @FXML
    public Text textRules;

    @Inject
    public LoginScreenController(App app, LoginService loginService, Provider<SignUpScreenController> signUpScreenControllerProvider, Provider<LobbyScreenController> lobbyScreenControllerProvider) {
        this.app = app;
        this.loginService = loginService;
        this.signUpScreenControllerProvider = signUpScreenControllerProvider;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
    }

    @Override
    public Parent render() {

        Parent parent;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/LoginScreen.fxml"));
        loader.setControllerFactory(c -> this);
        try {

            parent = loader.load();
            textFieldUserName.textProperty().bindBidirectional(userName);
            passwordField.textProperty().bindBidirectional(password);

            this.textFieldUserName.setOnMouseClicked(this::resetStatus);
            this.passwordField.setOnMouseClicked(this::resetStatus);

            final IntegerBinding userNameLength = Bindings.length(textFieldUserName.textProperty());
            final BooleanBinding invalid = Bindings.equal(userNameStatusText.textProperty(), passwordStatusText.textProperty()).not();

            userNameStatusText.textProperty().bind(Bindings.when(userNameLength.greaterThan(0)).then("").otherwise("Please enter a valid user name"));
            buttonLogin.disableProperty().bind(invalid);

            this.textRegister.setOnMouseEntered(this::markRegister);
            this.textRegister.setOnMouseExited(this::unmarkRegister);
            this.textRegister.setOnMouseClicked(this::toSignUp);

            this.textRules.setOnMouseEntered(this::markRules);
            this.textRules.setOnMouseExited(this::unmarkRules);
            this.textRules.setOnMouseClicked(this::toRules);

            return parent;

        } catch (Exception e) {
            System.err.println("Error loading Login Screen.");
            return null;
        }

    }

    private void markRegister(MouseEvent mouseEvent) {

        this.textRegister.setFill(Color.rgb(0, 0, 255));
    }

    private void unmarkRegister(MouseEvent mouseEvent) {

        this.textRegister.setFill(Color.rgb(0, 0, 0));
    }

    private void markRules(MouseEvent mouseEvent) {

        this.textRules.setFill(Color.rgb(0, 0, 255));
    }

    private void unmarkRules(MouseEvent mouseEvent) {

        this.textRules.setFill(Color.rgb(0, 0, 0));
    }

    private void resetStatus(MouseEvent mouseEvent) {

        this.passwordStatusText.setText("");
    }

    @Override
    public void init() {

        app.getStage().setTitle(LOGIN_SCREEN_TITLE);
    }

    @Override
    public void stop() {
    }

    public void login(ActionEvent event) {

        this.loginService.login(this.textFieldUserName.getText(), this.passwordField.getText())
                .observeOn(FX_SCHEDULER)
                .doOnError(e -> this.passwordStatusText.setText("Incorrect user name or password"))
                .doOnComplete(this::toLobby)
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull LoginResult loginResult) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }



    public void rememberMe(MouseEvent mouseEvent) {
    }

    public void toSignUp(MouseEvent mouseEvent) {

        SignUpScreenController signUpScreenController = this.signUpScreenControllerProvider.get();
        signUpScreenController.userName.set(textFieldUserName.getText());
        signUpScreenController.password.set(passwordField.getText());

        this.app.show(signUpScreenController);
    }

    public void toRules(MouseEvent mouseEvent) {
        System.out.println("toRules");
    }

    public void toLobby() {
        this.app.show(lobbyScreenControllerProvider.get());
    }
}
