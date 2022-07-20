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
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    PrefService prefService;

    @InjectMocks
    ChatController chatController;

    @Override
    public void start(Stage stage) {
        ObservableList<User> userList = new ObservableList<>() {
            @Override
            public void addListener(ListChangeListener<? super User> listener) {

            }

            @Override
            public void removeListener(ListChangeListener<? super User> listener) {

            }

            @Override
            public boolean addAll(User... elements) {
                return false;
            }

            @Override
            public boolean setAll(User... elements) {
                return false;
            }

            @Override
            public boolean setAll(Collection<? extends User> col) {
                return false;
            }

            @Override
            public boolean removeAll(User... elements) {
                return false;
            }

            @Override
            public boolean retainAll(User... elements) {
                return false;
            }

            @Override
            public void remove(int from, int to) {

            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public @NonNull Iterator<User> iterator() {
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public User next() {
                        return null;
                    }
                };
            }


            @Override
            public Object @NonNull [] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T @NonNull [] a) {
                return null;
            }

            @Override
            public boolean add(User user) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends User> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, @NonNull Collection<? extends User> c) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public User get(int index) {
                return null;
            }

            @Override
            public User set(int index, User element) {
                return null;
            }

            @Override
            public void add(int index, User element) {

            }

            @Override
            public User remove(int index) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @Override
            public ListIterator<User> listIterator() {
                return null;
            }

            @Override
            public ListIterator<User> listIterator(int index) {
                return null;
            }

            @Override
            public List<User> subList(int fromIndex, int toIndex) {
                return null;
            }

            @Override
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }
        };
        when(userlistService.getUsers()).thenReturn(userList);
        when(userlistControllerProvider.get()).thenReturn(new ChatUserlistController(userService, messageService, userlistService, eventListener));
        chatTabController.chattingWith = new User("123", "Tom", "online", null);
        app.start(stage);
        app.show(chatController);
    }

    @Test
    void send() {
        chatTabController.chattingWith = new User("123", "Tom", "online", null);
        when(messageService.sendMessageToGroup(anyString(), any())).thenReturn(Observable.just(new MessageDto("yesterday", "now", "1234", "me", "Test!")));
        chatController.setCurrentGroupId("1234");
        chatController.messageTextField.setText("Test!");
        chatController.send(new ActionEvent());
        verify(messageService).sendMessageToGroup(anyString(), any());
    }

    @Test
    void addTab() {
        User testUser = new User("1", "Steve", "online", null);
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