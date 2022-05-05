package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.MessageApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.ArrayList;

@Singleton
public class MessageService {

    private final List<User> chatUserList = new ArrayList<>();

    private final MessageApiService messageApiService;

    @Inject
    public MessageService(MessageApiService messageApiService) {
        this.messageApiService = messageApiService;
    }

    public List<User> getchatUserList(){
        return this.chatUserList;
    }

    public void addUserToChatUserList(User user) {
        this.chatUserList.add(user);
    }

    public Observable<MessageDto> sendMessageToGroup(String id, CreateMessageDto dto) {
        return messageApiService.sendMessage("groups", id, dto);
    }

    public String getUserIdByName(String username) {
        List<User> users = getchatUserList();
        for (User user : users) {
            if (user.name() != null && user.name().equals(username)) {
                return user._id();
            }
        }
        return null;
    }
}