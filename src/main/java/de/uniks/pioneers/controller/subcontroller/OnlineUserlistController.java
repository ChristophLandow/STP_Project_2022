package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.services.UserlistService;
import de.uniks.pioneers.ws.EventListener;
import javafx.collections.ListChangeListener;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;

public class OnlineUserlistController {
    protected final UserService userService;
    protected final MessageService messageService;
    protected final UserlistService userlistService;
    protected final EventListener eventListener;

    @Inject
    public OnlineUserlistController(UserService userService, MessageService messageService, UserlistService userlistService, EventListener eventListener) {
        this.userService = userService;
        this.messageService = messageService;
        this.userlistService = userlistService;
        this.eventListener = eventListener;
    }

    public void render() {
        if (this.userlistService.getUsers().size() > 0) {
            this.userlistService.getUsers().forEach(u->{
                if (validUser(u)) {
                    if(!u._id().equals(this.userlistService.getCurrentUser()._id())){
                        renderUser(u);
                    }
                }
                else{
                    this.userlistService.getUsers().remove(u);
                }
            });
        }

        this.userlistService.getUsers().addListener((ListChangeListener<? super User>) c-> {
            c.next();
            if(c.wasAdded()){
                c.getAddedSubList().forEach(u->{
                    if (validUser(u)) {
                        if(!u._id().equals(this.userlistService.getCurrentUser()._id())){
                            renderUser(u);
                        }
                    }
                    else{
                        this.userlistService.getUsers().remove(u);
                    }
                });
            }
            else if(c.wasRemoved()) {
                c.getRemoved().forEach(this::removeUser);
            }
            else if(c.wasUpdated()){
                for(int i=c.getFrom(); i < c.getTo(); i++) {
                    if(!this.userlistService.getUsers().get(i)._id().equals(this.userlistService.getCurrentUser()._id())) {
                        updateUser(this.userlistService.getUsers().get(i));
                    }
                    else {
                        removeUser(this.userlistService.getUsers().get(i));
                    }
                }
            }
        });
    }

    public void init() {
    }

    public void renderUser(User user) {
    }

    public void removeUser(User user) {
    }

    public void updateUser(User user) {
    }

    public void openChat(MouseEvent event) {
    }

    public boolean validUser(User user) {
        return user.name() != null && user._id() != null && user.status() != null;
    }
}