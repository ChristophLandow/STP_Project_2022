package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.ChatController;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.UserService;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import javax.inject.Inject;

import java.util.ArrayList;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class ChatTabController {

    private final TabPane chatTabPane;
    private VBox chatBox;
    public User chattingWith;
    public User currentUser;

    private final MessageService messageService;
    private final UserService userService;
    private final ChatController chatController;

    private ArrayList<ChatMessage> chatMessages = new ArrayList<>();

    public ChatTabController(ChatController chatController, MessageService messageService, UserService userService, TabPane chatTabPane, User chattingWith){
        this.chatController = chatController;
        this.messageService = messageService;
        this.userService = userService;
        this.chatTabPane = chatTabPane;
        this.chattingWith = chattingWith;
    }

    public void init(){

        this.chatBox = new VBox();

        ScrollPane newChatScrollPane = new ScrollPane(this.chatBox);
        newChatScrollPane.setPrefHeight(579);

        Tab newChatTab = new Tab(this.chattingWith.name(), newChatScrollPane);
        newChatTab.setOnClosed(this.chatController::removeTab);
        newChatTab.setClosable(true);

        this.chatTabPane.getTabs().add(newChatTab);
        this.chatTabPane.getSelectionModel().select(newChatTab);


        this.userService.getCurrentUser()
                .take(1)
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    currentUser = user;
                    //renderMessage(new MessageDto("","", "", "Me", "Test test test test"));
                    //renderMessage(new MessageDto("","", "", chattingWith.name(), "Test test"));
                });
    }

    public void renderMessage(MessageDto message){
        ChatMessage newMessage;
        if(message.sender().equals(this.chattingWith.name())){
            newMessage = new ChatMessage(chattingWith, message, this.chatBox);
        }
        else{
            newMessage = new ChatMessage(new User(currentUser._id(),"Me", currentUser.status(),currentUser.avatar()), message, this.chatBox);
        }

        newMessage.init();
        chatMessages.add(newMessage);
    }


}
