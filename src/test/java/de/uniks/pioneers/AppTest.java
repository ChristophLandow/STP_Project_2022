package de.uniks.pioneers;

import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.GameSettings;
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
import java.util.List;

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
    public void
    test() throws InterruptedException {
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
        TestModule.gameSubject.onNext(new Event<>(".updated", new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","000","000",3, false, new GameSettings(1,4))));
        TestModule.gameMemberSubject.onNext(new Event<>(".updated", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","001", true, "#ffffff", false)));
        TestModule.gameMemberSubject.onNext(new Event<>(".updated", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","002", true, "#000000", false)));
        TestModule.gameMemberSubject.onNext(new Event<>(".updated", new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","003", true, "#888888", false)));
        write("\t\t\tHallo Test Test\t");
        type(KeyCode.ENTER);
        write("\t");
        type(KeyCode.ENTER);
        type(KeyCode.ENTER);

        //IngameScreen
        WaitForAsyncUtils.waitForFxEvents();
        write("\t\t\tHallo Test");
        type(KeyCode.ENTER);
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "roll the dice"));
        clickOn("#rulesButton");
        clickOn("#leftDiceImageView");
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "0", "000", "000", "founding-roll", 4, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove[]{new ExpectedMove("founding-roll", List.of(new String[]{"001", "002", "003"}))}), null)));
        WaitForAsyncUtils.waitForFxEvents();

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_001:\n" + "roll the dice"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "1", "000", "001", "founding-roll", 4, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove[]{new ExpectedMove("founding-roll", List.of(new String[]{"002", "003"}))}), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_002:\n" + "roll the dice"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "2", "000", "002", "founding-roll", 4, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove[]{new ExpectedMove("founding-roll", List.of(new String[]{"003"}))}), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "roll the dice"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "3", "000", "003", "founding-roll", 4, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-1", List.of("000")), new ExpectedMove("founding-road-1", List.of("000")), new ExpectedMove("founding-settlement-1", List.of("002")), new ExpectedMove("founding-road-1", List.of("002")), new ExpectedMove("founding-settlement-1", List.of("003")), new ExpectedMove("founding-road-1", List.of("003")), new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(0, 0, 0, "1", 0, "settlement", "000", "000")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "4", "000", "000", "founding-settlement-1", 0, "1", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-1", List.of("000")), new ExpectedMove("founding-settlement-1", List.of("002")), new ExpectedMove("founding-road-1", List.of("002")), new ExpectedMove("founding-settlement-1", List.of("003")), new ExpectedMove("founding-road-1", List.of("003")), new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(1, 0, -1, "2", 7, "road", "000", "000")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "5", "000", "000", "founding-road-1", 0, "2", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-1", List.of("002")), new ExpectedMove("founding-road-1", List.of("002")), new ExpectedMove("founding-settlement-1", List.of("003")), new ExpectedMove("founding-road-1", List.of("003")), new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_002:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(-2, 1, 1, "3", 0, "settlement", "000", "002")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "6", "000", "002", "founding-settlement-1", 0, "3", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-1", List.of("002")), new ExpectedMove("founding-settlement-1", List.of("003")), new ExpectedMove("founding-road-1", List.of("003")), new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_002:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(-1, 1, 0, "4", 7, "road", "000", "002")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "7", "000", "002", "founding-settlement-1", 0, "4", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-1", List.of("003")), new ExpectedMove("founding-road-1", List.of("003")), new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(2, -1, -1, "5", 6, "settlement", "000", "003")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "8", "000", "003", "founding-settlement-1", 0, "5", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-1", List.of("003")), new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(1, -1, 0, "6", 3, "road", "000", "003")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "9", "000", "003", "founding-settlement-1", 0, "6", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_001:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(0, -1, 1, "7", 0, "settlement", "000", "001")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "10", "000", "001", "founding-settlement-1", 0, "7", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_001:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(0, -1, 1, "8", 11, "road", "000", "001")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "11", "000", "001", "founding-settlement-1", 0, "8", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(0, 2, -2, "9", 6, "settlement", "000", "003")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "12", "000", "003", "founding-settlement-1", 0, "9", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(-1, 2, -1, "10", 3, "road", "000", "003")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "13", "000", "003", "founding-settlement-1", 0, "10", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_002:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(-1, -1, 2, "11", 0, "settlement", "000", "002")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "14", "000", "002", "founding-settlement-1", 0, "11", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_002:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(-1, -1, 2, "12", 11, "road", "000", "002")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "15", "000", "002", "founding-settlement-1", 0, "12", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(2, 1, -3, "13", 6, "settlement", "000", "000")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "16", "000", "000", "founding-settlement-1", 0, "13", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(1, 1, -2, "14", 3, "road", "000", "000")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "17", "000", "000", "founding-settlement-1", 0, "14", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_001:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(3, -1, -2, "15", 6, "settlement", "000", "001")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "18", "000", "001", "founding-settlement-1", 0, "15", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_001:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(2, -1, -1, "16", 3, "road", "000", "001")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "19", "000", "001", "founding-settlement-1", 0, "16", null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "roll the dice"));
    }
}
