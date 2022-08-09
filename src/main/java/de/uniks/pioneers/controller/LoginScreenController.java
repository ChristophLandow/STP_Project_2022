package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.services.*;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.LOGIN_SCREEN_TITLE;

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
    private final StylesService stylesService;
    private final EventHandlerService eventHandlerService;

    @Inject
    public LoginScreenController(App app, LoginService loginService, EventHandlerService eventHandlerService,
                                 Provider<SignUpScreenController> signUpScreenControllerProvider, Provider<LobbyScreenController> lobbyScreenControllerProvider,
                                 Provider<RulesScreenController> rulesScreenControllerProvider, PrefService prefService, StylesService stylesService) {
        this.app = app;
        this.loginService = loginService;
        this.signUpScreenControllerProvider = signUpScreenControllerProvider;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
        this.prefService = prefService;
        this.stylesService = stylesService;
        this.eventHandlerService = eventHandlerService;
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
            this.textFieldUserName.setOnMouseClicked(mouseEvent -> resetStatus());
            this.passwordField.setOnMouseClicked(mouseEvent -> resetStatus());

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

    private void resetStatus() {
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
        Node passwordFieldNode = this.passwordField;
        eventHandlerService.setEnterEventHandler(passwordFieldNode, this.buttonLogin);
        app.getStage().setTitle(LOGIN_SCREEN_TITLE);
        stylesService.setStyleSheets(app.getStage().getScene().getStylesheets());
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
        this.app.show(signUpScreenController);
    }

    public void toRules() {
        RulesScreenController ruleController = rulesScreenControllerProvider.get();
        ruleController.init();
    }

    public void toLobby() {
        LobbyScreenController lobbyController = lobbyScreenControllerProvider.get();
        this.app.show(lobbyController);
    }

    public App getApp() {
        return this.app;
    }
}
