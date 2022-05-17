package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.services.UserService;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EditProfileControllerTest extends ApplicationTest {

    @Mock
    UserService userService;

    @InjectMocks
    EditProfileController editProfileController;

    @Override
    public void start(Stage stage) throws Exception {
        new App(editProfileController).start(stage);
    }

    @Test
    void editUsername() {
        doNothing().when(userService).editProfile(anyString(), anyString(), anyString(), anyString());

        // test leaving without changing anything
        FxAssert.verifyThat("#saveLeaveButton", NodeMatchers.isEnabled());

        // edit username
        write("Alice\t");
        FxAssert.verifyThat("#saveLeaveButton", NodeMatchers.isEnabled());

        // edit password
        write("password\t");
        FxAssert.verifyThat("#saveLeaveButton", NodeMatchers.isDisabled());
        FxAssert.verifyThat("#newPasswordStatusText", LabeledMatchers.hasText("Password must be at least 8 characters long"));

        write("12345678\t");
        FxAssert.verifyThat("#saveLeaveButton", NodeMatchers.isDisabled());
        FxAssert.verifyThat("#newPasswordStatusText", LabeledMatchers.hasText("Passwords do not match"));

        write("12345678\t");
        FxAssert.verifyThat("#saveLeaveButton", NodeMatchers.isEnabled());
        FxAssert.verifyThat("#newPasswordStatusText", LabeledMatchers.hasText(""));

        // edit avatar
        type(KeyCode.UP);
        FxAssert.verifyThat("#saveLeaveButton", NodeMatchers.isEnabled());

        // save and leave
        write("\t");
        type(KeyCode.SPACE);

        verify(userService).editProfile("Alice", anyString(), "12345678", anyString());
    }

}