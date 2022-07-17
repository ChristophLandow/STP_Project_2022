package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Constants;
import de.uniks.pioneers.controller.ChatController;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.services.UserlistService;
import de.uniks.pioneers.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import java.util.ArrayList;

public class ChatUserlistController extends OnlineUserlistController {
    public ListView<Label> userListView;
    public ObservableList<ChatTabController> chatTabControllers = FXCollections.observableArrayList(new ArrayList<>());
    public TabPane chatTabPane;
    public ChatController chatController;

    @Inject
    public ChatUserlistController(UserService userService, MessageService messageService, UserlistService userlistService, EventListener eventListener) {
        super(userService, messageService, userlistService, eventListener);
    }

    @Override
    public void render() {
        this.userListView.setOnMouseClicked(this::openChat);

        super.render();
    }

    @Override
    public void renderUser(User user) {
        Label newUser = new Label(user.name());
        newUser.setId("newUser");
        newUser.setOnMouseClicked(this::openChat);
        this.userListView.getItems().add(newUser);
    }

    @Override
    public void removeUser(User user) {
        this.userListView.getItems().removeIf(userlable->userlable.getText().equals(user.name()));
    }

    @Override
    public void updateUser(User user) {
        Label updatedUser = new Label(user.name());
        updatedUser.setOnMouseClicked(this::openChat);
        this.userListView.getItems().replaceAll(userlabel->userlabel.getText().equals(user.name()) ? updatedUser : userlabel );
    }

    @Override
    public void openChat(MouseEvent event) {
        // Open chat of the user in the ListView who was clicked
        Label userLabel = userListView.getSelectionModel().getSelectedItem();
        User findUser = new User("","","","");
        for(User user : this.userlistService.getUsers()){
            if(user.name() != null){
                if(user.name().equals(userLabel.getText())){
                    findUser = user;
                    break;
                }
            }
        }

        final User openUser = findUser;

        if(this.messageService.userlistContains(openUser)){
            // If the chat with the user is already open, take the chat tab to the front of the TabPane
            ChatTabController chatTabController = null;
            Tab chatTab = null;

            for(ChatTabController tabController : this.chatTabControllers){
                if(tabController.chattingWith._id().equals(openUser._id())){
                    chatTabController = tabController;
                    break;
                }
            }

            for(Tab tab:this.chatTabPane.getTabs()){
                if(tab.getText().equals(openUser.name())){
                    chatTab = tab;
                    break;
                }
            }

            // If the tab is already in front in the TabPane, these lines of code are skipped
            if(this.chatTabPane.getTabs().get(this.chatTabPane.getTabs().size()-1) != chatTab){
                this.messageService.getchatUserList().removeIf(user -> user._id().equals(openUser._id()));
                this.chatTabControllers.removeIf(cm->cm.chattingWith._id().equals(openUser._id()));
                this.chatTabPane.getTabs().removeIf(tab->tab.getText().equals(openUser.name()));

                this.messageService.getchatUserList().add(openUser);
                this.chatTabControllers.add(chatTabController);
                this.chatTabPane.getTabs().add(chatTab);

                this.chatTabPane.getSelectionModel().select(chatTab);
            }
        }
        else{
            // If the chat does not exist, add the user and render the tab
            this.messageService.getchatUserList().add(openUser);
            chatController.addTab(openUser);
        }

        if(this.messageService.getchatUserList().size() > Constants.MAX_OPEN_CHATS){
            this.messageService.getchatUserList().remove(0);
            this.chatTabControllers.get(0).stop();
            this.chatTabControllers.remove(0);
            this.chatTabPane.getTabs().remove(0);
        }
    }
}
