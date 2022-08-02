package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.ChatTabController;
import de.uniks.pioneers.controller.subcontroller.ChatUserlistController;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.dto.GroupDto;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.*;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock
    Provider<ChatUserlistController> userlistControllerProvider;

    @Mock
    MessageService messageService;

    @Mock
    UserService userService;

    @Mock
    StylesService stylesService;

    @Mock
    GroupService groupService;

    @Mock
    UserlistService userlistService;

    @Mock
    EventListener eventListener;

    @Mock
    ChatTabController chatTabController;

    @Mock
    EventHandlerService eventHandlerService;

    @InjectMocks
    ChatController chatController;

    @Override
    public void start(Stage stage) {
        ObservableList<User> userList = FXCollections.observableArrayList();
        when(userlistService.getUsers()).thenReturn(userList);
        when(userlistControllerProvider.get()).thenReturn(new ChatUserlistController(userService, messageService, userlistService, eventListener));
        chatTabController.chattingWith = new User("123", "Tom", "online", null);
        app.start(stage);
        app.show(chatController);
        verify(eventHandlerService, atLeastOnce()).setEnterEventHandler(any(), any());
        verify(stylesService, atLeastOnce()).setStyleSheets(any());
    }

    @Test
    void send() {
        chatTabController.chattingWith = new User("123", "Tom", "online", null);
        when(messageService.sendMessageToGroup(anyString(), any())).thenReturn(Observable.just(new MessageDto("yesterday", "now", "1234", "me", "Test!")));
        chatController.setCurrentGroupId("1234");
        chatController.messageTextField.setText("Test!");
        chatController.send();
        verify(messageService).sendMessageToGroup(anyString(), any());
    }

    @Test
    void addTab() {
        User testUser = new User("1", "Steve", "online", null);
        when(userlistService.getCurrentUser()).thenReturn(testUser);
        ObservableList<User> userList = userlistService.getUsers();
        userList.add(testUser);
        when(messageService.getOpenChatQueue()).thenReturn(userList);
        when(messageService.getOpenChatCounter()).thenReturn(new SimpleIntegerProperty(2));
        List<GroupDto> groupList = new ArrayList<>();
        List<String> memberList = new ArrayList<>();
        List<MessageDto> messageList = new ArrayList<>();
        memberList.add("1");
        memberList.add("2");
        messageList.add(new MessageDto("yesterday", "yesterday", "1", "1", "Test!"));
        groupList.add(new GroupDto("yesterday", "now", "1234", memberList, "Test"));
        when(groupService.getGroupsWithUser(anyString())).thenReturn(Observable.just(groupList));
        when(messageService.getChatMessages(anyString())).thenReturn(Observable.just(messageList));
        when(eventListener.listen(anyString(), any())).thenReturn(Observable.just(new Event<>("Test", new MessageDto("yesterday", "yesterday", "1", "1", "Test!"))));
        Platform.runLater(() -> {
            chatController.addTab(testUser);
            verify(groupService).getGroupsWithUser("1");
        });
    }

}