package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.ChatController;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GroupService;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import java.util.ArrayList;

public class ChatUserlistController extends OnlineUserlistController {
    public ListView userListView;
    public ArrayList<ChatTabController> chatTabControllers = new ArrayList<>();
    public TabPane chatTabPane;
    public ChatController chatController;

    @Inject
    public ChatUserlistController(UserService userService, MessageService messageService, EventListener eventListener){
        super(userService, messageService, eventListener);
    }

    @Override
    public void render(){
        this.chatTabPane.getTabs().remove(0);

        for(User u: this.messageService.getchatUserList()){
            chatController.addTab(u);
        }

        super.render();
    }

    @Override
    public void renderUser(User user){
        Label newUser = new Label(user.name());
        newUser.setOnMouseClicked(this::openChat);
        this.userListView.getItems().add(newUser);
    }

    @Override
    public void removeUser(User user){
        this.userListView.getItems().removeIf(userlable->((Label) userlable).getText().equals(user.name()));
    }

    @Override
    public void updateUser(User user){
        Label updatedUser = new Label(user.name());
        updatedUser.setOnMouseClicked(this::openChat);
        this.userListView.getItems().replaceAll(userlabel->((Label) userlabel).getText().equals(user.name()) ? updatedUser : userlabel );
    }

    @Override
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
        chatController.addTab(openUser);
    }
}
