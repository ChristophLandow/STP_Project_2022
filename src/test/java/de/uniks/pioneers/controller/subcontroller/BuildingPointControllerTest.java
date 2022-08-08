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
import javafx.scene.shape.SVGPath;
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
    void testVisible(){
        buildingPointController.displayedBuilding = new SVGPath();
        buildingPointController.view = new Circle();
        buildingPointController.setVisible(true);
        buildingPointController.displayedBuilding = null;
        buildingPointController.setVisible(false);
    }

    @Test
    void testCheckSettlementSpot(){
    }

    @Test
    void checkTradeOptions() {
        List<Harbor> harborList = new ArrayList<>();
        harborList.add(new Harbor(-1,-1,2,"grain", 7));
        harborList.add(new Harbor(0,-2,2,null, 5));
        harborList.add(new Harbor(1,-2,1,"lumber", 5));
        harborList.add(new Harbor(2,-1,-1,null, 3));
        harborList.add(new Harbor(2,0,-2,"brick", 1));
        harborList.add(new Harbor(1,1,-2,null, 1));
        harborList.add(new Harbor(-2,1,1,"ore", 11));
        harborList.add(new Harbor(-1,2,-1,null, 9));
        harborList.add(new Harbor(-2,1,1,"wool", 9));
        harborList.add(new Harbor(-1,2,-1,"grain", 7));
        harborList.add(new Harbor(0,-2,2,null, 5));
        harborList.add(new Harbor(2,-1,-1,"lumber", 5));
        harborList.add(new Harbor(1,-2,1,null, 3));
        harborList.add(new Harbor(2,0,-2,"brick", 1));
        harborList.add(new Harbor(1,-2,1,null, 1));
        harborList.add(new Harbor(-1,2,-1,"ore", 11));
        harborList.add(new Harbor(-2,2,0,null, 9));
        harborList.add(new Harbor(-2,1,1,"wool", 9));
        when(gameStorage.getHarbors()).thenReturn(harborList);
        //side 1
        buildingPointController.uploadCoords = new int[]{2, 0, -2, 0};
        buildingPointController.checkTradeOptions();
        buildingPointController.uploadCoords = new int[]{2, -2, 0, 6};
        buildingPointController.checkTradeOptions();
        //side 3
        buildingPointController.uploadCoords = new int[]{2, -2, 0, 6};
        buildingPointController.checkTradeOptions();
        buildingPointController.uploadCoords = new int[]{2, -2, 0, 0};
        buildingPointController.checkTradeOptions();
        //side 5
        buildingPointController.uploadCoords = new int[]{2, -2, 0, 0};
        buildingPointController.checkTradeOptions();
        buildingPointController.uploadCoords = new int[]{0, -2, 2, 6};
        buildingPointController.checkTradeOptions();
        // side 7
        buildingPointController.uploadCoords = new int[]{-1, -1, 2, 6};
        buildingPointController.checkTradeOptions();
        buildingPointController.uploadCoords = new int[]{-2, 2, 0, 0};
        buildingPointController.checkTradeOptions();
        // side 9
        buildingPointController.uploadCoords = new int[]{-2, 2, 0, 0};
        buildingPointController.checkTradeOptions();
        buildingPointController.uploadCoords = new int[]{-2, 2, 0, 6};
        buildingPointController.checkTradeOptions();
        // side 11
        buildingPointController.uploadCoords = new int[]{2, -2, 0, 6};
        buildingPointController.checkTradeOptions();
        buildingPointController.uploadCoords = new int[]{-1, 2, -1, 0};
        buildingPointController.checkTradeOptions();
    }
}
