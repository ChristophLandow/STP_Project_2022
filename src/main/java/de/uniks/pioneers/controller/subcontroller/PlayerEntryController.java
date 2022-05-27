package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

import java.io.IOException;

public class PlayerEntryController implements Controller {
    @FXML HBox playerEntry;
    @FXML ImageView playerAvatar;
    @FXML Label playerNameLabel;
    @FXML SVGPath playerHouseSVG;
    @FXML HBox playerReadyBox;
    @FXML Label playerReadyLabel;
    private boolean ready;

    public PlayerEntryController(Image playerAvatar, String playerName, String hexColor, String playerID) {
        this.render();
        this.playerEntry.setId(playerID);
        this.playerAvatar.setImage(playerAvatar);
        this.playerNameLabel.setText(playerName);
        this.playerHouseSVG.setFill(Paint.valueOf(hexColor));
        this.ready = false;
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Parent render() {
        Parent parent;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/PlayerEntry.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            parent = loader.load();
            return parent;
        } catch (IOException e) {
            return null;
        }
    }

    public void setReady(boolean ready) {
        if(ready) {
            this.playerReadyLabel.setText("Ready");
            this.playerReadyLabel.setAlignment(Pos.CENTER);
            this.playerReadyBox.setBackground(Background.fill(Color.GREEN));
        } else {
            this.playerReadyLabel.setText("Not Ready");
            this.playerReadyLabel.setAlignment(Pos.CENTER);
            this.playerReadyBox.setBackground(Background.fill(Color.RED));

        }
        this.ready = ready;
    }

    public void setColor(String hexColor) {
        this.playerHouseSVG.setFill(Paint.valueOf(hexColor));
    }

    public HBox getPlayerEntry() {
        return this.playerEntry;
    }

    public boolean getReady() {
        return this.ready;
    }
}
