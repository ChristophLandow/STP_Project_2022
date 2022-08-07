package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.IngameService;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import javax.inject.Inject;

import static de.uniks.pioneers.GameConstants.*;

public class IngameSelectController {
    private GameStorage gameStorage;
    private IngameService ingameService;
    private Pane roadFrame;
    private Pane settlementFrame;
    private Pane cityFrame;

    @Inject
    public IngameSelectController() {
    }

    public void init(GameStorage gameStorage, IngameService ingameService, Pane roadFrame, Pane settlementFrame, Pane cityFrame) {
        this.gameStorage = gameStorage;
        this.ingameService = ingameService;
        this.roadFrame = roadFrame;
        this.settlementFrame = settlementFrame;
        this.cityFrame = cityFrame;

        roadFrame.setOnMouseClicked(mouseEvent2 -> selectStreet());
        settlementFrame.setOnMouseClicked(mouseEvent1 -> selectSettlement());
        cityFrame.setOnMouseClicked(mouseEvent -> selectCity());
    }

    public void selectStreet() {
        if(ingameService.getExpectedMove().action().equals(BUILD)) {
            if(!this.gameStorage.selectedBuilding.equals(ROAD)) {
                this.gameStorage.selectedBuilding = ROAD;
                this.roadFrame.setBackground(Background.fill(Color.rgb(144,238,144)));
                this.settlementFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
                this.cityFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
            } else {
                resetSelect();
            }
        }
    }

    public void selectSettlement() {
        if(ingameService.getExpectedMove().action().equals(BUILD)) {
            if(!this.gameStorage.selectedBuilding.equals(SETTLEMENT)) {
                this.gameStorage.selectedBuilding = SETTLEMENT;
                this.settlementFrame.setBackground(Background.fill(Color.rgb(144,238,144)));
                this.roadFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
                this.cityFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
            } else {
                resetSelect();
            }
        }
    }

    public void selectCity() {
        if(ingameService.getExpectedMove().action().equals(BUILD)) {
            if(!this.gameStorage.selectedBuilding.equals(CITY)) {
                this.gameStorage.selectedBuilding = CITY;
                this.cityFrame.setBackground(Background.fill(Color.rgb(144,238,144)));
                this.settlementFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
                this.roadFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
            } else {
                resetSelect();
            }
        }
    }

    public void resetSelect() {
        this.gameStorage.selectedBuilding = "";
        this.roadFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
        this.settlementFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
        this.cityFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
    }
}
