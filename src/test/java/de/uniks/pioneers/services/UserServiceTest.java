package de.uniks.pioneers.services;

import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserApiService userApiService;

    @InjectMocks
    UserService userService;

    @Test
    void getCurrentUser() {
    }

    @Test
    void editProfile() {
        when(userApiService.update(any(), any())).thenReturn(Observable.just(new User("1", "n", "online", null)));

        final User result = userService.editProfile("n", null, null, "online").blockingFirst();
    }
}
