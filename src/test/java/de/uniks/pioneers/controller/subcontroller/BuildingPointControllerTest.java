package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.RemainingBuildings;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;

import static de.uniks.pioneers.GameConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuildingPointControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock
    HexTile tile;

    @Mock
    Circle view;

    @Mock
    IngameService ingameService;

    @Mock
    GameService gameService;

    @Mock
    Pane fieldPane;

    @Mock
    GameStorage gameStorage;

    @Mock
    UserService userService;

    @InjectMocks
    BuildingPointController buildingPointController;

    @Override
    public void start(Stage stage) {
        app.start(stage);
        //app.show(buildingPointController);
    }

    @Test
    void initAndCheckIfMouseInsideView() {
        buildingPointController.eventView = new Circle();
        buildingPointController.eventView.setLayoutX(0);
        buildingPointController.eventView.setLayoutY(0);
        buildingPointController.eventView.setRadius(5);
        buildingPointController.eventView.setOpacity(0);
        buildingPointController.view = new Circle();

        Platform.runLater(buildingPointController::init);
        assertTrue(buildingPointController.view.isVisible());
        assertEquals(buildingPointController.view.getFill(), Color.valueOf("0x000000ff"));
    }

    @Test
    void checkPosition() {
        buildingPointController.setAction("build");
        gameStorage.selectedBuilding = SETTLEMENT;
        gameStorage.remainingBuildings = FXCollections.observableHashMap();
        gameStorage.remainingBuildings.put(ROAD, 15);
        gameStorage.remainingBuildings.put(SETTLEMENT, 5);
        gameStorage.remainingBuildings.put(CITY, 4);
        when(gameService.checkResourcesSettlement()).thenReturn(true);

        assertTrue(buildingPointController.checkPosition(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, true, false, null)));
    }

    @Test
    void placeBuilding() {
        when(ingameService.getPlayer("000", "000")).thenReturn(Observable.just(new Player("000","000","#ff0000", true,1, new Resources(0,0,0,0,0,0),new RemainingBuildings(1,1,1), 0, 0, new ArrayList<>())));
        buildingPointController.fieldPane = new Pane();
        buildingPointController.view = new Circle();

        buildingPointController.placeBuilding(new Building(0,0,0, "000", 0, SETTLEMENT, "000", "000"));
        assertFalse(buildingPointController.view.isVisible());
        assertNotNull(buildingPointController.displayedBuilding);
    }

    @Test
    void checkTradeOptions() {
        buildingPointController.checkTradeOptions();
    }
}
