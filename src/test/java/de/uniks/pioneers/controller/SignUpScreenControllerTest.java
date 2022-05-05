package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.services.UserService;
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
import org.testfx.matcher.control.LabeledMatchers;
import javax.inject.Provider;

@ExtendWith(MockitoExtension.class)
class SignUpScreenControllerTest extends ApplicationTest {

    @Mock
    UserService userService;

    @Spy
    Provider<LoginScreenController> loginScreenControllerProvider;

    @InjectMocks
    SignUpScreenController signUpScreenController;

    @Override
    public void start(Stage stage) throws Exception {

        //new App(signUpScreenController).start(stage);
    }


    @Test
    void register(){

        //write("Test\t");
        //write("1234567");
        //FxAssert.verifyThat("#passwordStatusText", LabeledMatchers.hasText("Password must be at least 8 characters long"));
        //write("8\t");
        //FxAssert.verifyThat("#passwordStatusText", LabeledMatchers.hasText("Passwords do not match"));
        //FxAssert.verifyThat("#buttonRegister", NodeMatchers.isDisabled());
        //write("12345678\t");
        //FxAssert.verifyThat("#passwordStatusText", LabeledMatchers.hasText(""));


    }
}