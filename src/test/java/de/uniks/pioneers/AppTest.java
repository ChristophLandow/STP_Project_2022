package de.uniks.pioneers;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.matcher.control.TextMatchers;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.api.FxAssert.verifyThat;


class AppTest extends ApplicationTest {

    @Override
    public void start(Stage stage){

        final App app = new App(null);
        MainComponent testComponent = DaggerTestComponent.builder().mainApp(app).build();
        app.start(stage);
        app.show(testComponent.loginController());

    }

    @Test
    public void test() throws InterruptedException {

        //LoginScreen
        write("TestUser\t");
        write("12345678\t");
        clickOn("#textRegister");
        //SignUpScreen
        verifyThat("#textFieldUserName", TextInputControlMatchers.hasText("TestUser"));
        verifyThat("#passwordField", TextInputControlMatchers.hasText("12345678"));
        write("\t\t12345678\t\t\t");
        type(KeyCode.ENTER);
        //Dialog
        verifyThat(".information", Node::isVisible);
        type(KeyCode.ENTER);
        //LoginScreen
        write("\t12345678\t");
        verifyThat("#textFieldUserName", TextInputControlMatchers.hasText("TestUser"));
        verifyThat("#passwordField", TextInputControlMatchers.hasText("12345678"));
        type(KeyCode.ENTER);
        write("\t");
        type(KeyCode.ENTER);
        //LobbyScreen
        type(KeyCode.ENTER);
        //EditProfileScreen
        verifyThat("#usernameLabel", TextMatchers.hasText("TestUser"));
        write("\t\t\t\t\t");
        type(KeyCode.ENTER);
        //LobbyScreen
        write("\t\t");
        type(KeyCode.ENTER);
        write("TestGame\t");
        write("12345678\t\t");
        type(KeyCode.ENTER);
        //NewGameLobbyScreen
        verifyThat("#gameNameLabel", LabeledMatchers.hasText("TestGame"));
        verifyThat("#passwordLabel", LabeledMatchers.hasText("12345678"));
        write("Test\t");
        type(KeyCode.ENTER);
        write("\t\t");
        type(KeyCode.ENTER);
        write("\t");
        type(KeyCode.ENTER);

    }


}
