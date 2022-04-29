package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.services.MessageService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.io.IOException;

import static de.uniks.pioneers.Constants.CHAT_SCREEN_TITLE;

public class ChatController implements Controller {
    private final App app;
    private final MessageService messageService;
    @FXML public Button sendButton;
    @FXML public Button leaveButton;
    @FXML public ListView userListView;
    @FXML public TextField messageTextField;
    @FXML public TabPane chatTabPane;

    @Inject
    public ChatController(App app, MessageService messageService) {
        this.app = app;
        this.messageService = messageService;
    }

    @Override
    public Parent render() {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("views/ChatScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent view;
        try {
            view = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return view;
    }

    @Override
    public void init() {
        app.getStage().setTitle(CHAT_SCREEN_TITLE);
    }


    @Override
    public void stop() {
    }

    public void leave(MouseEvent mouseEvent) {
    }

    public void send(MouseEvent mouseEvent) {
        String message = this.messageTextField.getText();
        if (!message.equals("")) {
            // TODO: send message via Rest
            HBox messageBox = new HBox();
            ImageView avatar = new ImageView(); // TODO: set avatar as image
            Label msg = new Label("Me" + ": " + message); // TODO: set text to username
            messageBox.getChildren().add(avatar);
            messageBox.getChildren().add(msg);
            messageBox.getChildren().add(new ImageView(new Image("trash.png"))); // TODO: correct URL?
            ((VBox)((ScrollPane)this.chatTabPane.getTabs().get(0).getContent()).getContent()).getChildren().add(messageBox); // TODO: set to correct Tab
        }
    }
}
