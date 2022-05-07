package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.dto.UpdateMessageDto;
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

    public Observable<List<MessageDto>> getChatMessages(String id){
        return messageApiService.getChatMessages("groups", id);
    }

    public Observable<MessageDto> updateMessage(String namespace, String parent, String id, UpdateMessageDto message) {
        return messageApiService.updateMessage(namespace, parent, id, message);
    }

}