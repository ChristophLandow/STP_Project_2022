package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.MapEditorController;
import de.uniks.pioneers.controller.subcontroller.EditTile;
import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.dto.CreateMapTemplateDto;
import de.uniks.pioneers.dto.UpdateMapTemplateDto;
import de.uniks.pioneers.model.HarborTemplate;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.model.TileTemplate;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.MapApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapServiceTest {

    @Mock
    MapApiService mapApiService;

    @Mock
    UserService userService;

    @Mock
    MapEditorController mapEditorController;

    @InjectMocks
    MapService mapService;

    @Test
    void updateOrCreateMap() {
        /*//create and set test elements
        List<EditTile> editTiles = new ArrayList<>();
        List<HarborTemplate> harbors = new ArrayList<>();
        List<TileTemplate> tiles = new ArrayList<>();
        harbors.add(new HarborTemplate(1, 1, 1, "ore", 1));
        tiles.add(new TileTemplate(1, 1, 1, "desert", 2));
        Polygon view1 = new Polygon();
        javafx.scene.image.ImageView view2 = new ImageView();
        editTiles.add(new EditTile(new HexTile(1, 1, 1, 1, false), view1, view2, null));
        MapTemplate map = new MapTemplate("1", "1", "123", "1", "1", null, "123", 0, tiles, harbors);

        // prepare methods for the test
        User currentUser = new User("123", "Jeff", "online", null);
        userService.setCurrentUser(currentUser);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        //when(mapService.createMap("mapo", null, "cool map", any(), any())).thenReturn(Observable.just(map));
        //when(mapService.createMap(anyString(), any(), anyString(), any(), any())).thenReturn(Observable.just(map));







        //change the owner of the map
        map = new MapTemplate("1", "1", "123", "1", "1", null, "456", 0, tiles, harbors);
        mapService.setCurrentMap(map);

        when(mapService.saveMap(any(), any())).thenReturn(Observable.just(map));

        // call function
        mapService.updateOrCreateMap(editTiles, "mapo", "cool map");

        // check if the map will be updated, because it is your map
        //verify(mapApiService).updateMap("123", new UpdateMapTemplateDto("1", null, "1", null, null ));

        //change the owner of the map
        map = new MapTemplate("1", "1", "123", "1", "1", null, "123", 0, tiles, harbors);
        mapService.setCurrentMap(map);

        // call the function
        mapService.updateOrCreateMap(editTiles, "1", "1");

        // check if the map is updated because you are the owner
        verify(mapApiService).updateMap("123", new UpdateMapTemplateDto("1", null, "1", tiles, null));*/




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