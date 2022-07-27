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
    @FXML ImageView spectatorImageView;
    @FXML HBox playerReadyBox;
    @FXML Label playerReadyLabel;
    private String playerColor;
    private boolean ready;
    private boolean spectator;

    public PlayerEntryController(Image playerAvatar, String playerName, String hexColor, String playerID) {
        this.render();
        this.playerEntry.setId(playerID);
        this.playerAvatar.setImage(playerAvatar);
        this.playerNameLabel.setText(playerName);
        this.playerHouseSVG.setStroke(Paint.valueOf(hexColor));
        this.playerHouseSVG.setVisible(false);
        this.playerColor = hexColor;
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

    public void setReady(boolean ready, boolean spectator) {
        if(ready && !spectator) {
            this.playerHouseSVG.setVisible(true);
            this.spectatorImageView.setVisible(false);
            this.playerReadyLabel.setText("Ready");
            this.playerReadyLabel.setAlignment(Pos.CENTER);
            this.playerReadyBox.setBackground(Background.fill(Color.GREEN));
        } else if(ready && spectator) {
            this.playerHouseSVG.setVisible(false);
            this.spectatorImageView.setVisible(true);
            this.playerReadyLabel.setText("Ready");
            this.playerReadyLabel.setAlignment(Pos.CENTER);
            this.playerReadyBox.setBackground(Background.fill(Color.GREEN));
        } else {
            this.playerHouseSVG.setVisible(false);
            this.spectatorImageView.setVisible(false);
            this.playerReadyLabel.setText("Not Ready");
            this.playerReadyLabel.setAlignment(Pos.CENTER);
            this.playerReadyBox.setBackground(Background.fill(Color.RED));
        }

        this.ready = ready;
        this.spectator = spectator;
    }

    public void setColor(String hexColor) {
        this.playerHouseSVG.setStroke(Paint.valueOf(hexColor));
        this.playerColor = hexColor;
    }

    public HBox getPlayerEntry() {
        return this.playerEntry;
    }

    public String getPlayerColor() {
        return this.playerColor;
    }

    public boolean getReady() {
        return this.ready;
    }

    public boolean getSpectator() {
        return this.spectator;
    }
}
