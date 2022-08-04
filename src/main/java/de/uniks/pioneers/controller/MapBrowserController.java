package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.MapDetailsController;
import de.uniks.pioneers.controller.subcontroller.MapListController;
import de.uniks.pioneers.services.MapService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.StylesService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class MapBrowserController implements Controller {
    @FXML public Pane mapPreviewPane;
    @FXML public ImageView creatorImageView;
    @FXML public Text lastUpdatedOutputText;
    @FXML public Text votesOutputText;
    @FXML public Text tilesOutputText;
    @FXML public Text harborsOutputText;
    @FXML public Text mapNameOutputText;
    @FXML public Text createdByOutputText;
    @FXML ScrollPane MapListScrollPane;
    @FXML ListView<HBox> mapListView;

    @Inject Provider<LobbyScreenController> lobbyScreenControllerProvider;
    @Inject Provider<MapListController> mapListControllerProvider;
    @Inject Provider<MapDetailsController> mapDetailsControllerProvider;
    @Inject Provider<MapEditorController> mapEditorControllerProvider;
    @Inject PrefService prefService;

    @FXML private final App app;
    private LobbyScreenController lobbyScreenController;
    private final StylesService stylesService;
    private MapListController mapListController;

    private final MapService mapService;
    @FXML
    Button mapBrowserCreateButton;
    @FXML
    Button editMapButton;

    @Inject
    public MapBrowserController(App app, StylesService stylesService, MapService mapService){
        this.app = app;
        this.stylesService = stylesService;
        this.mapService = mapService;
    }

    @Override
    public void init() {
        String styleLocal = "/de/uniks/pioneers/styles/MapBrowser.css";
        String styleLocalDark = "/de/uniks/pioneers/styles/DarkMode_MapBrowser.css";
        stylesService.setStyleSheets(app.getStage().getScene().getStylesheets(), styleLocal, styleLocalDark);

        // init map list
        MapListScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mapListController = mapListControllerProvider.get();
        mapListController.setMapList(mapListView);
        mapListController.setMapListScrollPane(MapListScrollPane);
        mapListController.init();
        mapListController.render();

        // init map details
        MapDetailsController mapDetailsController = mapDetailsControllerProvider.get();
        mapDetailsController.setLastUpdatedOutputText(lastUpdatedOutputText)
                .setVotesOutputText(votesOutputText)
                .setTilesOutputText(tilesOutputText)
                .setHarborsOutputText(harborsOutputText)
                .setMapNameOutputText(mapNameOutputText)
                .setCreatedByOutputText(createdByOutputText)
                .setCreatorImageView(creatorImageView);
    }

    @Override
    public void stop() {
        mapListController.stop();
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/MapBrowserScreen.fxml"));
        loader.setControllerFactory(c->this);
        final Parent mapBrowserView;
        try {
            mapBrowserView =  loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return mapBrowserView;
    }

    public void editMap() {
        MapEditorController mapEditorController = mapEditorControllerProvider.get();
        this.app.show(mapEditorController);
    }

    public void createNewMap() {
        // set current map to null to create a new one and don't update the selected
        mapService.setCurrentMap(null);
        MapEditorController mapEditorController = mapEditorControllerProvider.get();
        this.app.show(mapEditorController);
    }

    public void leaveToLobby() {
        LobbyScreenController lobbyScreenController = lobbyScreenControllerProvider.get();
        this.app.show(lobbyScreenController);
    }
    public App getApp() {
        return this.app;
    }
}
