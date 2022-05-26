package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Paint;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;

import static de.uniks.pioneers.Constants.INGAME_SCREEN_TITLE;

@Singleton
public class IngameScreenController implements Controller {
    @FXML public Pane turnPane;
    @FXML public SVGPath streetSVG;
    @FXML public SVGPath houseSVG;
    @FXML public SVGPath citySVG;
    @FXML public Button rulesButton;
    private String clientPlayerColor;

    private final App app;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;

    @Inject
    public IngameScreenController(App app, Provider<RulesScreenController> rulesScreenControllerProvider) {
        this.app = app;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
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
        return view;
    }

    @Override
    public void init() {
        app.getStage().setTitle(INGAME_SCREEN_TITLE);
    }

    private void swapTurnSymbol() {
        turnPane.getChildren().get(0).setVisible(!turnPane.getChildren().get(0).isVisible());
        turnPane.getChildren().get(1).setVisible(!turnPane.getChildren().get(1).isVisible());
    }

    public void setPlayerColor(String hexColor)
    {
        clientPlayerColor = hexColor;
        streetSVG.setFill(Paint.valueOf(hexColor));
        houseSVG.setFill(Paint.valueOf(hexColor));
        citySVG.setFill(Paint.valueOf(hexColor));
    }

    public void giveUp(ActionEvent actionEvent) {
    }

    public void toRules(ActionEvent actionEvent) {
        RulesScreenController controller = rulesScreenControllerProvider.get();
        controller.init();
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
