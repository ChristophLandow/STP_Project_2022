package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Constants;
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
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock(name = "userlistControllerProvider")
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
    ObservableList<User> userList = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {

        when(userlistService.getUsers()).thenReturn(userList);
        when(userlistControllerProvider.get()).thenReturn(new ChatUserlistController(userService, messageService, userlistService, eventListener));
        chatTabController.chattingWith = new User("123", "Tom", "online", null);
        app.start(stage);
        app.show(chatController);
        stage.centerOnScreen();
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

        messageList.add(new MessageDto("","","2","1","Hello"));
        chatTabController.updateMessage(new MessageDto("","","2","1","Hello"));
    }

    @Test
    void openChatFromUserlist() {
        SimpleIntegerProperty chatCounter = new SimpleIntegerProperty(Constants.OPEN_CHATS_COUNTER_MAX_VALUE+10);
        ObservableList<User> chatQueue = FXCollections.observableArrayList();

        User testUser = new User("1", "Steve", "online", null);
        when(userlistService.getCurrentUser()).thenReturn(testUser);

        userList.add(testUser);
        userList.add(chatTabController.chattingWith);

        WaitForAsyncUtils.waitForFxEvents();

        when(messageService.getOpenChatQueue()).thenReturn(chatQueue);
        when(messageService.getOpenChatCounter()).thenReturn(chatCounter);

        clickOn("#newUser");

        TabPane chatTabPane = lookup("#chatTabPane").query();

        assertNotEquals(chatTabPane.getTabs().size(), 0);
        assertEquals(chatTabPane.getTabs().get(0).getText(), "Tom");

        //Check removing of tab
        ArrayList<User> tooManyOpenTabsList = new ArrayList();
        tooManyOpenTabsList.add(new User("1","","",""));
        tooManyOpenTabsList.add(new User("2","","",""));
        tooManyOpenTabsList.add(new User("3","","",""));
        tooManyOpenTabsList.add(new User("4","","",""));
        tooManyOpenTabsList.add(new User("5","","",""));
        when(messageService.getchatUserList()).thenReturn(tooManyOpenTabsList);

        clickOn("#newUser");
        assertTrue(chatTabPane.getTabs().size() <= Constants.MAX_OPEN_CHATS);
        clickOn("#newUser");
        assertTrue(chatTabPane.getTabs().size() <= Constants.MAX_OPEN_CHATS);
        clickOn("#newUser");
        assertTrue(chatTabPane.getTabs().size() <= Constants.MAX_OPEN_CHATS);

        //Check opening a tab that already exists
        when(messageService.userlistContains(any())).thenReturn(true);
        when(messageService.getchatUserList()).thenReturn(new ArrayList<>());

        clickOn("#newUser");
        assertEquals(chatTabPane.getTabs().size(), 1);
        clickOn("#newUser");
        assertEquals(chatTabPane.getTabs().size(), 1);
        clickOn("#newUser");
        assertEquals(chatTabPane.getTabs().size(), 1);

        //close tab
        clickOn(".tab-pane > .tab-header-area > .headers-region > .tab .tab-close-button");
        assertEquals(chatTabPane.getTabs().size(), 0);
    }

}