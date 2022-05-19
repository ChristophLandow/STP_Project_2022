package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.dto.UpdateUserDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserlistServiceTest {
    @Mock
    UserService userService;

    @Mock
    EventListener eventListener;

    @InjectMocks
    UserlistService userlistService;

    @BeforeEach
    public void start() {
        MockitoAnnotations.initMocks(this);
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("1", "Karli", "online", null));
        users.add(new User("2", "Bob", "offline", null));
        System.out.println("HELLOOOO");
        when(userService.findAll()).thenReturn(Observable.just(users));
    }

    @Test
    void init() {
        System.out.println("HIIIII");
        when(userService.getCurrentUser()).thenReturn(new User("1", "Karli", "online", null));
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("1", "Karli", "online", null));
        users.add(new User("2", "Bob", "offline", null));
        System.out.println("HELLOOOO");
        when(userService.findAll()).thenReturn(Observable.just(users));

        Event<User> newEvent = new Event<>("users.3.updated", new User("3", "Alice", "online", null));
        when(eventListener.listen("users.*.*", User.class)).thenReturn(Observable.just(newEvent));

        userlistService.init();
        assertTrue(userlistService.getUsers().size() > 2);
    }

    @Test
    void getUsers() {
    }
}