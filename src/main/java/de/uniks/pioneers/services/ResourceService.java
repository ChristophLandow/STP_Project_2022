package de.uniks.pioneers.services;

import de.uniks.pioneers.model.DevelopmentCard;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.uniks.pioneers.GameConstants.*;

@Singleton
public class ResourceService {
    public ObservableMap<String, Integer> myResources = FXCollections.observableHashMap();
    public ObservableMap<String, Integer> myDevCards = FXCollections.observableHashMap();
    public HashMap<String, Integer> missingResources = new HashMap<>();
    public SimpleBooleanProperty notEnoughResources = new SimpleBooleanProperty();

    @Inject
    public ResourceService() {
    }

    public void updateResources(String type, int amount) {
        myResources.replace(type, myResources.get(type), amount);
    }

    public void updateDevCards(String type, int amount) {
        myDevCards.replace(type, myDevCards.get(type), amount);
    }

    private void calcMissingRessources(Map<String, Integer> cost) {
        missingResources = new HashMap<>();
        cost.keySet().forEach(s -> missingResources.put(s, myResources.get(s) - cost.get(s)));
    }

    public boolean checkRoad() {
        boolean enoughResources = (myResources.get(LUMBER) >= 1 && myResources.get(BRICK) >= 1);

        if (enoughResources) {
            notEnoughResources.set(false);
            return true;
        } else {
            Map<String, Integer> cost = Map.of(BRICK, 1, LUMBER, 1);
            calcMissingRessources(cost);
            notEnoughResources.set(true);
            return false;
        }
    }

    public boolean checkDevCard() {
        boolean enoughResources = false;

        if(myResources.size() > 0) {
            enoughResources = (myResources.get(WOOL) > 0 && myResources.get(GRAIN) > 0 && myResources.get(ORE) > 0);

            if(enoughResources) {
                notEnoughResources.set(false);
            } else {
                Map<String, Integer> cost = Map.of(WOOL, 1, GRAIN, 1, ORE, 1);
                calcMissingRessources(cost);
                notEnoughResources.set(true);
            }
        }

        return enoughResources;
    }

    public boolean checkResourcesSettlement() {
        boolean enoughResources = (myResources.get(LUMBER) >= 1 && myResources.get(BRICK) >= 1
                && myResources.get(GRAIN) >= 1 && myResources.get(WOOL) >= 1);

        if (enoughResources) {
            notEnoughResources.set(false);
            return true;
        } else {
            Map<String, Integer> cost = Map.of(BRICK, 1, LUMBER, 1, GRAIN, 1, WOOL, 1);
            calcMissingRessources(cost);
            notEnoughResources.set(true);
            return false;
        }
    }

    public boolean checkCity() {
        boolean enoughResources = (myResources.get(ORE) >= 3 && myResources.get(GRAIN) >= 2);

        if (enoughResources) {
            notEnoughResources.set(false);
            return true;
        } else {
            Map<String, Integer> cost = Map.of(ORE, 3, GRAIN, 2);
            calcMissingRessources(cost);
            notEnoughResources.set(true);
            return false;
        }
    }

    public HashMap<String, Integer> getDevCardMap(List<DevelopmentCard> devCards) {
        HashMap<String, Integer> devCardMap = new HashMap<>();
        int knight = 0;
        int road = 0;
        int plenty = 0;
        int monopoly = 0;
        int vpoint = 0;
        int unknown = 0;

        for(DevelopmentCard devCard : devCards) {
            switch (devCard.type()) {
                case "knight" -> knight += 1;
                case "road-building" -> road += 1;
                case "year-of-plenty" -> plenty += 1;
                case "monopoly" -> monopoly += 1;
                case "victory-point" -> vpoint += 1;
                case "unknown" -> unknown += 1;
            }
        }

        devCardMap.put("knight", knight);
        devCardMap.put("road", road);
        devCardMap.put("plenty", plenty);
        devCardMap.put("monopoly", monopoly);
        devCardMap.put("vpoint", vpoint);

        return devCardMap;
    }

    public void resetMyResources() {
        myResources = FXCollections.observableHashMap();
        myDevCards = FXCollections.observableHashMap();
        notEnoughResources = new SimpleBooleanProperty();
    }
}
