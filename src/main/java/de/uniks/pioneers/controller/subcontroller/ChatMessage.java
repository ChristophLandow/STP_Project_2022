package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;

import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.dto.UpdateMessageDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.MessageService;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import static de.uniks.pioneers.Constants.DELETE_MESSAGE_TEXT;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class ChatMessage {

    private final VBox tabChatBox;
    private HBox chatBox;
    private final User sender;

    private final SimpleStringProperty groupId;
    private final MessageDto message;
    private final MessageService messageService;


    public ChatMessage(User sender, MessageDto message, VBox tabChatBox, SimpleStringProperty groupId, MessageService messageService){
        this.sender = sender;
        this.message = message;
        this.tabChatBox = tabChatBox;
        this.groupId = groupId;
        this.messageService = messageService;
    }

    public void init(){
        ImageView avatarImgView;

        try{
            avatarImgView = new ImageView(new Image(this.sender.avatar()));
        }
        catch(IllegalArgumentException | NullPointerException e){
            avatarImgView = new ImageView(new Image(App.class.getResource("user-avatar.svg").toString()));
        }

        avatarImgView.setFitHeight(20);
        avatarImgView.setFitWidth(20);

        Text chatText = new Text(sender.name() + ": " + message.body());
        chatText.setFont(new Font(16));

        TextFlow textFlow = new TextFlow(chatText);
        textFlow.setPrefWidth(310);

        ImageView deleteImgView = new ImageView(new Image(App.class.getResource("trash.png").toString()));
        deleteImgView.setFitHeight(20);
        deleteImgView.setFitWidth(20);
        deleteImgView.setStyle("-fx-cursor: hand;");

        if(this.sender.name().equals("Me")){
            deleteImgView.setVisible(true);
            deleteImgView.setOnMouseClicked(this::removeChat);
        }
        else{
            deleteImgView.setVisible(false);
        }

        HBox newChat = new HBox(avatarImgView, textFlow, deleteImgView);
        newChat.setPadding(new Insets(5,0,0,0));
        newChat.setMargin(textFlow, new Insets(0,0,0,5));
        newChat.setSpacing(5);

        this.tabChatBox.getChildren().add(newChat);
    }

    public void removeChat(MouseEvent event){
        messageService.updateMessage("groups", groupId.getValue(), message._id(),new UpdateMessageDto(DELETE_MESSAGE_TEXT))
                .observeOn(FX_SCHEDULER)
                .doOnError(Throwable::printStackTrace)
                .subscribe(res -> System.out.println("Nachricht gelöscht!"));
    }

    public String getMessageID(){
        return this.message._id();
    }
}
