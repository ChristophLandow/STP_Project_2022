package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.LoginService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextMatchers;

import javax.inject.Provider;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditProfileControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock
    UserService userService;

    @Mock
    LoginService loginService;

    @InjectMocks
    EditProfileController editProfileController;

    @Override
    public void start(Stage stage) throws Exception {
        when(userService.getCurrentUser()).thenReturn(Observable.just(new User("1", "Alice", "online", null)));
        app.start(stage);
        app.show(editProfileController);
    }

    @Test
    void editUsername() {
        String newAvatar = "data:image/png;base64,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";
        when(loginService.checkPassword("Alice", "password")).thenReturn(new LoginResult("1", "Alice", "online", null, null, null));
        when(userService.editProfile("Alice", newAvatar, "12345678", null)).thenReturn(Observable.just(new User("1", "Alice", "online", newAvatar)));

        // test leaving without changing anything
        FxAssert.verifyThat("#saveLeaveButton", NodeMatchers.isEnabled());

        // edit username
        write("Alice\t");
        FxAssert.verifyThat("#saveLeaveButton", NodeMatchers.isEnabled());

        // edit password
        write("password\t");
        FxAssert.verifyThat("#saveLeaveButton", NodeMatchers.isDisabled());
        FxAssert.verifyThat("#newPasswordStatusText", TextMatchers.hasText("Password must be at least 8 characters long"));

        write("12345678\t");
        FxAssert.verifyThat("#saveLeaveButton", NodeMatchers.isDisabled());
        FxAssert.verifyThat("#newPasswordStatusText", TextMatchers.hasText("Passwords do not match"));

        write("12345678\t");
        FxAssert.verifyThat("#saveLeaveButton", NodeMatchers.isEnabled());
        FxAssert.verifyThat("#newPasswordStatusText", TextMatchers.hasText(""));

        // edit avatar
        type(KeyCode.UP);
        FxAssert.verifyThat("#saveLeaveButton", NodeMatchers.isEnabled());

        // save and leave
        write("\t");
        type(KeyCode.SPACE);

        verify(loginService).checkPassword("Alice", "password");
        verify(userService).editProfile("Alice", newAvatar, "12345678", null);
    }

}