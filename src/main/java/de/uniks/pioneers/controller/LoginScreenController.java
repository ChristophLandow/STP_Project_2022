package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.services.LoginService;
import de.uniks.pioneers.services.PrefService;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javax.inject.Inject;
import javax.inject.Provider;
import static de.uniks.pioneers.Constants.*;

public class LoginScreenController implements Controller {
    @FXML public TextField textFieldUserName;
    @FXML public PasswordField passwordField;
    @FXML public Button buttonLogin;
    @FXML public Text userNameStatusText;
    @FXML public Text passwordStatusText;
    @FXML public CheckBox checkRememberMe;
    @FXML public Hyperlink textRegister;
    @FXML public Hyperlink textRules;

    public final SimpleStringProperty userName = new SimpleStringProperty();
    public final SimpleStringProperty password = new SimpleStringProperty();
    private final App app;
    private final LoginService loginService;
    private final Provider<SignUpScreenController> signUpScreenControllerProvider;
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;
    private final PrefService prefService;
    private boolean darkMode = false;

    @Inject
    public LoginScreenController(App app, LoginService loginService, Provider<SignUpScreenController> signUpScreenControllerProvider, Provider<LobbyScreenController> lobbyScreenControllerProvider, Provider<RulesScreenController> rulesScreenControllerProvider, PrefService prefService) {
        this.app = app;
        this.loginService = loginService;
        this.signUpScreenControllerProvider = signUpScreenControllerProvider;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
        this.prefService = prefService;
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

            //clear error messages on interaction with text fields
            this.textFieldUserName.setOnMouseClicked(this::resetStatus);
            this.passwordField.setOnMouseClicked(this::resetStatus);

            final IntegerBinding userNameLength = Bindings.length(textFieldUserName.textProperty());
            final BooleanBinding invalid = Bindings.equal(userNameStatusText.textProperty(), passwordStatusText.textProperty()).not();

            userNameStatusText.textProperty().bind(Bindings.when(userNameLength.greaterThan(0)).then("").otherwise("Please enter a valid user name"));
            buttonLogin.disableProperty().bind(invalid);

            return parent;
        } catch (Exception e) {
            System.err.println("Error loading Login Screen.");
            return null;
        }
    }

    private void resetStatus(MouseEvent mouseEvent) {
        this.passwordStatusText.setText("");
    }

    @Override
    public void init() {
        //attempt to retrieve refresh token fpr RememberMe login
        if(!this.prefService.recall().equals("")){

            this.loginService.refresh()
                    .observeOn(FX_SCHEDULER)
                    .doOnError(e -> System.out.println("An error has occurred during refresh login."))
                    .doOnComplete(this::loginComplete)
                    .subscribe();
        }
        app.getStage().setTitle(LOGIN_SCREEN_TITLE);
        if(darkMode){
            app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_LoginScreen.css");
        }
    }

    @Override
    public void stop() {

    }
    public void login() {
        this.loginService.login(this.textFieldUserName.getText(), this.passwordField.getText())
                .observeOn(FX_SCHEDULER)
                .doOnError(e -> this.passwordStatusText.setText("Incorrect user name or password"))
                .doOnComplete(this::loginComplete)
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}
                    @Override
                    public void onNext(@NonNull LoginResult loginResult) {}
                    @Override
                    public void onError(@NonNull Throwable e) {}
                    @Override
                    public void onComplete() {}
                });
    }

    private void loginComplete() {
        if(this.checkRememberMe.isSelected()) {
            this.prefService.remember();
        }
        toLobby();

    }
    public void toSignUp() {
        SignUpScreenController signUpScreenController = this.signUpScreenControllerProvider.get();
        signUpScreenController.userName.set(textFieldUserName.getText());
        signUpScreenController.password.set(passwordField.getText());
        if(darkMode){
            signUpScreenController.setDarkMode();
        }
        this.app.show(signUpScreenController);
    }

    public void toRules() {
        RulesScreenController ruleController = rulesScreenControllerProvider.get();
        if(darkMode){
            ruleController.setDarkMode();
        }
        ruleController.init();
    }

    public void toLobby() {
        LobbyScreenController lobbyController = lobbyScreenControllerProvider.get();
        if(darkMode){
            lobbyController.setDarkMode();
        }
        this.app.show(lobbyController);
    }

    public void setDarkMode() {
        darkMode = true;
    }

    public void setBrightMode(){
        darkMode = false;
    }

    public App getApp() {
        return this.app;
    }
}
