package de.uniks.pioneers;

import de.uniks.pioneers.controller.LoginScreenController;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

class AppTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception{

        final App app = new App(null);
        MainComponent testComponent = DaggerTestComponent.builder().mainApp(app).build();
        app.start(stage);
        app.show(testComponent.loginController());

    }

    @Test
    public void test() {

        write("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    //clickOn("#textFieldUserName");


    }

}