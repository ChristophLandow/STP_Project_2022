package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.MapEditorController;
import de.uniks.pioneers.controller.subcontroller.EditTile;
import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.dto.CreateMapTemplateDto;
import de.uniks.pioneers.model.HarborTemplate;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.model.TileTemplate;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.MapApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MapServiceTest {

    @Mock
    MapApiService mapApiService;

    @Mock
    UserService userService;

    @Mock
    MapEditorController mapEditorController;

    @Mock
    MapBrowserService mapBrowserService;


    @InjectMocks
    MapService mapService;



    static void initJfxRuntime() {
        Platform.startup(() -> {});
    }

    @Test
    void updateOrCreateMap() {
        // needed method to initialize the toolkid because of IllegalStateException
        try {
            initJfxRuntime();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        //create and set test elements
        List<EditTile> editTiles = new ArrayList<>();
        HexTile hexTile = new HexTile(1, 1, 1, 1, false);
        hexTile.type = "desert";
        hexTile.number = 1;
        Polygon view1 = new Polygon();
        ImageView view2 = new ImageView();
        EditTile editTile = new EditTile(hexTile, view1, view2, null);
        editTile.currentHarborType = "harbour_ore";
        editTile.currentHarborSide = 1;
        editTiles.add(editTile);

        //objects for checking
        List<HarborTemplate> harbors = new ArrayList<>();
        List<TileTemplate> tiles = new ArrayList<>();
        harbors.add(new HarborTemplate(1, 1, 1, "ore", 1));
        tiles.add(new TileTemplate(1, 1, 1, "desert", 1));
        MapTemplate map = new MapTemplate("1", "1", "1", "testMap", "map for testing", null, "1", 0,  tiles, harbors);
        User user = new User("1", "1", "online", null);

        //prepare needed methods
        when(mapService.createMap("testMap", null, "map for testing", tiles, harbors ))
                .thenReturn(Observable.just(map));
        doNothing().when(mapBrowserService).addOwnMap(map);

        //call the function for creating, because the map is null
        mapService.updateOrCreateMap(editTiles, "testMap", "map for testing");

        //check if the map is correct
        verify(mapApiService)
                .createMap(new CreateMapTemplateDto("testMap", null, "map for testing", tiles, harbors));

        //set current map
        mapService.setCurrentMap(map);

        //needed stubbings
        when(userService.getCurrentUser()).thenReturn(user);
        when(mapService.saveMap(any(), any())).thenReturn(Observable.just(map));
        doNothing().when(mapBrowserService).updateOwnMap(map);

        //call the function
        mapService.updateOrCreateMap(editTiles, "no new name", "no new description");

        //check if the map is updated
        verify(mapApiService).updateMap(any(), any());

        //set current map to map from another user
        MapTemplate map2 = new MapTemplate("1", "1", "1", "testMap", "map for testing", null, "2", 0,  tiles, harbors);
        mapService.setCurrentMap(map2);

        //call the function
        mapService.updateOrCreateMap(editTiles, "testMap", "map for testing");

        //check if a new map is created
        verify(mapApiService, atLeast(2)).createMap(new CreateMapTemplateDto("testMap", null, "map for testing", tiles, harbors));
    }

    @Test
    void loadMap() {
        //create elements
        List<HarborTemplate> harbors = new ArrayList<>();
        List<TileTemplate> tiles = new ArrayList<>();
        harbors.add(new HarborTemplate(1, 1, 1, "ore", 1));
        tiles.add(new TileTemplate(1, 1, 1, "desert", 12));
        MapTemplate map = new MapTemplate("1", "1", "1", "1", "1", null, "1", 0, tiles, harbors);
        mapService.setMapEditorController(mapEditorController);

        // prepare functions
        when(mapEditorController.setView(20.0)).thenReturn(new Polygon());
        when(mapEditorController.setNumberView(any(), anyDouble())).thenReturn(new ImageView());

        // call the function
        mapService.setCurrentMap(map);
        List<EditTile> mapEditTiles = mapService.loadMap(5);

        // check for correct transformation to the editTiles
        assertEquals(1, mapEditTiles.size());
        assertEquals(2, mapService.getCurrentMapSize());
        assertEquals("desert", mapEditTiles.get(0).hexTile.type);
        assertEquals(1, mapEditTiles.get(0).hexTile.q);
        assertEquals(1, mapEditTiles.get(0).hexTile.r);
        assertEquals(1, mapEditTiles.get(0).hexTile.s);
        assertEquals("harbour_ore", mapEditTiles.get(0).currentHarborType);
        assertEquals(12, mapEditTiles.get(0).hexTile.number);
    }
}