package de.uniks.pioneers.model;

import static de.uniks.pioneers.GameConstants.*;

public record RemainingBuildings(
        Integer settlement,
        Integer city,
        Integer road) {

    // record data is immutable
    public RemainingBuildings updateRemainingBuildings(String type) {
            RemainingBuildings newBuildings= null;
        switch (type){
            case SETTLEMENT -> newBuildings = new RemainingBuildings( settlement-1,city,road);
            case CITY -> newBuildings = new RemainingBuildings(settlement,city-1,road);
            case ROAD -> newBuildings = new RemainingBuildings(settlement,city,road-1);
        }

        return newBuildings;
    }
}
