package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@Singleton
public class UserlistService {

    protected final EventListener eventListener;
    protected final UserService userService;

    private User currentUser = new User("","","","");
    private ObservableList<User> users = FXCollections.observableArrayList();

    @Inject
    public UserlistService(EventListener eventListener, UserService userService) {
        this.eventListener = eventListener;
        this.userService = userService;

        init();
    }

    public void init() {
        this.currentUser = userService.getCurrentUser();
        users.removeIf(u->u._id().equals(currentUser._id()));

        userService.findAll().observeOn(FX_SCHEDULER)
                .subscribe(this.users::setAll);

        System.out.println("users.*.*");
        eventListener.listen("users.*.*", User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(userEvent -> {
                    final User user = userEvent.data();
                    if(validUser(user)) {
                        if (userEvent.event().endsWith(".created") && user.status().equals("online") && !user._id().equals(currentUser._id())) {
                            users.add(user);
                        } else if (userEvent.event().endsWith(".deleted")) {
                            users.removeIf(u -> u._id().equals(user._id()));
                        } else if (userEvent.event().endsWith(".updated")) {
                            if (user.status().equals("online")) {
                                if(!user._id().equals(currentUser._id())) {
                                    users.removeIf(u -> u._id().equals(user._id()));
                                    users.add(user);
                                }
                            } else {
                                users.removeIf(u -> u._id().equals(user._id()));
                            }
                        }
                    }
                });
    }

    public boolean validUser(User user){
        if(user.name() == null || user._id() == null || user.status() == null) {
            return false;
        }

        return true;
    }

    public ObservableList<User> getUsers() {
        return this.users;
    }

    public User getCurrentUser() {
        return currentUser;
    }

}
