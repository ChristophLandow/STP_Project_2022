package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.services.*;
import de.uniks.pioneers.model.Harbor;
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
import java.util.List;


import static de.uniks.pioneers.GameConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    ResourceService resourceService;

    @Mock
    GameStorage gameStorage;

    @Mock
    IngameSelectController ingameSelectController;

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
        buildingPointController.setAction(BUILD);
        gameStorage.selectedBuilding = SETTLEMENT;
        gameStorage.remainingBuildings = FXCollections.observableHashMap();
        gameStorage.remainingBuildings.put(ROAD, 15);
        gameStorage.remainingBuildings.put(SETTLEMENT, 5);
        gameStorage.remainingBuildings.put(CITY, 4);
        when(resourceService.checkResourcesSettlement()).thenReturn(true);

        assertFalse(buildingPointController.checkPosition(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, true, false, null)));
        verify(ingameSelectController, atLeastOnce()).resetSelect();
    }

    @Test
    void placeBuilding() {
        when(ingameService.getPlayer("000", "000")).thenReturn(Observable.just(new Player("000","000","#ff0000", true,1, new Resources(0,0,0,0,0,0),new RemainingBuildings(1,1,1), 0, 0, new ArrayList<>())));
        buildingPointController.fieldPane = new Pane();
        buildingPointController.view = new Circle();

        buildingPointController.placeBuilding(new Building(0,0,0, "000", 0, SETTLEMENT, "000", "000"));
        assertFalse(buildingPointController.view.isVisible());
        assertNotNull(buildingPointController.displayedBuilding);
        //verify(gameService, atLeastOnce()).getUsers();
    }

    @Test
    void checkTradeOptions() {
        Harbor testHarbor1 = new Harbor(-1,-1,2,"grain", 7);
        Harbor testHarbor2 = new Harbor(0,-2,2,null, 5);
        Harbor testHarbor3 = new Harbor(1,-2,1,"lumber", 5);
        Harbor testHarbor4 = new Harbor(2,-1,-1,null, 3);
        Harbor testHarbor5 = new Harbor(2,0,-2,"brick", 1);
        Harbor testHarbor6 = new Harbor(1,1,-2,null, 1);
        Harbor testHarbor7 = new Harbor(-1,2,-1,"ore", 11);
        Harbor testHarbor8 = new Harbor(-2,2,0,null, 9);
        Harbor testHarbor9 = new Harbor(-2,1,1,"wool", 9);
        Harbor testHarbor10 = new Harbor(-1,-1,2,"grain", 7);
        Harbor testHarbor11 = new Harbor(0,-2,2,null, 5);
        Harbor testHarbor12 = new Harbor(1,-2,1,"lumber", 5);
        Harbor testHarbor13 = new Harbor(2,-1,-1,null, 3);
        Harbor testHarbor14 = new Harbor(2,0,-2,"brick", 1);
        Harbor testHarbor15 = new Harbor(1,1,-2,null, 1);
        Harbor testHarbor16 = new Harbor(-1,2,-1,"ore", 11);
        Harbor testHarbor17 = new Harbor(-2,2,0,null, 9);
        Harbor testHarbor18 = new Harbor(-2,1,1,"wool", 9);

        List<Harbor> harborList = new ArrayList<>();
        //harborList.add(new Harbor(-1,-1,2,"grain", 7));
        //when(gameStorage.getHarbors()).thenReturn()
        //buildingPointController.uploadCoords = [];
        buildingPointController.checkTradeOptions();
    }
}
