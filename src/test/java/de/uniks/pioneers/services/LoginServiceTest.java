package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.dto.RefreshDto;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.rest.AuthApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Spy
    TokenStorage tokenStorage;
    @Mock
    UserService userService;
    @Mock
    AuthApiService authApiService;
    @Mock
    PrefService prefService;
    @Mock
    RefreshService refreshService;
    @InjectMocks
    LoginService loginService;

    @Test
    void login() {

        when(authApiService.login(any())).thenReturn(
                Observable.just(new LoginResult("000", "LoginServiceTestUser","online","","123","456")));

        final LoginResult loginResult = loginService.login("LoginServiceTestUser","12345678").blockingFirst();

        assertEquals(loginResult._id(), "000");
        assertEquals(loginResult.name(), "LoginServiceTestUser");
        assertEquals(loginResult.status(), "online");
        assertEquals(loginResult.avatar(), "");
        assertEquals(loginResult.accessToken(), "123");
        assertEquals(loginResult.refreshToken(), "456");

        assertEquals(tokenStorage.getAccessToken(), "123");
        assertEquals(tokenStorage.getRefreshToken(), "456");

        verify(authApiService).login(new LoginDto("LoginServiceTestUser","12345678"));
        verify(userService, atLeastOnce()).setCurrentUser(any());
        verify(refreshService, atLeastOnce()).startRefreshCycle();

    }

    @Test
    void refresh() {

        when(prefService.recall()).thenReturn("abc");
        when(authApiService.refresh(any())).thenReturn(
                Observable.just(new LoginResult("000", "LoginServiceTestUser","online","","123","456")));

        final LoginResult loginResult = loginService.refresh().blockingFirst();

        assertEquals(loginResult._id(), "000");
        assertEquals(loginResult.name(), "LoginServiceTestUser");
        assertEquals(loginResult.status(), "online");
        assertEquals(loginResult.avatar(), "");
        assertEquals(loginResult.accessToken(), "123");
        assertEquals(loginResult.refreshToken(), "456");

        assertEquals(tokenStorage.getAccessToken(), "123");
        assertEquals(tokenStorage.getRefreshToken(), "456");

        verify(authApiService).refresh(new RefreshDto("abc"));

    }
}