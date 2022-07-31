package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.PopUpController.TradeOfferPopUpController;
import de.uniks.pioneers.controller.PopUpController.TradePopUpController;
import de.uniks.pioneers.controller.subcontroller.*;
import de.uniks.pioneers.model.ExpectedMove;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.GameApiService;
import de.uniks.pioneers.services.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngameScreenControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock
    MapRenderService mapRenderService;

    @Mock(name = "zoomableScrollPaneProvider")
    Provider<ZoomableScrollPane> zoomableScrollPaneProvider;

    @Mock(name = "tradePopUpControllerProvider")
    Provider<TradePopUpController> tradePopUpControllerProvider;

    @Mock(name = "settingsScreenControllerProvider")
    Provider<SettingsScreenController> settingsScreenControllerProvider;

    @Mock(name = "rulesScreenControllerProvider")
    Provider<RulesScreenController> rulesScreenControllerProvider;

    @Mock
    TradePopUpController tradePopUpController;

    @Mock
    GameStorage gameStorage;

    @Mock
    RulesScreenController rulesController;

    @Mock
    SettingsScreenController settingsScreenController;

    @Mock
    ZoomableScrollPane zoomableScrollPane;

    @Mock
    GameApiService gameApiService;

    @Mock
    UserService userService;

    @Mock
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
    GameChatController gameChatController;

    @Mock
    LeaveGameController leaveGameController;

    @Mock
    TimerService timerService;

    @Mock
    PrefService prefService;

    @Mock
    TradeOfferPopUpController tradeOfferPopUpController;

    @Spy
    IngameSelectController ingameSelectController;

    @Mock
    IngameStateController ingameStateController;

    @InjectMocks
    IngameScreenController ingameScreenController;

    @Override
    public void start(Stage stage) {
        when(zoomableScrollPaneProvider.get()).thenReturn(zoomableScrollPane);
        when(mapRenderService.isFinishedLoading()).thenReturn(new SimpleBooleanProperty());
        when(settingsScreenControllerProvider.get()).thenReturn(settingsScreenController);
        when(rulesScreenControllerProvider.get()).thenReturn(rulesController);
        when(ingameService.getExpectedMove()).thenReturn(new ExpectedMove("build", List.of("000", "001")));
        when(tradePopUpControllerProvider.get()).thenReturn(tradePopUpController);

        ingameService.game = new SimpleObjectProperty<>();
        ingameService.game.set(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","001","TestGameA","001",1,false, null));
        gameService.me = "000";
        userService.setCurrentUser(new User("000", "test", "online", ""));

        app.start(stage);
        app.show(ingameScreenController);
    }

    @Test
    void test() {
        type(KeyCode.SPACE);
        verify(leaveGameController).leave();

        write("\t");
        type(KeyCode.SPACE);
        verify(rulesController).init();

        write("\t");
        type(KeyCode.SPACE);
        verify(settingsScreenController).init();

        Pane roadFrame = lookup("#roadFrame").query();
        Pane settlementFrame = lookup("#settlementFrame").query();
        Pane cityFrame = lookup("#cityFrame").query();
        Pane tradePane = lookup("#tradePane").query();
        Pane hammerPane = lookup("#hammerPane").query();
        Pane rightPane = lookup("#rightPane").query();

        gameStorage.selectedBuilding = "";
        ingameSelectController = new IngameSelectController();
        ingameSelectController.init(gameStorage, ingameService, roadFrame, settlementFrame, cityFrame);
        ingameScreenController.ingameSelectController = ingameSelectController;
        ingameScreenController.ingameDevelopmentCardController = new IngameDevelopmentCardController(ingameScreenController.hammerPane, ingameScreenController.leftPane, ingameScreenController.rightPane, ingameScreenController.hammerImageView, ingameScreenController.leftView, ingameScreenController.rightView, ingameService, new ResourceService());

        roadFrame.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, true, false, null));
        assertEquals(roadFrame.getBackground(), Background.fill(Color.rgb(144,238,144)));
        assertEquals(settlementFrame.getBackground(), Background.fill(Color.rgb(250,250,250)));
        assertEquals(cityFrame.getBackground(), Background.fill(Color.rgb(250,250,250)));

        settlementFrame.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, true, false, null));
        assertEquals(roadFrame.getBackground(), Background.fill(Color.rgb(250,250,250)));
        assertEquals(settlementFrame.getBackground(), Background.fill(Color.rgb(144,238,144)));
        assertEquals(cityFrame.getBackground(), Background.fill(Color.rgb(250,250,250)));

        cityFrame.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, true, false, null));
        assertEquals(roadFrame.getBackground(), Background.fill(Color.rgb(250,250,250)));
        assertEquals(settlementFrame.getBackground(), Background.fill(Color.rgb(250,250,250)));
        assertEquals(cityFrame.getBackground(), Background.fill(Color.rgb(144,238,144)));

        tradePane.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, true, false, null));
        verify(tradePopUpController).show();
        verify(stylesService).setStyleSheets(any(), anyString(), anyString());
        verify(timerService).setTimeLabel(any());

        hammerPane.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, true, false, null));
        assertEquals(hammerPane.getStyle(), "-fx-border-width: 3; -fx-border-color: lightgreen");
        assertTrue(ingameScreenController.ingameDevelopmentCardController.leftPane.isVisible());
        assertTrue(ingameScreenController.ingameDevelopmentCardController.rightPane.isVisible());
        rightPane.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, true, false, null));
        assertEquals(hammerPane.getStyle(), "-fx-border-width: 1; -fx-border-color: black");
        assertFalse(ingameScreenController.ingameDevelopmentCardController.leftPane.isVisible());
        assertFalse(ingameScreenController.ingameDevelopmentCardController.rightPane.isVisible());
    }
}
