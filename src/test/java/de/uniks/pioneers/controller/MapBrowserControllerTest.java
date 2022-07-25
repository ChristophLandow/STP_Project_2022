package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.MapDetailsController;
import de.uniks.pioneers.controller.subcontroller.MapListController;
import de.uniks.pioneers.model.HarborTemplate;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.model.TileTemplate;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.MapBrowserService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.StylesService;
import de.uniks.pioneers.services.UserService;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MapBrowserControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @Mock(name = "ingameScreenControllerProvider")
    Provider<IngameScreenController> ingameScreenControllerProvider;

    @Mock(name = "mapListControllerProvider")
    Provider<MapListController> mapListControllerProvider;

    @Mock(name = "mapDetailsControllerProvider")
    Provider<MapDetailsController> mapDetailsControllerProvider;

    @Mock
    PrefService prefService;

    @Mock
    MapBrowserService mapBrowserService;

    @Mock
    StylesService stylesService;

    @Mock
    UserService userService;

    @InjectMocks
    MapDetailsController mapDetailsController;

    @InjectMocks
    IngameScreenController ingameScreenController;

    @InjectMocks
    MapListController mapListController;

    @InjectMocks MapBrowserController mapBrowserController;

    @Override
    public void start(Stage stage){
        when(mapListControllerProvider.get()).thenReturn(mapListController);
        when(mapDetailsControllerProvider.get()).thenReturn(mapDetailsController);

        ArrayList<MapTemplate> returnValue = new ArrayList<>();
        returnValue.add(new MapTemplate("today","2022-07-19T14:47:42.402Z","map456","map2",null,"1234",7,null, null));
        returnValue.add(new MapTemplate("yesterday", "today", "map123", "map", null, "1234", 3, null, null));

        List<MapTemplate> maps = new ArrayList<>();
        when(mapBrowserService.getUpdateMaps()).thenReturn(FXCollections.observableArrayList(maps));

        when(mapBrowserService.getMaps()).thenReturn(FXCollections.observableArrayList(returnValue));

        app.start(stage);
        app.show(mapBrowserController);
    }

    @Test
    public void updateMapDetails() {
        List<TileTemplate> tiles = new ArrayList<>();
        List<HarborTemplate> harbors = new ArrayList<>();

        when(userService.getUserById("1234")).thenReturn(Observable.just(new User("1234", "me", "online", null)));
        when(mapBrowserService.getMap("map456")).thenReturn(Observable.just(new MapTemplate("today","2022-07-19T14:47:42.402Z","map456","map2",null,"1234",0,tiles, harbors)));
        when(mapBrowserService.getMap("map123")).thenReturn(Observable.just(new MapTemplate("yesterday", "2022-07-24T14:47:42.402Z", "map123", "map", null, "1234", 3, tiles, harbors)));


        // select map123
        type(KeyCode.DOWN);
        type(KeyCode.DOWN);

        // check label contents
        FxAssert.verifyThat("#mapNameOutputText", TextMatchers.hasText("map"));
        FxAssert.verifyThat("#createdByOutputText", TextMatchers.hasText("me"));
        FxAssert.verifyThat("#lastUpdatedOutputText", TextMatchers.hasText("2022-07-24, 14:47"));
        FxAssert.verifyThat("#votesOutputText", TextMatchers.hasText("3"));
    }

    @Test
    void test() {

    }
}
