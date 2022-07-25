package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.services.MapBrowserService;
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
import java.io.IOException;

public class MapListController implements Controller {
    private ListView<HBox> mapList;
    private ScrollPane mapListScrollPane;

    private final MapBrowserService mapBrowserService;

    @Inject
    public MapListController(MapBrowserService mapBrowserService) {
        this.mapBrowserService = mapBrowserService;
    }

    @Override
    public void init() {
        mapListScrollPane.setOnScroll((ScrollEvent event) -> mapListScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER));
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapListScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    private void updateListElement(HBox element, MapTemplate map){
        for(Node n : element.getChildren()){
            if(n.getId().equals("MapNameLabel")){
                ((Label) n).setText(map.name());
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
