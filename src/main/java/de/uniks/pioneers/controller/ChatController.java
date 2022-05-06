package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.ChatTabController;
import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GroupService;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.UserService;
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
import java.util.ArrayList;

import static de.uniks.pioneers.Constants.CHAT_SCREEN_TITLE;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class ChatController implements Controller {
    private final App app;
    private final MessageService messageService;
    private final GroupService groupService;
    private final UserService userService;
    private String currentOpenChat;
    private String currentGroupid;

    @FXML public Button sendButton;
    @FXML public Button leaveButton;
    @FXML public ListView userListView;
    @FXML public TextField messageTextField;
    @FXML public TabPane chatTabPane;

    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;

    private final ArrayList<ChatTabController> chatTabControllers = new ArrayList<>();

    @Inject
    public ChatController(App app, MessageService messageService, GroupService groupService, UserService userService,
                          Provider<LobbyScreenController> lobbyScreenControllerProvider) {
        this.app = app;
        this.messageService = messageService;
        this.userService = userService;
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

        this.chatTabPane.getTabs().remove(0);

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
        ChatTabController newChatController = new ChatTabController(this, this.messageService, this.userService, this.chatTabPane, user);
        newChatController.init();

        this.chatTabControllers.add(newChatController);

        this.currentOpenChat = this.chatTabPane.getSelectionModel().getSelectedItem().getText();
        getOrCreateGroup();
    }

    public void removeTab(Event event){
        Tab closedTab = (Tab) event.getSource();

        this.messageService.getchatUserList().removeIf(u->u.name().equals(closedTab.getText()));
        this.chatTabControllers.removeIf(c->c.chattingWith.name().equals(closedTab.getText()));
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
