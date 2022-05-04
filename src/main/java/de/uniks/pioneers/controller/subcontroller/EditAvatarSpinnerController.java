package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.UserService;
import javafx.scene.image.Image;
import javafx.util.Pair;

import java.util.Base64;
import java.util.function.Consumer;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class EditAvatarSpinnerController extends AvatarSpinnerController {

    private UserService userService;
    private String avatarB64;

    public EditAvatarSpinnerController(Consumer<String> changeAvatar) {
        super(changeAvatar);
    }

    @Override
    protected void initImageView() {
        // get current user's avatar and display it
        this.userService.getCurrentUser()
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    setValue(0);
                    avatarImageView.setImage(new Image(user.avatar()));
                });
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
