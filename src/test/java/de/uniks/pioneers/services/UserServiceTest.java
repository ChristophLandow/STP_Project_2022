package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.UpdateUserDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserApiService userApiService;

    @InjectMocks
    UserService userService;

    @Test
    void editProfile() {
        when(userApiService.update(any(), any())).thenReturn(Observable.just(new User("1", "newName", "online", null)));
        userService.setCurrentUser(new User("1", "name", "online", null));

        final User result = userService.editProfile("newName", null, null, null).blockingFirst();
        assertEquals("newName", result.name());

        verify(userApiService).update("1", new UpdateUserDto("newName", null, null, null));
    }

    @Test
    void getUserById() {
        when(userApiService.getUser(any())).thenReturn(Observable.just(new User("1", "Alice", "online", null)));

        final String name = userService.getUserById("1").blockingFirst().name();
        assertEquals("Alice", name);

        verify(userApiService).getUser("1");
    }

    @Test
    void findAll() {
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Alice", "online", null));
        users.add(new User("2", "Bob", "offline", null));
        users.add(new User("3", "Karli", "offline", null));
        when(userApiService.findAll()).thenReturn(Observable.just(users));

        final String result = userService.findAll().blockingFirst().get(2)._id();
        assertEquals("3", result);

        verify(userApiService).findAll();
    }
}