package de.uniks.pioneers.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;

public record Resources(
        Integer unknown,
        Integer grain,
        Integer brick,
        Integer ore,
        Integer lumber,
        Integer wool

) {

    public Resources(Integer grain, Integer brick, Integer ore, Integer lumber, Integer wool) {
        this(null, grain, brick, ore, lumber, wool);
    }

    public Resources normalize (){
        int brick = brick() == null ? 0 : brick();
        int grain = grain() == null ? 0 : grain();
        int ore = ore() == null ? 0 : ore();
        int lumber = lumber() == null ? 0 : lumber();
        int wool = wool() == null ? 0 : wool();
        int unknown = unknown() == null ? 0 : unknown();
        return new Resources(unknown, grain, brick, ore, lumber, wool);
    }

    public ObservableMap<String,Integer> createMap(){
        ObservableMap<String ,Integer> resources = FXCollections.observableHashMap();
        resources.put("grain",grain());
        resources.put("brick",brick());
        resources.put("ore",ore());
        resources.put("lumber",lumber());
        resources.put("wool", wool());
        return resources;
    }
}
