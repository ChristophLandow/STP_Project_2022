package de.uniks.pioneers;

import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.*;
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
import java.util.Arrays;

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
    public void test() throws InterruptedException {
        //LoginScreen
        WaitForAsyncUtils.waitForFxEvents();
        write("TestUser\t");
        write("12345678\t\t\t");
        type(KeyCode.ENTER);

        //SignUpScreen
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#textFieldUserName", TextInputControlMatchers.hasText("TestUser"));
        verifyThat("#passwordField", TextInputControlMatchers.hasText("12345678"));
        write("\t\t12345678\t\t\t");
        type(KeyCode.ENTER);

        //Dialog
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(".information", Node::isVisible);
        type(KeyCode.ENTER);

        //LoginScreen
        WaitForAsyncUtils.waitForFxEvents();
        write("\t12345678\t");
        verifyThat("#textFieldUserName", TextInputControlMatchers.hasText("TestUser"));
        verifyThat("#passwordField", TextInputControlMatchers.hasText("12345678"));
        type(KeyCode.ENTER);
        write("\t");
        type(KeyCode.ENTER);

        //LobbyScreen
        WaitForAsyncUtils.waitForFxEvents();
        type(KeyCode.ENTER);

        //EditProfileScreen
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#usernameLabel", TextMatchers.hasText("TestUser"));
        write("\t\t\t\t\t");
        type(KeyCode.ENTER);

        //LobbyScreen
        WaitForAsyncUtils.waitForFxEvents();
        write("\t\t");
        type(KeyCode.ENTER);
        clickOn("#gameNameTextField");
        write("TestGame\t");
        verifyThat("#gameNameTextField", TextInputControlMatchers.hasText("TestGame"));
        write("12345678\t\t");
        verifyThat("#passwordTextField", TextInputControlMatchers.hasText("12345678"));
        type(KeyCode.ENTER);

        //NewGameLobbyScreen
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#gameNameLabel", LabeledMatchers.hasText("TestGame"));
        verifyThat("#passwordLabel", LabeledMatchers.hasText("12345678"));
        TestModule.gameMemberSubject.onNext(new Event<>(".created", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","001", false, "#ffffff", false)));
        TestModule.gameMemberSubject.onNext(new Event<>(".created", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","002", false, "#000000", false)));
        TestModule.gameMemberSubject.onNext(new Event<>(".created", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","003", false, "#888888", false)));
        TestModule.gameSubject.onNext(new Event<>(".updated", new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","000","000",3, false, null)));
        TestModule.gameMemberSubject.onNext(new Event<>(".updated", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","001", true, "#ffffff", false)));
        TestModule.gameMemberSubject.onNext(new Event<>(".updated", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","002", true, "#000000", false)));
        TestModule.gameMemberSubject.onNext(new Event<>(".updated", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","003", true, "#888888", false)));
        write("\t");
        type(KeyCode.DOWN);
        write("\t\tHallo Test Test\t");
        type(KeyCode.ENTER);
        write("\t");
        type(KeyCode.ENTER);
        type(KeyCode.ENTER);

        //IngameScreen
        WaitForAsyncUtils.waitForFxEvents();
        write("\t\t\tHallo Test");
        type(KeyCode.ENTER);
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "roll the dice"));
        clickOn("#leftDiceImageView");
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", Arrays.asList(new ExpectedMove[]{new ExpectedMove("founding-roll", Arrays.asList(new String[]{"001", "002", "003"}))}), null)));
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_001:\n" + "roll the dice"));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", Arrays.asList(new ExpectedMove[]{new ExpectedMove("founding-roll", Arrays.asList(new String[]{"002", "003"}))}), null)));
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_002:\n" + "roll the dice"));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", Arrays.asList(new ExpectedMove[]{new ExpectedMove("founding-roll", Arrays.asList(new String[]{"003"}))}), null)));
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "roll the dice"));

        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", Arrays.asList(new ExpectedMove[]{new ExpectedMove("founding-settlement-1", Arrays.asList(new String[]{"000"})),
                                                                                                                                                    new ExpectedMove("founding-road-1", Arrays.asList(new String[]{"000"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-1", Arrays.asList(new String[]{"002"})),
                                                                                                                                                    new ExpectedMove("founding-road-1", Arrays.asList(new String[]{"002"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-1", Arrays.asList(new String[]{"003"})),
                                                                                                                                                    new ExpectedMove("founding-road-1", Arrays.asList(new String[]{"003"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-1", Arrays.asList(new String[]{"001"})),
                                                                                                                                                    new ExpectedMove("founding-road-1", Arrays.asList(new String[]{"001"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-2", Arrays.asList(new String[]{"003"})),
                                                                                                                                                    new ExpectedMove("founding-road-2", Arrays.asList(new String[]{"003"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-2", Arrays.asList(new String[]{"002"})),
                                                                                                                                                    new ExpectedMove("founding-road-2", Arrays.asList(new String[]{"002"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-2", Arrays.asList(new String[]{"000"})),
                                                                                                                                                    new ExpectedMove("founding-road-2", Arrays.asList(new String[]{"000"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-2", Arrays.asList(new String[]{"001"})),
                                                                                                                                                    new ExpectedMove("founding-road-2", Arrays.asList(new String[]{"001"})),
                                                                                                                                                    new ExpectedMove("roll", Arrays.asList(new String[]{"003"}))}), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(0, 1, -1, "settlement1", 0, "settlement", "000", "000")));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", Arrays.asList(new ExpectedMove[]{new ExpectedMove("founding-road-1", Arrays.asList(new String[]{"000"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-1", Arrays.asList(new String[]{"002"})),
                                                                                                                                                    new ExpectedMove("founding-road-1", Arrays.asList(new String[]{"002"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-1", Arrays.asList(new String[]{"003"})),
                                                                                                                                                    new ExpectedMove("founding-road-1", Arrays.asList(new String[]{"003"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-1", Arrays.asList(new String[]{"001"})),
                                                                                                                                                    new ExpectedMove("founding-road-1", Arrays.asList(new String[]{"001"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-2", Arrays.asList(new String[]{"003"})),
                                                                                                                                                    new ExpectedMove("founding-road-2", Arrays.asList(new String[]{"003"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-2", Arrays.asList(new String[]{"002"})),
                                                                                                                                                    new ExpectedMove("founding-road-2", Arrays.asList(new String[]{"002"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-2", Arrays.asList(new String[]{"000"})),
                                                                                                                                                    new ExpectedMove("founding-road-2", Arrays.asList(new String[]{"000"})),
                                                                                                                                                    new ExpectedMove("founding-settlement-2", Arrays.asList(new String[]{"001"})),
                                                                                                                                                    new ExpectedMove("founding-road-2", Arrays.asList(new String[]{"001"})),
                                                                                                                                                    new ExpectedMove("roll", Arrays.asList(new String[]{"003"}))}), null)));
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "place road"));
        sleep(10000);
    }
}
