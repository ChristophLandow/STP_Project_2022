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
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static de.uniks.pioneers.Constants.CHAT_SCREEN_TITLE;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class ChatController implements Controller {
    @FXML public Button sendButton;
    @FXML public Button leaveButton;
    @FXML public ListView<Label> userListView;
    @FXML public TextField messageTextField;
    @FXML public TabPane chatTabPane;

    private final App app;
    private final MessageService messageService;
    private final UserService userService;
    private final GroupService groupService;
    private final EventListener eventListener;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final Provider<ChatUserlistController> userlistControllerProvider;
    private final ObservableList<ChatTabController> chatTabControllers = FXCollections.observableArrayList(new ArrayList<>());
    private final ListChangeListener<ChatTabController> listChangeListener = c -> sendButtonBinding();
    private String currentGroupId;
    private final Timer timer = new Timer();
    private boolean darkMode = false;

    @Inject
    public ChatController(App app, MessageService messageService, UserService userService,
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

        this.chatTabPane.getTabs().remove(0);

        // Call sendButtonBinding if the opened tab changes
        this.chatTabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            sendButtonBinding();

            // Sometimes the chatTabController of the opened Tab is null
            // This listener calls sendButtonBinding on changes of the ChatTabController list, until the chatTabController for the tab is created
            chatTabControllers.addListener(listChangeListener);
        });

        for(User u: this.messageService.getchatUserList()){
            this.addTab(u);
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
        if(darkMode){
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/ChatScreen.css")));
            this.app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_ChatScreen.css");
        } else {
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_ChatScreen.css")));
            this.app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/ChatScreen.css");
        }
        this.sendButton.setDefaultButton(true);
        Node textFieldNode = this.messageTextField;
        Node sendButtonNode = this.sendButton;
        setEventHandler(textFieldNode);
        setEventHandler(sendButtonNode);

        this.messageService.increaseOpenChatCounter();
        this.messageService.increaseOpenChatCounter();
        //Timer to deacrease openChatCounter every 10 seconds
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

    public void addTab(User user) {
        ChatTabController newChatController = new ChatTabController(this, this.messageService, this.userService, this.groupService, this.chatTabPane, user, this.eventListener);
        newChatController.render();
        newChatController.init();

        this.chatTabControllers.add(newChatController);
    }

    public void removeTab(Event event) {
        Tab closedTab = (Tab) event.getSource();
        this.messageService.getchatUserList().removeIf(u->u.name().equals(closedTab.getText()));

        for(ChatTabController chatTabController : this.chatTabControllers){
            if(chatTabController.chattingWith.name().equals(closedTab.getText())){
                chatTabController.stop();
            }
        }

        this.chatTabControllers.removeIf(c->c.chattingWith.name().equals(closedTab.getText()));
    }

    public void sendButtonBinding() {
        //Bind the button with the opened tab and his controller
        Tab openTab = this.chatTabPane.getSelectionModel().getSelectedItem();

        if(openTab != null) {
            for (ChatTabController tabController : this.chatTabControllers) {
                if (tabController.chattingWith.name().equals(openTab.getText())) {
                    sendButton.disableProperty().bind(tabController.getFinishedInitialization().not());
                    //Remove listener after finding the ChatTabController. The listener is not needed until the open tab changes
                    chatTabControllers.removeListener(listChangeListener);
                    break;
                }
            }
        }
        else{
            this.sendButton.disableProperty().unbind();
            this.sendButton.setDisable(true);
        }
    }

    public void leave(ActionEvent ignoredEvent) {
        this.messageService.getchatUserList().clear();
        LobbyScreenController lobbyController = lobbyScreenControllerProvider.get();
        if(darkMode){
            lobbyController.setDarkMode();
        } else {
            lobbyController.setBrightMode();
        }
        app.show(lobbyController);
    }

    public void send(ActionEvent ignoredEvent) {
        //Send messages
        Tab openTab = chatTabPane.getSelectionModel().getSelectedItem();

        //Find the ChatTabController of the open tab
        for (ChatTabController chatTabController : chatTabControllers) {
            if (chatTabController.chattingWith.name().equals(openTab.getText())) {
                currentGroupId = chatTabController.groupId.get();
            }
        }

        if (!currentGroupId.isEmpty()) {
            String message = this.messageTextField.getText();
            if (!message.equals("")) {
                disposable.add(messageService.sendMessageToGroup(currentGroupId, new CreateMessageDto(message))
                        .observeOn(FX_SCHEDULER)
                        .doOnError(Throwable::printStackTrace)
                        .subscribe(result -> this.messageTextField.clear()));
            }
        }
    }

    private void setEventHandler(Node root) {
        root.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                sendButton.fire();
                event.consume();
            }
        });
    }

    public void setCurrentGroupId(String groupId) {
        this.currentGroupId = groupId;
    }

    public void resetOpenChatCounter() {
        this.messageService.decreaseChatCounter(Constants.MAX_LOADING_CHATS);
    }

    public App getApp() {
        return this.app;
    }

    public void setDarkMode() {
        darkMode = true;
    }

    public void setBrightMode() {
        darkMode = false;
    }
}
