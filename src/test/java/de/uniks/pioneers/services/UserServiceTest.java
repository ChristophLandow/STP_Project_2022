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
    void register() {

        when(userApiService.create(any())).thenReturn(Observable.just(new User("000", "UserServiceTestUser","online","")));

        final User user = userService.register("LoginServiceTestUser","","12345678").blockingFirst();

        assertEquals(user._id(), "000");
        assertEquals(user.name(), "UserServiceTestUser");
        assertEquals(user.status(), "online");
        assertEquals(user.avatar(), "");
    }
    @Test
    void editProfile() {
        when(userApiService.update(any(), any())).thenReturn(Observable.just(new User("1", "newName", "online", null)));
        userService.setCurrentUser(new User("1", "name", "online", null));

        final User result = userService.editProfile("newName", null, null, null).blockingFirst();
        assertEquals("newName", result.name());

        verify(userApiService).update("1", new UpdateUserDto("newName", null, null, null));
    }
    @Test
    void findAll() {

        when(userApiService.findAll()).thenReturn(Observable.just(List.of(
                new User("000", "UserServiceTestUser1","online",""),
                new User("111", "UserServiceTestUser2","online",""),
                new User("222", "UserServiceTestUser3","offline","")
        )));

        final List<User> list = userService.findAll().blockingFirst();

        assertEquals(list.get(0)._id(), "000");
        assertEquals(list.get(0).name(), "UserServiceTestUser1");
        assertEquals(list.get(0).status(), "online");

        assertEquals(list.get(1)._id(), "111");
        assertEquals(list.get(1).name(), "UserServiceTestUser2");
        assertEquals(list.get(1).status(), "online");

        assertEquals(list.get(2)._id(), "222");
        assertEquals(list.get(2).name(), "UserServiceTestUser3");
        assertEquals(list.get(2).status(), "offline");
    }


    @Test
    void getUserById() {
        when(userApiService.getUser(any())).thenReturn(Observable.just(new User("1", "Alice", "online", null)));

        final String name = userService.getUserById("1").blockingFirst().name();
        assertEquals("Alice", name);

        verify(userApiService).getUser("1");
    }
}