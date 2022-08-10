package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.MapDetailsController;
import de.uniks.pioneers.controller.subcontroller.MapListController;
import de.uniks.pioneers.controller.subcontroller.ZoomableScrollPane;
import de.uniks.pioneers.model.HarborTemplate;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.model.TileTemplate;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.*;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.TextMatchers;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MapBrowserControllerTest extends ApplicationTest {
    final List<TileTemplate> tiles = new ArrayList<>();
    final List<HarborTemplate> harbors = new ArrayList<>();

    final MapTemplate mapDummy1 = new MapTemplate("today","2022-07-19T14:47:42.402Z","map456","map2","", null,"1234",7,tiles, harbors);
    final MapTemplate mapDummy2 = new MapTemplate("yesterday", "2022-07-24T14:47:42.402Z", "map123", "map", "", null, "1234", 3, tiles, harbors);

    @Spy
    App app = new App(null);

    @Mock(name = "mapListControllerProvider")
    Provider<MapListController> mapListControllerProvider;

    @Mock(name = "mapDetailsControllerProvider")
    Provider<MapDetailsController> mapDetailsControllerProvider;

    @Mock(name = "boardControllerProvider")
    Provider<BoardController> boardControllerProvider;

    @Mock(name = "mapRenderServiceProvider")
    Provider<MapRenderService> mapRenderServiceProvider;

    @Mock(name = "zoomableScrollPaneProvider")
    Provider<ZoomableScrollPane> zoomableScrollPaneProvider;

    @Mock MapRenderService mapRenderService;
    @Mock BoardController boardController;
    @Mock ZoomableScrollPane zoomableScrollPane;

    @Mock
    MapBrowserService mapBrowserService;

    @Mock
    StylesService stylesService;

    @Mock
    UserService userService;

    @Mock
    MapService mapService;

    @InjectMocks
    MapDetailsController mapDetailsController;

    @InjectMocks
    MapListController mapListController;

    @InjectMocks MapBrowserController mapBrowserController;

    @Override
    public void start(Stage stage){
        when(userService.getCurrentUser()).thenReturn(new User("1234", "me", "online", null));
        when(mapListControllerProvider.get()).thenReturn(mapListController);
        when(mapDetailsControllerProvider.get()).thenReturn(mapDetailsController);
        when(mapRenderServiceProvider.get()).thenReturn(mapRenderService);
        when(boardControllerProvider.get()).thenReturn(boardController);
        when(zoomableScrollPaneProvider.get()).thenReturn(zoomableScrollPane);
        doNothing().when(boardController).buildMapPreview(any(), any());
        when(userService.getUserById("1234")).thenReturn(Observable.just(new User("1234", "me", "online", null)));
        doNothing().when(mapService).setCurrentMap(any());
        when(mapBrowserService.getMap("map456")).thenReturn(mapDummy1);

        ArrayList<MapTemplate> returnValue = new ArrayList<>();
        returnValue.add(mapDummy1);
        returnValue.add(mapDummy2);

        List<MapTemplate> maps = new ArrayList<>();
        when(mapBrowserService.getUpdateMaps()).thenReturn(FXCollections.observableArrayList(maps));

        when(mapBrowserService.getMaps()).thenReturn(FXCollections.observableArrayList(returnValue));

        app.start(stage);
        app.show(mapBrowserController);
        verify(stylesService, atLeastOnce()).setStyleSheets(any(), anyString(), anyString());
    }

    @Test
    public void updateMapDetails() {
        when(mapBrowserService.getMap("map123")).thenReturn(mapDummy2);

        // select map123
        type(KeyCode.DOWN);

        // check label contents
        FxAssert.verifyThat("#mapNameOutputText", TextMatchers.hasText("map"));
        FxAssert.verifyThat("#createdByOutputText", TextMatchers.hasText("me"));
        FxAssert.verifyThat("#lastUpdatedOutputText", TextMatchers.hasText("2022-07-24, 14:47"));
        FxAssert.verifyThat("#votesOutputText", TextMatchers.hasText("3"));
    }

    @Test
    public void deleteMap() {
        when(mapBrowserService.deleteMap("map456")).thenReturn(Observable.just(mapDummy1));

        write("\t");
        type(KeyCode.SPACE);

        verify(mapBrowserService).deleteMap("map456");
    }
}
