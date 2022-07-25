package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.StylesService;
import de.uniks.pioneers.services.UserService;
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
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.TextMatchers;

import javax.inject.Provider;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignUpScreenControllerTest extends ApplicationTest {
    @Mock
    UserService userService;

    @Mock
    PrefService prefService;

    @Mock
    StylesService stylesService;

    @Spy
    Provider<LoginScreenController> loginScreenControllerProvider;

    @Spy
    App app = new App(null);

    @InjectMocks
    SignUpScreenController signUpScreenController;

    @Override
    public void start(Stage stage) {

        app.start(stage);
        app.show(signUpScreenController);
    }

    @Test
    void register() {
        String snakeAvatarB64 = "data:image/png;base64,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";

        when(userService.register("Test",snakeAvatarB64,"12345678")).thenReturn(Observable.just(new User("000","Test","online",snakeAvatarB64)));
        write("Test\t");
        write("1234567");
        FxAssert.verifyThat("#passwordStatusText", TextMatchers.hasText("Password must be at least 8 characters long"));
        write("8\t");
        FxAssert.verifyThat("#passwordStatusText", TextMatchers.hasText("Passwords do not match"));
        FxAssert.verifyThat("#buttonRegister", NodeMatchers.isDisabled());
        write("12345678\t");
        FxAssert.verifyThat("#passwordStatusText", TextMatchers.hasText(""));
        type(KeyCode.DOWN);
        write("\t");
        write("\t");
        type(KeyCode.SPACE);
        verify(userService).register("Test",snakeAvatarB64, "12345678");
    }
}