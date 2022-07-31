package de.uniks.pioneers;

import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.dto.RobDto;
import de.uniks.pioneers.model.*;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.matcher.control.TextMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class AppTest extends ApplicationTest {
    final App app = new App(null);

    @Override
    public void start(Stage stage) {
        MainComponent testComponent = DaggerTestComponent.builder().mainApp(app).build();
        app.start(stage);
        app.show(testComponent.loginController());
    }

    @Test
    public void
    test() throws TimeoutException {
        //LoginScreen
        WaitForAsyncUtils.waitForFxEvents();
        write("TestUser\t");
        write("12345678\t\t\t");
        type(KeyCode.SPACE);

        //SignUpScreen
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#textFieldUserName", TextInputControlMatchers.hasText("TestUser"));
        verifyThat("#passwordField", TextInputControlMatchers.hasText("12345678"));
        write("\t\t12345678\t\t\t");
        type(KeyCode.SPACE);

        //Dialog
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(".information", Node::isVisible);
        type(KeyCode.SPACE);

        //LoginScreen
        WaitForAsyncUtils.waitForFxEvents();
        write("\t12345678\t");
        verifyThat("#textFieldUserName", TextInputControlMatchers.hasText("TestUser"));
        verifyThat("#passwordField", TextInputControlMatchers.hasText("12345678"));
        type(KeyCode.SPACE);
        write("\t");
        type(KeyCode.SPACE);

        //LobbyScreen
        WaitForAsyncUtils.waitForFxEvents();
        type(KeyCode.SPACE);

        //EditProfileScreen
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#usernameLabel", TextMatchers.hasText("TestUser"));
        write("\t\t\t\t\t");
        type(KeyCode.SPACE);

        //LobbyScreen
        WaitForAsyncUtils.waitForFxEvents();
        write("\t\t");
        type(KeyCode.SPACE);
        clickOn("#gameNameTextField");
        write("TestGame\t");
        verifyThat("#gameNameTextField", TextInputControlMatchers.hasText("TestGame"));
        write("12345678\t\t");
        verifyThat("#passwordTextField", TextInputControlMatchers.hasText("12345678"));
        type(KeyCode.SPACE);

        //NewGameLobbyScreen
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#gameNameLabel", LabeledMatchers.hasText("TestGame"));
        verifyThat("#passwordLabel", LabeledMatchers.hasText("12345678"));
        TestModule.gameMemberSubject.onNext(new Event<>(".created", new Member("2022-05-18T18:12:58.114Z", "2022-05-18T18:12:58.114Z", "000", "001", false, "#ffffff", false)));
        TestModule.gameMemberSubject.onNext(new Event<>(".created", new Member("2022-05-18T18:12:58.114Z", "2022-05-18T18:12:58.114Z", "000", "002", false, "#000000", false)));
        TestModule.gameMemberSubject.onNext(new Event<>(".created", new Member("2022-05-18T18:12:58.114Z", "2022-05-18T18:12:58.114Z", "000", "003", false, "#888888", false)));
        TestModule.gameSubject.onNext(new Event<>(".updated", new Game("2022-05-18T18:12:58.114Z", "2022-05-18T18:12:58.114Z", "000", "000", "000", 3, false , new GameSettings(2, 4, null, true, 0))));
        TestModule.gameMemberSubject.onNext(new Event<>(".updated", new Member("2022-05-18T18:12:58.114Z", "2022-05-18T18:12:58.114Z", "000", "001", true, "#ffffff", false)));
        TestModule.gameMemberSubject.onNext(new Event<>(".updated", new Member("2022-05-18T18:12:58.114Z", "2022-05-18T18:12:58.114Z", "000", "002", true, "#000000", false)));
        TestModule.gameMemberSubject.onNext(new Event<>(".updated", new Member("2022-05-18T18:12:58.114Z", "2022-05-18T18:12:58.114Z", "000", "003", true, "#888888", false)));
        TestModule.mapTemplateSubject.onNext(new Event<>(".updated", new MapTemplate("","","id1","Test","","",0, null, null)));
        write("\t\t\t");
        type(KeyCode.SPACE);
        type(KeyCode.DOWN);
        write("\tHallo Test Test\t");
        type(KeyCode.SPACE);
        TestModule.gameChatSubject.onNext(new Event<>(".created", new MessageDto("2022-05-18T18:12:58.114Z", "2022-05-18T18:12:58.114Z", "003", "A", "Hallo Test Test")));
        write("\t");
        type(KeyCode.SPACE);
        type(KeyCode.SPACE);

        //IngameScreen
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
        write("\t\t\tHallo Test");
        type(KeyCode.SPACE);
        TestModule.gameChatSubject.onNext(new Event<>(".created", new MessageDto("2022-05-18T18:12:58.114Z", "2022-05-18T18:12:58.114Z", "004", "A", "Hallo Test Test")));
        WaitForAsyncUtils.waitForFxEvents();
        WaitForAsyncUtils.waitFor(15, TimeUnit.SECONDS, () -> lookup("#rulesButton") != null);
        clickOn("#rulesButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#settingsButton");
        WaitForAsyncUtils.waitForFxEvents();
        type(KeyCode.LEFT);
        type(KeyCode.SPACE);
        write("\t");
        type(KeyCode.SPACE);
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#leftDiceImageView");
        sleep(1000);

        Pane fieldPane = lookup("#fieldPane").query();
        Scale fieldScale = (Scale) fieldPane.getTransforms().get(0);
        assertNotEquals(fieldScale.getX(), 1);
        assertNotEquals(fieldScale.getY(), 1);

        WaitForAsyncUtils.waitForFxEvents();
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "000", "#ff0000", true, 3, new Resources(0, 0, 0, 0, 0, 0), new RemainingBuildings(5, 4, 15), 0, 0, new ArrayList<>())));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "0", "000", "000", "founding-roll", 3, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove[]{new ExpectedMove("founding-roll", List.of(new String[]{"001", "002", "003"}))}), null)));
        sleep(3000);

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_001:\n" + "roll the dice"));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "001", "#00ff00", true, 4, new Resources(0, 0, 0, 0, 0, 0), new RemainingBuildings(5, 4, 15), 0, 0, new ArrayList<>())));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "1", "000", "001", "founding-roll", 4, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove[]{new ExpectedMove("founding-roll", List.of(new String[]{"002", "003"}))}), null)));
        sleep(1000);

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_002:\n" + "roll the dice"));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "002", "#0000ff", true, 5, new Resources(0, 0, 0, 0, 0, 0), new RemainingBuildings(5, 4, 15), 0, 0, new ArrayList<>())));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "2", "000", "002", "founding-roll", 5, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove[]{new ExpectedMove("founding-roll", List.of(new String[]{"003"}))}), null)));
        sleep(1000);

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "roll the dice"));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "003", "#ffffff", true, 6, new Resources(0, 0, 0, 0, 0, 0), new RemainingBuildings(5, 4, 15), 0, 0, new ArrayList<>())));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "3", "000", "003", "founding-roll", 6, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-1", List.of("000")), new ExpectedMove("founding-road-1", List.of("000")), new ExpectedMove("founding-settlement-1", List.of("002")), new ExpectedMove("founding-road-1", List.of("002")), new ExpectedMove("founding-settlement-1", List.of("003")), new ExpectedMove("founding-road-1", List.of("003")), new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));
        sleep(1000);

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "place settlement"));
        clickOn("#0,0,0,0");
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(0, 0, 0, "1", 0, "settlement", "000", "000")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "4", "000", "000", "founding-settlement-1", 0, "1", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "000", "#ff0000", true, 3, new Resources(0, 0, 0, 0, 0, 0), new RemainingBuildings(4, 4, 15), 1, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-1", List.of("000")), new ExpectedMove("founding-settlement-1", List.of("002")), new ExpectedMove("founding-road-1", List.of("002")), new ExpectedMove("founding-settlement-1", List.of("003")), new ExpectedMove("founding-road-1", List.of("003")), new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "place road"));
        clickOn("#1,0,-1,7");
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(1, 0, -1, "2", 7, "road", "000", "000")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "5", "000", "000", "founding-road-1", 0, "2", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "000", "#ff0000", true, 3, new Resources(0, 0, 0, 0, 0, 0), new RemainingBuildings(4, 4, 14), 1, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-1", List.of("002")), new ExpectedMove("founding-road-1", List.of("002")), new ExpectedMove("founding-settlement-1", List.of("003")), new ExpectedMove("founding-road-1", List.of("003")), new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_002:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(-2, 1, 1, "3", 0, "settlement", "000", "002")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "6", "000", "002", "founding-settlement-1", 0, "3", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "002", "#0000ff", true, 5, new Resources(0, 0, 0, 0, 0, 0), new RemainingBuildings(4, 4, 15), 1, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-1", List.of("002")), new ExpectedMove("founding-settlement-1", List.of("003")), new ExpectedMove("founding-road-1", List.of("003")), new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_002:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(-1, 1, 0, "4", 7, "road", "000", "002")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "7", "000", "002", "founding-road-1", 0, "4", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "002", "#0000ff", true, 5, new Resources(0, 0, 0, 0, 0, 0), new RemainingBuildings(4, 4, 14), 1, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-1", List.of("003")), new ExpectedMove("founding-road-1", List.of("003")), new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(2, -1, -1, "5", 6, "settlement", "000", "003")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "8", "000", "003", "founding-settlement-1", 0, "5", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "003", "#ffffff", true, 6, new Resources(0, 0, 0, 0, 0, 0), new RemainingBuildings(4, 4, 15), 1, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-1", List.of("003")), new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(1, -1, 0, "6", 3, "road", "000", "003")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "9", "000", "003", "founding-road-1", 0, "6", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "003", "#ffffff", true, 6, new Resources(0, 0, 0, 0, 0, 0), new RemainingBuildings(4, 4, 14), 1, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-1", List.of("001")), new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_001:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(0, -1, 1, "7", 0, "settlement", "000", "001")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "10", "000", "001", "founding-settlement-1", 0, "7", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "001", "#00ff00", true, 4, new Resources(0, 0, 0, 0, 0, 0), new RemainingBuildings(4, 4, 15), 1, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-1", List.of("001")), new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_001:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(0, -1, 1, "8", 11, "road", "000", "001")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "11", "000", "001", "founding-road-1", 0, "8", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "001", "#00ff00", true, 4, new Resources(0, 0, 0, 0, 0, 0), new RemainingBuildings(4, 4, 14), 1, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-2", List.of("003")), new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(0, 2, -2, "9", 6, "settlement", "000", "003")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "12", "000", "003", "founding-settlement-2", 0, "9", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "003", "#ffffff", true, 6, new Resources(0, 1, 1, 1, 1, 1), new RemainingBuildings(3, 4, 14), 2, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-2", List.of("003")), new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(-1, 2, -1, "10", 3, "road", "000", "003")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "13", "000", "003", "founding-road-2", 0, "10", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "003", "#ffffff", true, 6, new Resources(0, 1, 1, 1, 1, 1), new RemainingBuildings(3, 4, 13), 2, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-2", List.of("002")), new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_002:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(-1, -1, 2, "11", 0, "settlement", "000", "002")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "14", "000", "002", "founding-settlement-2", 0, "11", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "002", "#0000ff", true, 5, new Resources(0, 1, 1, 1, 1, 1), new RemainingBuildings(3, 4, 14), 2, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-2", List.of("002")), new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_002:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(-1, -1, 2, "12", 11, "road", "000", "002")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "15", "000", "002", "founding-road-2", 0, "12", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "002", "#0000ff", true, 5, new Resources(0, 1, 1, 1, 1, 1), new RemainingBuildings(3, 4, 13), 2, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-2", List.of("000")), new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "place settlement"));
        clickOn("#2,1,-3,6");
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(2, 1, -3, "13", 6, "settlement", "000", "000")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "16", "000", "000", "founding-settlement-2", 0, "13", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "000", "#ff0000", true, 3, new Resources(0, 2, 2, 2, 2, 2), new RemainingBuildings(3, 4, 14), 2, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-2", List.of("000")), new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));
        sleep(1500);

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "place road"));
        clickOn("#1,1,-2,3");
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(1, 1, -2, "14", 3, "road", "000", "000")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "17", "000", "000", "founding-road-2", 0, "14", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "000", "#ff0000", true, 3, new Resources(0, 2, 2, 2, 2, 2), new RemainingBuildings(3, 4, 13), 2, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-settlement-2", List.of("001")), new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_001:\n" + "place settlement"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(3, -1, -2, "15", 6, "settlement", "000", "001")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "18", "000", "001", "founding-settlement-2", 0, "15", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "001", "#00ff00", true, 4, new Resources(0, 1, 1, 1, 1, 1), new RemainingBuildings(3, 4, 14), 2, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("founding-road-2", List.of("001")), new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_001:\n" + "place road"));
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(2, -1, -1, "16", 3, "road", "000", "001")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "19", "000", "001", "founding-road-2", 0, "16", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "001", "#00ff00", true, 4, new Resources(0, 1, 1, 1, 1, 1), new RemainingBuildings(3, 4, 13), 2, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "roll the dice"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "20", "000", "003", "roll", 8, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("build", List.of("003"))), null)));
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "build"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "21", "000", "003", "build", 0, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("roll", List.of("000"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "roll the dice"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "22", "000", "000", "roll", 7, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("drop", List.of("000")), new ExpectedMove("rob", List.of("000")), new ExpectedMove("build", List.of("000"))), null)));
        WaitForAsyncUtils.waitForFxEvents();
        type(KeyCode.UP);
        write("\t");
        type(KeyCode.UP);
        write("\t");
        type(KeyCode.UP);
        write("\t");
        type(KeyCode.UP);
        write("\t");
        type(KeyCode.UP);
        write("\t");
        type(KeyCode.SPACE);
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "22", "000", "000", "drop", 0, null, null, new Resources(null, -1, -1, -1, -1, -1), null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "000", "#ff0000", true, 3, new Resources(0, 1, 1, 1, 1, 1), new RemainingBuildings(3, 4, 13), 2, 0, new ArrayList<>())));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("rob", List.of("000")), new ExpectedMove("build", List.of("000"))), null)));
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "place robber"));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("rob", List.of("000")), new ExpectedMove("build", List.of("000"))), new Point3D(1, 0, -1))));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("build", List.of("000"))), new Point3D(1, 0, -1))));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "23", "000", "000", "rob", 0, null, new RobDto(1, 0, -1, "001"), null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "001", "#00ff00", true, 3, new Resources(0, 1, 1, 0, 1, 1), new RemainingBuildings(3, 4, 13), 2, 0, new ArrayList<>())));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "000", "#ff0000", true, 3, new Resources(0, 1, 1, 2, 1, 1), new RemainingBuildings(3, 4, 13), 2, 0, new ArrayList<>())));
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "build"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "24", "000", "000", "build", 0, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("roll", List.of("002"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_002:\n" + "roll the dice"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "25", "000", "002", "roll", 11, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("build", List.of("002"))), null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "000", "#ff0000", true, 3, new Resources(0, 2, 2, 3, 2, 2), new RemainingBuildings(3, 4, 13), 2, 0, new ArrayList<>())));
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_002:\n" + "build"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "26", "000", "002", "build", 0, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("roll", List.of("003"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "roll the dice"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "27", "000", "003", "roll", 7, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("rob", List.of("003")), new ExpectedMove("build", List.of("003"))), new Point3D(1, 0, -1))));
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "place robber"));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("rob", List.of("003")), new ExpectedMove("build", List.of("003"))), new Point3D(2, 0, -1))));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("build", List.of("003"))), new Point3D(2, 0, -1))));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "28", "000", "003", "rob", 0, null, new RobDto(2, 0, -1, "002"), null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "002", "#0000ff", true, 3, new Resources(0, 1, 1, 0, 1, 1), new RemainingBuildings(3, 4, 13), 2, 0, new ArrayList<>())));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "003", "#ffffff", true, 3, new Resources(0, 1, 1, 2, 1, 1), new RemainingBuildings(3, 4, 13), 2, 0, new ArrayList<>())));
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_003:\n" + "build"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "29", "000", "003", "build", 0, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("roll", List.of("001"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_001:\n" + "roll the dice"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "30", "000", "001", "roll", 11, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("build", List.of("001"))), null)));
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("TestUser_001:\n" + "build"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "31", "000", "001", "build", 0, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("roll", List.of("000"))), null)));

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "roll the dice"));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "32", "000", "000", "roll", 11, null, null, null, null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("build", List.of("000"))), null)));
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#situationLabel", LabeledMatchers.hasText("ME:\n" + "build"));
        clickOn("#tradePane");
        type(KeyCode.UP);
        write("\t\t\t\t\t");
        type(KeyCode.UP);
        write("\t\t\t\t\t\t");
        type(KeyCode.SPACE);
        WaitForAsyncUtils.waitForFxEvents();

        // test trade with players
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "33", "000", "000", "build", 0, null, null, new Resources(null,-1,1,0,0,0), null, null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("offer", List.of("002")), new ExpectedMove("accept", List.of("000")), new ExpectedMove("build", List.of("000"))), null)));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "34", "000", "002", "offer", 0, null, null, new Resources(null,1,-1,0,0,0), "000", null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("accept", List.of("000")), new ExpectedMove("build", List.of("000"))), null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000","002","#0000ff", true,3, new Resources(0,2,0,0,1,1), new RemainingBuildings(3,4,13), 2, 0, new ArrayList<>())));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000","000","#ff0000", true,3, new Resources(0,1,3,3,2,2), new RemainingBuildings(3,4,13), 2, 0, new ArrayList<>())));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "35", "000", "000", "accept", 0, null, null, null, "002", null)));
        TestModule.gameStateSubject.onNext(new Event<>(".updated", new State("2022-05-18T18:12:59.114Z", "000", List.of(new ExpectedMove("build", List.of("000"))), null)));
        WaitForAsyncUtils.waitForFxEvents();

        // test build house
        clickOn("#houseSVG");
        clickOn("#0,0,0,0");
        TestModule.gameBuildingSubject.onNext(new Event<>(".created", new Building(0, 0, 0, "17", 0, "settlement", "000", "000")));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "36", "000", "000", "build", 0, "17", null, null, null, null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "001", "#ff0000", true, 3, new Resources(0, 2, 1, 3, 1, 1), new RemainingBuildings(2, 4, 14), 3, 0, new ArrayList<>())));
        WaitForAsyncUtils.waitForFxEvents();

        // test trade with bank
        clickOn("#tradePane");
        type(KeyCode.UP);
        type(KeyCode.UP);
        type(KeyCode.UP);
        type(KeyCode.UP);
        write("\t\t\t\t\t");
        type(KeyCode.UP);
        write("\t\t\t\t\t\t\t");
        type(KeyCode.SPACE);
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "37", "000", "000", "build", 0, null, null, new Resources(null,-4,1,0,0,0), "bank", null)));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "001", "#ff0000", true, 3, new Resources(0, 1, 5, 4, 4, 4), new RemainingBuildings(2, 4, 14), 3, 0, new ArrayList<>())));
        WaitForAsyncUtils.waitForFxEvents();

        // buy dev card
        clickOn("#hammerPane");
        WaitForAsyncUtils.waitForFxEvents();
        Pane rightPane = lookup("#rightPane").query();
        rightPane.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, true, false, null));
        TestModule.gamePlayerSubject.onNext(new Event<>(".updated", new Player("000", "000", "#ff0000", true, 3, new Resources(0, 1, 1, 2, 1, 0), new RemainingBuildings(2, 4, 14), 3, 0, List.of(new DevelopmentCard("knight", false, true)))));
        TestModule.gameMoveSubject.onNext(new Event<>(".created", new Move("2022-05-18T18:12:59.114Z", "38", "000", "000", "build", 0, null, null, null, null, "new")));
        sleep(4000);
    }
}
