package de.uniks.pioneers.services;

import de.uniks.pioneers.model.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MessageService {

    private final List<User> chatUserList = new ArrayList<>();

    @Inject
    public MessageService() {

    }

    public List<User> getchatUserList(){
        return this.chatUserList;
    }

}
