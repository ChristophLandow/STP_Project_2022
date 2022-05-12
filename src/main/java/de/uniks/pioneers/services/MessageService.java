package de.uniks.pioneers.services;

import de.uniks.pioneers.Constants;
import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.dto.UpdateMessageDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.MessageApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.ArrayList;

@Singleton
public class MessageService {

    private final List<User> chatUserList = new ArrayList<>();

    private final ObservableList<User> openChatQueue = FXCollections.observableList(new ArrayList<>());
    private final SimpleIntegerProperty openChatCounter = new SimpleIntegerProperty();

    private final MessageApiService messageApiService;

    @Inject
    public MessageService(MessageApiService messageApiService) {
        this.messageApiService = messageApiService;
    }

    public List<User> getchatUserList(){
        return this.chatUserList;
    }

    public ObservableList<User> getOpenChatQueue() {
        return this.openChatQueue;
    }

    public SimpleIntegerProperty getOpenChatCounter(){
        return this.openChatCounter;
    }

    public void decreaseChatCounter(int value){
        this.openChatCounter.set(this.openChatCounter.get()-value);

        if(this.openChatCounter.get() < 0){
            this.openChatCounter.set(0);
        }
    }

    public void increaseOpenChatCounter(){
        this.openChatCounter.set(this.openChatCounter.get()+1);

        if(this.openChatCounter.get() > Constants.OPEN_CHATS_COUNTER_MAX_VALUE){
            this.openChatCounter.set(Constants.OPEN_CHATS_COUNTER_MAX_VALUE);
        }
    }

    public boolean userlistContains(User user){
        for(User u : this.chatUserList){
            if(u._id().equals(user._id())){
                return true;
            }
        }
        return false;
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