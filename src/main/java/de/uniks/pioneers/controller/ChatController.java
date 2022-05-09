package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.ChatTabController;
import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GroupService;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;

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
    @FXML public ListView userListView;
    @FXML public TextField messageTextField;
    @FXML public TabPane chatTabPane;

    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;

    private final ArrayList<ChatTabController> chatTabControllers = new ArrayList<>();
    private String currentGroupId;

    private final ObservableList<User> users = FXCollections.observableArrayList();

    private User currentUser = new User("","","","");

    @Inject
    public ChatController(App app, MessageService messageService, UserService userService, EventListener eventListener,
                          GroupService groupService, Provider<LobbyScreenController> lobbyScreenControllerProvider) {
        this.app = app;
        this.messageService = messageService;
        this.userService = userService;
        this.groupService = groupService;
        this.eventListener = eventListener;
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

        disposable.add(this.userService.getCurrentUser()
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> currentUser = user));

        users.addListener((ListChangeListener<? super User>) c->{
            c.next();
            if(c.wasAdded()){
                c.getAddedSubList().forEach(u->{
                    if(u.name() != null){
                        if(!u._id().equals(this.currentUser._id())){
                            renderUser(u);
                        }
                    }
                });
            }
            else if(c.wasRemoved()){
                c.getRemoved().forEach(this::removeUser);
            }
            else if(c.wasUpdated()){
                for(int i=c.getFrom(); i < c.getTo(); i++){
                    if(!users.get(i)._id().equals(this.currentUser._id())){
                        updateUser(users.get(i));
                    }
                    else{
                        removeUser(users.get(i));
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void init() {
        app.getStage().setTitle(CHAT_SCREEN_TITLE);

        disposable.add(userService.findAll().observeOn(FX_SCHEDULER)
                .subscribe(this.users::setAll));

        disposable.add(eventListener.listen("users.*.*", User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(userEvent -> {
                    final User user = userEvent.data();
                    if (userEvent.event().endsWith(".created") && user.status().equals("online")){
                        users.add(user);
                    }
                    else if (userEvent.event().endsWith(".deleted")){
                        users.removeIf(u->u._id().equals(user._id()));
                    }
                    else if(userEvent.event().endsWith(".updated")){
                        if(user.status().equals("online")){
                            users.removeIf(u->u._id().equals(user._id()));
                            users.add(user);
                        }
                        else{
                            users.removeIf(u->u._id().equals(user._id()));
                        }
                    }
                }));
    }


    @Override
    public void stop() {
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
        this.chatTabControllers.removeIf(c->c.chattingWith.name().equals(closedTab.getText()));
    }

    public void leave(ActionEvent event) {
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

    public void renderUser(User user){
        Label newUser = new Label(user.name());
        newUser.setOnMouseClicked(this::openChat);
        this.userListView.getItems().add(newUser);
    }

    public void removeUser(User user){
        this.userListView.getItems().removeIf(userlable->((Label) userlable).getText().equals(user.name()));
    }

    public void updateUser(User user){
        Label updatedUser = new Label(user.name());
        updatedUser.setOnMouseClicked(this::openChat);
        this.userListView.getItems().replaceAll(userlabel->((Label) userlabel).getText().equals(user.name()) ? updatedUser : userlabel );
    }

    public void openChat(MouseEvent event){
        Label userLabel = (Label) event.getSource();
        User findUser = new User("","","","");
        for(User user : this.users){
            if(user.name() != null){
                if(user.name().equals(userLabel.getText())){
                    findUser = user;
                    break;
                }
            }
        }

        final User openUser = findUser;
        this.messageService.getchatUserList().removeIf(user -> user._id().equals(openUser._id()));
        this.chatTabControllers.removeIf(cm->cm.chattingWith._id().equals(openUser._id()));
        this.chatTabPane.getTabs().removeIf(tab->tab.getText().equals(openUser.name()));
        this.messageService.getchatUserList().add(openUser);
        addTab(openUser);
    }

}
