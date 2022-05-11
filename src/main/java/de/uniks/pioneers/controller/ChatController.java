package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Constants;
import de.uniks.pioneers.controller.subcontroller.ChatTabController;
import de.uniks.pioneers.controller.subcontroller.ChatUserlistController;
import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GroupService;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.services.UserlistService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static de.uniks.pioneers.Constants.CHAT_SCREEN_TITLE;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class ChatController implements Controller {
    private final App app;
    private final MessageService messageService;
    private final UserService userService;
    private final GroupService groupService;
    private final EventListener eventListener;

    private final CompositeDisposable disposable = new CompositeDisposable();

    @FXML public Button sendButton;
    @FXML public Button leaveButton;
    @FXML public ListView<Label> userListView;
    @FXML public TextField messageTextField;
    @FXML public TabPane chatTabPane;

    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final Provider<ChatUserlistController> userlistControllerProvider;

    private final ArrayList<ChatTabController> chatTabControllers = new ArrayList<>();
    private String currentGroupId;

    private final Timer timer = new Timer();

    @Inject
    public ChatController(App app, MessageService messageService, UserService userService, UserlistService userlistService,
                          EventListener eventListener, GroupService groupService,
                          Provider<LobbyScreenController> lobbyScreenControllerProvider,
                          Provider<ChatUserlistController> userlistControllerProvider) {
        this.app = app;
        this.messageService = messageService;
        this.userService = userService;
        this.groupService = groupService;
        this.eventListener = eventListener;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.userlistControllerProvider = userlistControllerProvider;
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

        ChatUserlistController chatUserlistController = userlistControllerProvider.get();
        chatUserlistController.chatController = this;
        chatUserlistController.chatTabControllers = this.chatTabControllers;
        chatUserlistController.userListView = this.userListView;
        chatUserlistController.chatTabPane = this.chatTabPane;
        chatUserlistController.render();
        chatUserlistController.init();

        return view;
    }

    @Override
    public void init() {
        app.getStage().setTitle(CHAT_SCREEN_TITLE);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> resetOpenChatCounter());
            }
        }, 1000*5, 1000*10);
    }


    @Override
    public void stop() {
        for(ChatTabController chatTabController : this.chatTabControllers){
            chatTabController.stop();
        }

        timer.cancel();

        this.chatTabControllers.clear();

        disposable.dispose();
    }

    public void addTab(User user){
        ChatTabController newChatController = new ChatTabController(this, this.messageService, this.userService, this.groupService, this.chatTabPane, user, this.eventListener);
        newChatController.render();
        newChatController.init();

        this.chatTabControllers.add(newChatController);
    }

    public void removeTab(Event event){
        Tab closedTab = (Tab) event.getSource();
        this.messageService.getchatUserList().removeIf(u->u.name().equals(closedTab.getText()));

        for(ChatTabController chatTabController : this.chatTabControllers){
            if(chatTabController.chattingWith.name().equals(closedTab.getText())){
                chatTabController.stop();
            }
        }

        this.chatTabControllers.removeIf(c->c.chattingWith.name().equals(closedTab.getText()));
    }

    public void leave(ActionEvent event) {
        this.messageService.getchatUserList().clear();
        app.show(lobbyScreenControllerProvider.get());
    }

    public void send(ActionEvent event) {
        Tab open = chatTabPane.getSelectionModel().getSelectedItem();
        for (ChatTabController chatTabController : chatTabControllers) {
            if (chatTabController.chattingWith.name().equals(open.getText())) {
                currentGroupId = chatTabController.groupId.get();
            }
        }

        if(!currentGroupId.isEmpty()){
            String message = this.messageTextField.getText();
            if (!message.equals("")) {
                disposable.add(messageService.sendMessageToGroup(currentGroupId, new CreateMessageDto(message))
                        .observeOn(FX_SCHEDULER)
                        .doOnError(Throwable::printStackTrace)
                        .subscribe(result -> {
                            System.out.println("Message mit Id: " + result._id() + " von " + result.sender() + ":" + result.body());
                            this.messageTextField.clear();
                        }));
            }
        }

    }

    public void resetOpenChatCounter(){
        this.messageService.decreaseChatCounter(Constants.MAX_LOADING_CHATS);
    }
}
