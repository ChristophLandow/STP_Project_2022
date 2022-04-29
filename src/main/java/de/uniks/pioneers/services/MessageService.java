package de.uniks.pioneers.services;

import de.uniks.pioneers.model.User;

import javax.inject.Inject;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

public class MessageService {

    private final List<User> chatUserList = new ArrayList<>();

    @Inject
    public MessageService() {

    }

    public List<User> getchatUserList(){
        return this.chatUserList;
    }

}
