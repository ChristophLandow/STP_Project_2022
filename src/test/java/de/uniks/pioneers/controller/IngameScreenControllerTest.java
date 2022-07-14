package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.PopUpController.TradeOfferPopUpController;
import de.uniks.pioneers.controller.PopUpController.TradePopUpController;
import de.uniks.pioneers.controller.subcontroller.*;
import de.uniks.pioneers.model.ExpectedMove;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.GameApiService;
import de.uniks.pioneers.services.*;
import javafx.beans.property.SimpleBooleanProperty;
import static org.mockito.Mockito.verify;
import javafx.scene.input.KeyCode;
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

    @Spy
    GameService gameService = new GameService(gameApiService, userService, ingameService);

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

    @Mock
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
    }
}
