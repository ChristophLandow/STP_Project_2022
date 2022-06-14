package de.uniks.pioneers;

import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.matcher.control.TextMatchers;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class AppTest extends ApplicationTest {

    @Override
    public void start(Stage stage){

        final App app = new App(null);
        MainComponent testComponent = DaggerTestComponent.builder().mainApp(app).build();
        app.start(stage);
        app.show(testComponent.loginController());
    }

    @Test
    public void test(){

        //LoginScreen
        write("TestUser\t");
        write("12345678\t\t\t");
        type(KeyCode.ENTER);
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
        clickOn("#gameNameTextField");
        write("TestGame\t");
        verifyThat("#gameNameTextField", TextInputControlMatchers.hasText("TestGame"));
        write("12345678\t\t");
        verifyThat("#passwordTextField", TextInputControlMatchers.hasText("12345678"));
        type(KeyCode.ENTER);
        //NewGameLobbyScreen
        verifyThat("#gameNameLabel", LabeledMatchers.hasText("TestGame"));
        verifyThat("#passwordLabel", LabeledMatchers.hasText("12345678"));
        TestModule.gameMemberSubject.onNext(new Event<>(".created", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","001", false, "#ffffff")));
        TestModule.gameMemberSubject.onNext(new Event<>(".created", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","002", false, "#000000")));
        TestModule.gameMemberSubject.onNext(new Event<>(".created", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","003", false, "#888888")));
        TestModule.gameSubject.onNext(new Event<>(".updated", new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","000","000",3, false)));
        TestModule.gameMemberSubject.onNext(new Event<>(".updated", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","001", true, "#ffffff")));
        TestModule.gameMemberSubject.onNext(new Event<>(".updated", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","002", true, "#000000")));
        TestModule.gameMemberSubject.onNext(new Event<>(".updated", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","003", true, "#888888")));
        write("\tHallo Test Test\t");
        type(KeyCode.ENTER);
        write("\t");
        type(KeyCode.ENTER);
        type(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "founding-roll"));
    }


}
