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
}