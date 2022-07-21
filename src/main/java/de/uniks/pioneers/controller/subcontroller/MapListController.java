package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.services.MapBrowserService;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import javax.inject.Inject;

public class MapListController implements Controller {
    private ListView<HBox> mapList;

    private final MapBrowserService mapBrowserService;

    @Inject
    public MapListController(MapBrowserService mapBrowserService) {
        this.mapBrowserService = mapBrowserService;
    }

    @Override
    public void init() {
        //Get Data
    }

    @Override
    public Parent render() {
        mapBrowserService.getMaps().addListener((ListChangeListener<? super MapTemplate>) c-> {
            c.next();
            if(c.wasAdded()){
                c.getAddedSubList().forEach(this::renderListElement);
            }
        });

        return null;
    }

    @Override
    public void stop() {
        mapBrowserService.stop();
    }

    private void renderListElement(MapTemplate map){
        Label nameLabel = new Label(map.name());
        mapList.getItems().add(new HBox(nameLabel));
    }

    public void setMapList(ListView<HBox> mapList) {
        this.mapList = mapList;
    }
}
