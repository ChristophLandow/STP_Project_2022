package de.uniks.pioneers.model;

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

    public Map<String,Integer> createMap(){
        Map<String ,Integer> resources = new HashMap<>();
        resources.put("grain",grain());
        resources.put("brick",brick());
        resources.put("ore",ore());
        resources.put("lumber",lumber());
        resources.put("wool", wool());
        return resources;
    }
}
