package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.services.UserService;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

@ExtendWith(MockitoExtension.class)
class SignUpScreenControllerTest extends ApplicationTest {

    @Mock
    UserService userService;

    @InjectMocks
    SignUpScreenController signUpScreenController;

    @Override
    public void start(Stage stage){

        //new App().start(stage);
    }


    @Test
    void register(){



    }
}