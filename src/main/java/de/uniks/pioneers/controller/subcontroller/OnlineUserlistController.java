package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class OnlineUserlistController {
    protected final UserService userService;
    protected final MessageService messageService;
    protected final EventListener eventListener;
    protected ObservableList<User> users = FXCollections.observableArrayList();
    private User currentUser = new User("","","","");

    @Inject
    public OnlineUserlistController(UserService userService, MessageService messageService, EventListener eventListener){
        this.userService = userService;
        this.messageService = messageService;
        this.eventListener = eventListener;
    }

    public void render(){
        this.userService.getCurrentUser()
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    this.currentUser = user;
                    removeUser(user);
                });

        users.addListener((ListChangeListener<? super User>) c->{
            c.next();
            if(c.wasAdded()){
                c.getAddedSubList().forEach(u->{
                    if (validUser(u)) {
                        if(!u._id().equals(this.currentUser._id())){
                            renderUser(u);
                        }
                    }
                    else{
                        users.remove(u);
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
    }

    public void init(){
        userService.findAll().observeOn(FX_SCHEDULER)
                .subscribe(this.users::setAll);

        eventListener.listen("users.*.*", User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(userEvent -> {
                    final User user = userEvent.data();
                    if(validUser(user)) {
                        if (userEvent.event().endsWith(".created") && user.status().equals("online")) {
                            users.add(user);
                        } else if (userEvent.event().endsWith(".deleted")) {
                            users.removeIf(u -> u._id().equals(user._id()));
                        } else if (userEvent.event().endsWith(".updated")) {
                            if (user.status().equals("online")) {
                                users.removeIf(u -> u._id().equals(user._id()));
                                users.add(user);
                            } else {
                                users.removeIf(u -> u._id().equals(user._id()));
                            }
                        }
                    }
                });
    }

    public void renderUser(User user){

    }

    public void removeUser(User user){

    }

    public void updateUser(User user){

    }

    public void openChat(MouseEvent event){

    }

    public boolean validUser(User user){
        if(user.name() == null || user._id() == null || user.status() == null){
            return false;
        }

        return true;
    }
}