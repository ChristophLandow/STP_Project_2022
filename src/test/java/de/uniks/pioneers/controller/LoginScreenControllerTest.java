package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.services.EventHandlerService;
import de.uniks.pioneers.services.LoginService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.StylesService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.TextMatchers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginScreenControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);
    @Mock
    LoginService loginService;
    @Mock
    PrefService prefService;

    @Mock
    StylesService stylesService;

    @Mock
    EventHandlerService eventHandlerService;

    @InjectMocks
    LoginScreenController loginScreenController;
    @Override
    public void start(Stage stage){

        when(prefService.recall()).thenReturn("");
        app.start(stage);
        app.show(loginScreenController);
    }
    @Test
    public void login(){

        when(loginService.login("Test","12345678")).thenReturn(Observable.just(new LoginResult("000","Test","online","","accessToken","refreshToken")));

        FxAssert.verifyThat("#userNameStatusText", TextMatchers.hasText("Please enter a valid user name"));
        write("Test\t");
        FxAssert.verifyThat("#userNameStatusText", TextMatchers.hasText(""));
        write("12345678\t");
        type(KeyCode.SPACE);
        write("\t");
        type(KeyCode.SPACE);

        verify(loginService).login("Test","12345678");

    }
}