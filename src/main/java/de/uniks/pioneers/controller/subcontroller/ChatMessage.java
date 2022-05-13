package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Constants;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.dto.UpdateMessageDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.MessageService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Objects;

import static de.uniks.pioneers.Constants.DELETE_MESSAGE_TEXT;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class ChatMessage {

    private final VBox tabChatBox;
    private HBox chatBox;
    private Text messageText;
    private ImageView deleteImgView;
    private final User sender;

    private final SimpleStringProperty groupId;
    private final MessageDto message;
    private final MessageService messageService;
    private final CompositeDisposable disposable = new CompositeDisposable();


    public ChatMessage(User sender, MessageDto message, VBox tabChatBox, SimpleStringProperty groupId, MessageService messageService){
        this.sender = sender;
        this.message = message;
        this.tabChatBox = tabChatBox;
        this.groupId = groupId;
        this.messageService = messageService;
    }

    public void render(){
        ImageView avatarImgView;

        try{
            avatarImgView = new ImageView(new Image(this.sender.avatar()));
        }
        catch(IllegalArgumentException | NullPointerException e){
            avatarImgView = new ImageView(new Image(Constants.DEFAULT_AVATAR));
        }

        avatarImgView.setFitHeight(20);
        avatarImgView.setFitWidth(20);

        messageText = new Text(sender.name() + ": " + message.body());
        messageText.setFont(new Font(16));

        TextFlow textFlow = new TextFlow(messageText);
        textFlow.setPrefWidth(310);

        deleteImgView = new ImageView(new Image(Objects.requireNonNull(App.class.getResource("trash.jpg")).toString()));
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

        chatBox = new HBox(avatarImgView, textFlow, deleteImgView);
        chatBox.setPadding(new Insets(5,0,0,0));
        HBox.setMargin(textFlow, new Insets(0,0,0,5));
        chatBox.setSpacing(5);

        this.tabChatBox.getChildren().add(chatBox);
    }

    public void removeChat(MouseEvent event){
        disposable.add(messageService.updateMessage("groups", groupId.getValue(), message._id(),new UpdateMessageDto(DELETE_MESSAGE_TEXT))
                .observeOn(FX_SCHEDULER)
                .doOnError(Throwable::printStackTrace)
                .subscribe(res -> System.out.println("Nachricht gelöscht!")));
    }

    public void stop(){
        this.chatBox = null;
        this.tabChatBox.getChildren().remove(this.chatBox);
    }

    public String getMessageID(){
        return this.message._id();
    }

    public void setMessageText(String newText){
        this.messageText.setText(newText);

        if(newText.equals(DELETE_MESSAGE_TEXT)){
            deleteImgView.setOnMouseClicked(null);
            deleteImgView.setVisible(false);
            messageText.setFont(Font.font("Verdana", FontPosture.ITALIC, 16));
            messageText.setFill(Color.GRAY);
        }
    }
}
