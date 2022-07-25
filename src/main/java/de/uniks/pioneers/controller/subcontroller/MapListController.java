package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.services.StylesService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.services.MapBrowserService;
import de.uniks.pioneers.services.UserService;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

public class MapListController implements Controller {
    private final App app;
    private final StylesService stylesService;
    private final UserService userService;
    private final PrefService prefService;
    private ListView<HBox> mapList;
    private ScrollPane mapListScrollPane;
    private final MapBrowserService mapBrowserService;
    private final Provider<MapDetailsController> mapDetailsControllerProvider;

    @Inject
    public MapListController(App app, PrefService prefService, StylesService styleService, MapBrowserService mapBrowserService, UserService userService, Provider<MapDetailsController> mapDetailsControllerProvider) {
        this.mapBrowserService = mapBrowserService;
        this.app = app;
        this.stylesService = styleService;
        this.userService = userService;
        this.prefService = prefService;
        this.mapDetailsControllerProvider = mapDetailsControllerProvider;
    }

    @Override
    public void init() {
        this.mapList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                mapDetailsControllerProvider.get().updateMapDetails(newValue.getId());
            }
        });
        mapListScrollPane.setOnScroll((ScrollEvent event) -> mapListScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER));
        String styleLocal = "/de/uniks/pioneers/styles/MapBrowserListElement.css";
        String styleLocalDark = "/de/uniks/pioneers/styles/DarkMode_MapBrowserListElement.css";
        stylesService.setStyleSheets(app.getStage().getScene().getStylesheets(), styleLocal, styleLocalDark);
    }

    @Override
    public Parent render() {
        mapBrowserService.getMaps().forEach(this::renderListElement);

        mapBrowserService.getMaps().addListener((ListChangeListener<? super MapTemplate>) c-> {
            c.next();
            if(c.wasAdded()){
                c.getAddedSubList().forEach(this::renderListElement);
            }
            else if (c.wasRemoved()){
                c.getRemoved().forEach(mapTemplate -> mapList.getItems().removeIf(hBox -> hBox.getId().equals(mapTemplate._id())));
            }
            else if(c.wasUpdated()){
                for(int i = c.getFrom(); i <= c.getTo(); i++){
                    MapTemplate mapToUpdate = mapBrowserService.getMaps().get(i);
                    updateListElement((HBox) mapList.lookup(mapToUpdate._id()), mapToUpdate);
                }
            }
        });

        return null;
    }

    @Override
    public void stop() {
        mapBrowserService.stop();
    }

    private void renderListElement(MapTemplate map){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/MapListElement.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            HBox newListElement = loader.load();
            newListElement.setId(map._id());
            mapList.getItems().add(newListElement);

            //Adjust HBox Elements
            updateListElement(newListElement, map);
            MapBrowserListElementController elementController = new MapBrowserListElementController(prefService, userService, mapBrowserService, map, newListElement);
            elementController.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapListScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    private void updateListElement(HBox element, MapTemplate map){
        element.setId(map.name());
        for(Node n : element.getChildren()){
            //show map name
            if(n.getId().equals("MapNameLabel")){
                ((Label) n).setText(map.name());
            }
            //show map votes
            if(n.getId().equals("VotingLabel")){
                ((Label) n).setText(Integer.toString(map.votes()));
            }
        }

        sortList();
    }

    private void sortList(){
        mapList.getItems().sort(((o1, o2) -> {
            Label voting1 = (Label) o1.getChildren().get(1);
            Label voting2 = (Label) o2.getChildren().get(1);
            return (Integer.parseInt(voting1.getText()) < Integer.parseInt(voting2.getText())) ? 1 : 0;
        }));
    }

    public void setMapList(ListView<HBox> mapList) {
        this.mapList = mapList;
    }

    public void setMapListScrollPane(ScrollPane mapListScrollPane) {
        this.mapListScrollPane = mapListScrollPane;
    }
}
