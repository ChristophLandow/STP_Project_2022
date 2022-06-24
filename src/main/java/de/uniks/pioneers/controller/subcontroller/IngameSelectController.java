package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.GameStorage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import static de.uniks.pioneers.GameConstants.*;

public class IngameSelectController {
    private final GameStorage gameStorage;
    private final Pane roadFrame;
    private final Pane settlementFrame;
    private final Pane cityFrame;
    private final SVGPath streetSVG;
    private final SVGPath houseSVG;
    private final SVGPath citySVG;

    public IngameSelectController(GameStorage gameStorage, Pane roadFrame, Pane settlementFrame, Pane cityFrame, SVGPath streetSVG, SVGPath houseSVG, SVGPath citySVG) {
        this.gameStorage = gameStorage;
        this.roadFrame = roadFrame;
        this.settlementFrame = settlementFrame;
        this.cityFrame = cityFrame;
        this.streetSVG = streetSVG;
        this.houseSVG = houseSVG;
        this.citySVG = citySVG;

        streetSVG.setOnMouseClicked(this::selectStreet);
        houseSVG.setOnMouseClicked(this::selectSettlement);
        citySVG.setOnMouseClicked(this::selectCity);
    }

    public void selectStreet(MouseEvent mouseEvent) {
        this.gameStorage.selectedBuilding = ROAD;
        this.roadFrame.setBackground(Background.fill(Color.rgb(0,100,0)));
        this.settlementFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
        this.cityFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
    }
    public void selectSettlement(MouseEvent mouseEvent) {
        this.gameStorage.selectedBuilding = SETTLEMENT;
        this.settlementFrame.setBackground(Background.fill(Color.rgb(0,100,0)));
        this.roadFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
        this.cityFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
    }
    public void selectCity(MouseEvent mouseEvent) {
        this.gameStorage.selectedBuilding = CITY;
        this.cityFrame.setBackground(Background.fill(Color.rgb(0,100,0)));
        this.settlementFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
        this.roadFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
    }
}
