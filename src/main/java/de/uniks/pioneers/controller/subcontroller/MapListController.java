package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.services.MapBrowserService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.StylesService;
import de.uniks.pioneers.services.UserService;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;

public class MapListController implements Controller {

    @FXML
    private final App app;
    private final StylesService stylesService;
    private final UserService userService;
    private final PrefService prefService;
    @FXML
    Button VoteButton;
    private ListView<HBox> mapList;
    private ScrollPane mapListScrollPane;

    private final MapBrowserService mapBrowserService;



    @Inject
    public MapListController(App app, PrefService prefService, StylesService styleService, MapBrowserService mapBrowserService, UserService userService) {
        this.mapBrowserService = mapBrowserService;
        this.app = app;
        this.stylesService = styleService;
        this.userService = userService;
        this.prefService = prefService;
    }

    @Override
    public void init() {
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
    }

    public void setMapList(ListView<HBox> mapList) {
        this.mapList = mapList;
    }

    public void setMapListScrollPane(ScrollPane mapListScrollPane) {
        this.mapListScrollPane = mapListScrollPane;
    }
}
