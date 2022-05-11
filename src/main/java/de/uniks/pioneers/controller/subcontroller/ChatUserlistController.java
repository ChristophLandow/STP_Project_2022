package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Constants;
import de.uniks.pioneers.controller.ChatController;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GroupService;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.services.UserlistService;
import de.uniks.pioneers.ws.EventListener;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import java.util.ArrayList;

public class ChatUserlistController extends OnlineUserlistController {
    public ListView<Label> userListView;
    public ArrayList<ChatTabController> chatTabControllers = new ArrayList<>();
    public TabPane chatTabPane;
    public ChatController chatController;

    @Inject
    public ChatUserlistController(UserService userService, MessageService messageService, UserlistService userlistService, EventListener eventListener){
        super(userService, messageService, userlistService, eventListener);
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
        this.userListView.getItems().removeIf(userlable->userlable.getText().equals(user.name()));
    }

    @Override
    public void updateUser(User user){
        Label updatedUser = new Label(user.name());
        updatedUser.setOnMouseClicked(this::openChat);
        this.userListView.getItems().replaceAll(userlabel->userlabel.getText().equals(user.name()) ? updatedUser : userlabel );
    }

    @Override
    public void openChat(MouseEvent event){
        Label userLabel = (Label) event.getSource();
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
