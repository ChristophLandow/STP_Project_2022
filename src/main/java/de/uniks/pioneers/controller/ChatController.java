package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GroupService;
import de.uniks.pioneers.services.MessageService;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static de.uniks.pioneers.Constants.CHAT_SCREEN_TITLE;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class ChatController implements Controller {
    private final App app;
    private final MessageService messageService;
    private final GroupService groupService;
    private String currentOpenChat;
    private String currentGroupid;

    @FXML public Button sendButton;
    @FXML public Button leaveButton;
    @FXML public ListView userListView;
    @FXML public TextField messageTextField;
    @FXML public TabPane chatTabPane;

    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;

    @Inject
    public ChatController(App app, MessageService messageService, GroupService groupService, Provider<LobbyScreenController> lobbyScreenControllerProvider) {
        this.app = app;
        this.messageService = messageService;
        this.groupService = groupService;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
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

        for(User u: this.messageService.getchatUserList()){
            addTab(u);
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

    public void addTab(User user){
        if(this.chatTabPane.getTabs().get(0).getText().equals("Chat One")){
            this.chatTabPane.getTabs().get(0).setText(user.name());
            this.chatTabPane.getTabs().get(0).setOnClosed(this::removeUser);
        }
        else{
            ScrollPane newChatScrollPane = new ScrollPane(new VBox());
            newChatScrollPane.setPrefHeight(579);
            Tab newUserTab = new Tab(user.name(), newChatScrollPane);
            newUserTab.setOnClosed(this::removeUser);
            newUserTab.setClosable(true);
            this.chatTabPane.getTabs().add(newUserTab);
            this.chatTabPane.getSelectionModel().select(newUserTab);
        }
        this.currentOpenChat = this.chatTabPane.getSelectionModel().getSelectedItem().getText();
        getOrCreateGroup();
    }

    public void removeUser(Event event){
        Tab closedTab = (Tab) event.getSource();

        this.messageService.getchatUserList().removeIf(u->u.name().equals(closedTab.getText()));
    }

    public void leave(ActionEvent event) {
        app.show(lobbyScreenControllerProvider.get());
    }

    public void send(ActionEvent event) {
        String message = this.messageTextField.getText();
        if (!message.equals("")) {
            messageService.sendMessageToGroup(currentGroupid, new CreateMessageDto(message))
                    .observeOn(FX_SCHEDULER)
                    .doOnError(Throwable::printStackTrace)
                    .subscribe(result -> System.out.println("Message an: " + result._id() + ":" + result.body()));
        }
    }

    public void getOrCreateGroup() {
        String currentUserId = messageService.getUserIdByName(currentOpenChat);
        groupService.getGroupsWithUser(currentUserId)
                .observeOn(FX_SCHEDULER)
                .doOnError(Throwable::printStackTrace)
                .subscribe(res -> {
                    if (res.size() != 0 && res.get(0) != null && res.get(0)._id() != null) {
                        currentGroupid = res.get(0)._id();
                    } else {
                        groupService.createNewGroupWithOtherUser(currentUserId)
                                .observeOn(FX_SCHEDULER)
                                .doOnError(Throwable::printStackTrace)
                                .subscribe(result -> currentGroupid= result._id(), Throwable::printStackTrace);
                    }
                }, Throwable::printStackTrace);
    }
}
