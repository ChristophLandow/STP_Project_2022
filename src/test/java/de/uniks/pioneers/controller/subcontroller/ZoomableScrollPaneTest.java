package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.DaggerTestComponent;
import de.uniks.pioneers.MainComponent;
import de.uniks.pioneers.TestModule;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.GameSettings;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.model.Member;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class ZoomableScrollPaneTest extends ApplicationTest {
    final App app = new App(null);

    @Override
    public void start(Stage stage) {
        MainComponent testComponent = DaggerTestComponent.builder().mainApp(app).build();
        app.start(stage);
        app.show(testComponent.loginController());
    }

    @Test
    public void
    test() {
        //LoginScreen
        WaitForAsyncUtils.waitForFxEvents();
        write("TestUser\t");
        write("12345678\t\t\t");
        type(KeyCode.ENTER);

        //SignUpScreen
        WaitForAsyncUtils.waitForFxEvents();
        write("\t\t12345678\t\t\t");
        type(KeyCode.ENTER);

        //Dialog
        WaitForAsyncUtils.waitForFxEvents();
        type(KeyCode.ENTER);

        //LoginScreen
        WaitForAsyncUtils.waitForFxEvents();
        write("\t12345678\t");
        type(KeyCode.ENTER);
        write("\t");
        type(KeyCode.ENTER);

        //LobbyScreen
        WaitForAsyncUtils.waitForFxEvents();
        write("\t\t");
        type(KeyCode.ENTER);
        clickOn("#gameNameTextField");
        write("TestGame\t");
        write("12345678\t\t");
        type(KeyCode.ENTER);

        //NewGameLobbyScreen
        WaitForAsyncUtils.waitForFxEvents();
        TestModule.gameMemberSubject.onNext(new Event<>(".created", new Member("2022-05-18T18:12:58.114Z", "2022-05-18T18:12:58.114Z", "000", "001", true, "#ffffff", false)));
        TestModule.gameSubject.onNext(new Event<>(".updated", new Game("2022-05-18T18:12:58.114Z", "2022-05-18T18:12:58.114Z", "000", "000", "000", 1, false , new GameSettings(2, 4, null, true, 0))));
        TestModule.mapTemplateSubject.onNext(new Event<>(".updated", new MapTemplate("","","id1","Test","","",0, null, null)));
        write("\t\t\t");
        type(KeyCode.SPACE);
        type(KeyCode.DOWN);
        sleep(1000);
        write("\t\t");
        type(KeyCode.ENTER);
        TestModule.gameChatSubject.onNext(new Event<>(".created", new MessageDto("2022-05-18T18:12:58.114Z", "2022-05-18T18:12:58.114Z", "003", "A", "Hallo Test Test")));
        write("\t");
        type(KeyCode.ENTER);
        type(KeyCode.ENTER);

        //IngameScreen
        sleep(300);
        Pane fieldPane = lookup("#fieldPane").query();
        double fieldPaneWidth = fieldPane.getPrefWidth();
        double fieldPaneHeight = fieldPane.getPrefHeight();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);

        //Check if centerMap() from ZoomableScrollPane was called
        assertNotEquals(fieldPaneWidth, fieldPane.getPrefWidth());
        assertNotEquals(fieldPaneHeight, fieldPane.getPrefHeight());

        Scale fieldScale = (Scale) fieldPane.getTransforms().get(0);
        assertNotEquals(fieldScale.getX(), 1);
        assertNotEquals(fieldScale.getY(), 1);
    }

}