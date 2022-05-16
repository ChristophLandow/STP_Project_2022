package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Constants;
import de.uniks.pioneers.controller.ChatController;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GroupService;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

import static de.uniks.pioneers.Constants.DELETE_MESSAGE_TEXT;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class ChatTabController {

    private final TabPane chatTabPane;
    private ScrollPane scrollPane;
    private VBox chatBox;
    public User chattingWith;
    public User currentUser;
    public SimpleStringProperty groupId = new SimpleStringProperty("");
    private final ChangeListener<String> changeListener = (observable, oldValue, newValue) -> initSubscription();
    private final UserService userService;
    private final GroupService groupService;
    private final MessageService messageService;
    private final EventListener eventListener;
    private final ChatController chatController;
    private final ArrayList<ChatMessage> chatMessages = new ArrayList<>();
    private final ObservableList<MessageDto> messages = FXCollections.observableArrayList();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final SimpleBooleanProperty finishedInitialization = new SimpleBooleanProperty(false);

    public ChatTabController(ChatController chatController, MessageService messageService, UserService userService, GroupService groupService,
                             TabPane chatTabPane, User chattingWith, EventListener eventListener){
        this.chatController = chatController;
        this.userService = userService;
        this.eventListener = eventListener;
        this.messageService = messageService;
        this.chatTabPane = chatTabPane;
        this.chattingWith = chattingWith;
        this.groupService = groupService;
    }

    public void render(){
        this.chatBox = new VBox();

        scrollPane = new ScrollPane(this.chatBox);
        scrollPane.setPrefHeight(579);

        chatBox.heightProperty().addListener(u->scrollPane.setVvalue(1D));

        Tab chatTab = new Tab(this.chattingWith.name(), scrollPane);
        chatTab.setOnClosed(this.chatController::removeTab);
        chatTab.setClosable(true);

        this.chatTabPane.getTabs().add(chatTab);
        this.chatTabPane.getSelectionModel().select(chatTab);

        Label loadingLabel = new Label(Constants.LOADING_CHAT_TEXT);
        loadingLabel.setPrefSize(340,263);
        loadingLabel.setAlignment(Pos.CENTER);

        this.chatBox.getChildren().add(loadingLabel);

        this.messageService.getOpenChatQueue().add(this.chattingWith);
        this.messageService.increaseOpenChatCounter();

        if(this.messageService.getOpenChatQueue().size() == 0){
            checkCounter();
        }
        else if(this.messageService.getOpenChatQueue().get(0)._id().equals(this.chattingWith._id())){
            checkCounter();
        }
        else{
            this.messageService.getOpenChatQueue().addListener((ListChangeListener<? super User>) c->{
                if(this.messageService.getOpenChatQueue().size() == 0){
                    checkCounter();
                }
                else if(this.messageService.getOpenChatQueue().get(0)._id().equals(this.chattingWith._id())){
                    checkCounter();
                }
            });
        }
    }

    public void checkCounter(){
        if(this.messageService.getOpenChatCounter().get() <= Constants.MAX_LOADING_CHATS){
            renderMessages();
        }
        else{
            this.messageService.getOpenChatCounter().addListener((observable, oldValue, newValue) -> {
                if(this.messageService.getOpenChatCounter().get() <= Constants.MAX_LOADING_CHATS){
                    renderMessages();
                }
            });
        }
    }

    public void renderMessages(){
        this.chatBox.getChildren().clear();

        this.messages.addListener((ListChangeListener<? super MessageDto>) c->{
            c.next();
            if(c.wasAdded()){
                c.getAddedSubList().forEach(this::renderMessage);
            }
            else if(c.wasRemoved()){
                c.getList().forEach(this::deleteMessage);
            }
        });

        disposable.add(this.userService.getCurrentUser()
                .take(1)
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    currentUser = user;
                    getOrCreateGroup();
                }));
    }

    public void init(){
        groupId.addListener(changeListener);
    }

    public void initSubscription(){
        if (!groupId.get().isEmpty()) {
            disposable.add(messageService.getChatMessages(groupId.get()).observeOn(FX_SCHEDULER)
                    .subscribe(this.messages::setAll));

            disposable.add(eventListener.listen("groups." + groupId.get() + ".messages.*.*", MessageDto.class)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(messageEvent -> {
                        final MessageDto message = messageEvent.data();
                        System.out.println(message);
                        if (messageEvent.event().endsWith(".created")){
                            messages.add(message);
                        }
                        else if (messageEvent.event().endsWith(".deleted")){
                            messages.removeIf(m->m._id().equals(message._id()));
                        } else if (messageEvent.event().endsWith(".updated")) {
                            messages.replaceAll(m->m.sender().equals(message.sender()) ? message : m);
                            updateMessage(message);
                        }
                    }));

            finishedInitialization();
        }
    }

    public void finishedInitialization(){
        this.messageService.getOpenChatQueue().removeIf(u->u._id().equals(chattingWith._id()));
        this.messageService.increaseOpenChatCounter();
        this.finishedInitialization.set(true);
        groupId.removeListener(changeListener);
    }

    public void renderMessage(MessageDto message){
        if(!messageAlreadyRendered(message)) {
            ChatMessage newMessage;
            if (message.sender().equals(this.chattingWith._id())) {
                newMessage = new ChatMessage(chattingWith, message, this.chatBox, this.groupId, messageService);
            } else {
                newMessage = new ChatMessage(new User(currentUser._id(), "Me", currentUser.status(), currentUser.avatar()), message, this.chatBox, this.groupId, messageService);
            }

            newMessage.render();

            if (message.body().equals(DELETE_MESSAGE_TEXT)) {
                newMessage.setMessageText(DELETE_MESSAGE_TEXT);
            }

            chatMessages.add(newMessage);
        }
    }


    public void deleteMessage(MessageDto message){
        chatMessages.removeIf(m->{
            if(m.getMessageID().equals(message._id())){
                m.stop();
                return true;
            }
            return false;
        });
    }

    public void updateMessage(MessageDto message){
        for(ChatMessage cm : chatMessages){
            if(cm.getMessageID().equals(message._id()) && message.body().equals(DELETE_MESSAGE_TEXT)){
                cm.setMessageText(DELETE_MESSAGE_TEXT);
            }
        }
    }

    public boolean messageAlreadyRendered(MessageDto message){
        for(ChatMessage cm : chatMessages){
            if(cm.getMessageID().equals(message._id())){
                return true;
            }
        }
        return false;
    }

    public void getOrCreateGroup() {
        disposable.add(groupService.getGroupsWithUser(chattingWith._id())
                .observeOn(FX_SCHEDULER)
                .doOnError(Throwable::printStackTrace)
                .subscribe(res -> {
                    if (res.size() != 0 && res.get(0) != null && res.get(0)._id() != null) {
                        groupId.set(res.get(0)._id());
                    } else {
                        disposable.add(groupService.createNewGroupWithOtherUser(chattingWith._id())
                                .observeOn(FX_SCHEDULER)
                                .doOnError(Throwable::printStackTrace)
                                .subscribe(result -> groupId.set(result._id()), Throwable::printStackTrace));
                    }
                }, Throwable::printStackTrace));
    }

    public void stop(){
        disposable.dispose();
        this.messageService.getOpenChatQueue().removeIf(u->u._id().equals(chattingWith._id()));
        this.chatMessages.clear();
    }

    public SimpleBooleanProperty getFinishedInitialization(){
        return this.finishedInitialization;
    }

}
