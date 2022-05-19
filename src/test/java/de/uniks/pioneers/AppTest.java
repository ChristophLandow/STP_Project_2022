package de.uniks.pioneers;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.matcher.control.TextMatchers;


class AppTest extends ApplicationTest {

    @Override
    public void start(Stage stage){

        final App app = new App(null);
        MainComponent testComponent = DaggerTestComponent.builder().mainApp(app).build();
        app.start(stage);
        app.show(testComponent.loginController());

    }

    @Test
    public void test() {


        //Test module still provisionally uses Server functionality! Implementation of tests may require adjustment to respective classes in TestModule class!

        //LoginScreen
        write("TestUser\t");
        write("12345678\t");
        clickOn("#textRegister");
        //SignUpScreen
        FxAssert.verifyThat("#textFieldUserName", TextInputControlMatchers.hasText("TestUser"));
        FxAssert.verifyThat("#passwordField", TextInputControlMatchers.hasText("12345678"));
        write("\t\t12345678\t\t\t");
        type(KeyCode.ENTER);
        //Dialog
        type(KeyCode.ENTER);
        //LoginScreen
        write("\t12345678\t");
        FxAssert.verifyThat("#textFieldUserName", TextInputControlMatchers.hasText("TestUser"));
        FxAssert.verifyThat("#passwordField", TextInputControlMatchers.hasText("12345678"));
        type(KeyCode.ENTER);
        write("\t");
        type(KeyCode.ENTER);
        //LobbyScreen
        type(KeyCode.ENTER);
        //EditProfileScreen
        FxAssert.verifyThat("#usernameLabel", TextMatchers.hasText("TestUser"));
        write("\t\t\t\t\t");
        type(KeyCode.ENTER);
        //LobbyScreen
        write("\t\t");
        type(KeyCode.ENTER);
        write("TestGame\t");
        write("12345678\t\t");
        type(KeyCode.ENTER);
        write("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    }


}
