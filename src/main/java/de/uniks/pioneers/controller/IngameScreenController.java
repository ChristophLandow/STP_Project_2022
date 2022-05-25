package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.Tile;
import de.uniks.pioneers.services.BoardGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Paint;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;

import static de.uniks.pioneers.Constants.INGAME_SCREEN_TITLE;
import static de.uniks.pioneers.GameConstants.scale;

@Singleton
public class IngameScreenController implements Controller {
    @FXML public Pane turnPane;
    @FXML public SVGPath streetSVG;
    @FXML public SVGPath houseSVG;
    @FXML public SVGPath citySVG;

    @FXML public Pane fieldPane;

    private final App app;

    @Inject
    public IngameScreenController(App app) {
        this.app = app;
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/IngameScreen.fxml"));
        loader.setControllerFactory(c->this);
        final Parent view;
        try {
            view =  loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        BoardGenerator generator = new BoardGenerator();
        List<Tile> tiles = generator.generate(2);

        for(int i = 0; i < tiles.size(); i++){

            Polygon hex = new Polygon();
            hex.getPoints().addAll(
                    0.0, 1.0,
                    Math.sqrt(3)/2, 0.5,
                    Math.sqrt(3)/2, -0.5,
                    0.0, -1.0,
                    -Math.sqrt(3)/2, -0.5,
                    -Math.sqrt(3)/2, 0.5);
            hex.setScaleX(scale);
            hex.setScaleY(scale);
            Image image = new Image("ingame/tile_wald.png");
            hex.setFill(new ImagePattern(image));
            hex.setLayoutX(tiles.get(i).x + this.fieldPane.getPrefWidth()/2);
            hex.setLayoutY(tiles.get(i).y + this.fieldPane.getPrefHeight()/2);
            this.fieldPane.getChildren().add(hex);


        }


        return view;
    }

    @Override
    public void init() {
        app.getStage().setTitle(INGAME_SCREEN_TITLE);
        setPlayerColor("#FF0000");
    }

    private void swapTurnSymbol() {
        turnPane.getChildren().get(0).setVisible(!turnPane.getChildren().get(0).isVisible());
        turnPane.getChildren().get(1).setVisible(!turnPane.getChildren().get(1).isVisible());
    }

    private void setPlayerColor(String hexColor)
    {
        streetSVG.setFill(Paint.valueOf(hexColor));
        houseSVG.setFill(Paint.valueOf(hexColor));
        citySVG.setFill(Paint.valueOf(hexColor));
    }

    public void giveUp(ActionEvent actionEvent) {
    }

    public void toRules(ActionEvent actionEvent) {
    }

    public void toSettings(ActionEvent actionEvent) {
    }

    public void sendMessage(KeyEvent keyEvent) {
    }

    public void onHammerPressed(MouseEvent mouseEvent) {
    }

    public void onStreetPressed(MouseEvent mouseEvent) {
    }

    public void onHousePressed(MouseEvent mouseEvent) {
    }

    public void onCityPressed(MouseEvent mouseEvent) {
    }

    public void onTradePressed(MouseEvent mouseEvent) {
    }

    public void onTurnPressed(MouseEvent mouseEvent) {
        // only for testing
        swapTurnSymbol();
    }

    @Override
    public void stop() {
    }
}
