package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.IngameScreenController;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.GameApiService;
import de.uniks.pioneers.services.*;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import java.util.List;

import static de.uniks.pioneers.GameConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DevCardControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock
    TimerService timerService;

    @Mock
    RobberController robberController;

    @Mock
    MapRenderService mapRenderService;

    @Mock
    GameStorage gameStorage;

    @Mock
    GameApiService gameApiService;

    @Mock
    UserService userService;

    @InjectMocks
    IngameService ingameService;

    @Mock
    SpeechService speechService;

    @Spy
    ResourceService resourceService = new ResourceService();

    @Spy
    GameService gameService = new GameService(gameApiService, userService, ingameService, resourceService);

    @Mock
    StylesService stylesService;

    @Mock
    DiceSubcontroller diceSubcontroller;

    @Mock
    RobberService robberService;

    @Mock
    AchievementService achievementService;

    @Spy
    IngameScreenController ingameScreenController = new IngameScreenController(app, diceSubcontroller, ingameService, gameStorage, userService, resourceService, gameService, timerService, mapRenderService, robberService, speechService, stylesService, achievementService);

    @Spy
    IngameDevelopmentCardController ingameDevelopmentCardController = new IngameDevelopmentCardController(app.getStage(), new Pane(), new Pane(), new Pane(), ingameScreenController.hammerImageView, ingameScreenController.leftView, ingameScreenController.rightView, timerService, ingameService, resourceService, gameService, userService, robberController, true);

    @Override
    public void start(Stage stage) {
        Platform.runLater(() -> ingameDevelopmentCardController.newDevCardPlayStage());
    }

    @Test
    void test() {
        UserService userServiceNew = new UserService(null);
        userServiceNew.setCurrentUser(new User("000", "lon", "online", null));
        ingameDevelopmentCardController.userService = userServiceNew;
        gameService.players.put("000", new Player("000", "000", "#ff0000", true, 3, new Resources(0, 1, 1, 2, 1, 0), new RemainingBuildings(2, 4, 14), 3, 0, List.of(new DevelopmentCard(DEV_KNIGHT, false, false))));

        Platform.runLater(() -> {
            ingameDevelopmentCardController.ingameStage = new Stage();
            ingameDevelopmentCardController.show();

            assertTrue(ingameDevelopmentCardController.knightView.isVisible());
            assertFalse(ingameDevelopmentCardController.plentyView.isVisible());
            assertFalse(ingameDevelopmentCardController.monopolyView.isVisible());
            assertFalse(ingameDevelopmentCardController.roadView.isVisible());

            assertFalse(ingameDevelopmentCardController.knightViewMono.isVisible());
            assertTrue(ingameDevelopmentCardController.plentyViewMono.isVisible());
            assertTrue(ingameDevelopmentCardController.monopolyViewMono.isVisible());
            assertTrue(ingameDevelopmentCardController.roadViewMono.isVisible());
        });

        WaitForAsyncUtils.waitForFxEvents();
        ImageView knightView = lookup("#knightView").query();
        clickOn(knightView);
        knightView.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, true, false, null));

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(ingameDevelopmentCardController.knightRect.isVisible());
        assertFalse(ingameDevelopmentCardController.plentyRect.isVisible());
        assertFalse(ingameDevelopmentCardController.monopolyRect.isVisible());
        assertFalse(ingameDevelopmentCardController.roadRect.isVisible());
    }
}
