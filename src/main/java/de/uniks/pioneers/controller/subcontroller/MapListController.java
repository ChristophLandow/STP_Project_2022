package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.services.MapBrowserService;
import de.uniks.pioneers.services.StylesService;
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
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;

public class MapListController implements Controller {

    @FXML
    private final App app;
    @FXML
    Button VoteButton;
    private ListView<HBox> mapList;

    private ArrayList<HBox> mapListElements = new ArrayList<>();
    private final StylesService stylesService;
    private final MapBrowserService mapBrowserService;

    

    @Inject
    public MapListController(App app,StylesService styleService, MapBrowserService mapBrowserService) {
        this.mapBrowserService = mapBrowserService;
        this.app = app;
        this.stylesService = styleService;
    }

    @Override
    public void init() {
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
            MapBrowserListElementController elementController = new MapBrowserListElementController( mapBrowserService, map, newListElement);
            elementController.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        mapListElements.add(element);
    }

    public void setMapList(ListView<HBox> mapList) {
        this.mapList = mapList;
    }
}

/*ObservableList<MapTemplate> maps = mapBrowserService.getMaps();
        for(MapTemplate map : maps){
            for(HBox listElement : mapListElements){
                if(map.name().equals(listElement.getId())){
                    CreateVoteDto voteMove = new CreateVoteDto(map.votes() + 1);
                    System.out.println("vote for " + map.name());
                    mapBrowserService.vote(map._id(),voteMove);
                }
            }
        }*/
