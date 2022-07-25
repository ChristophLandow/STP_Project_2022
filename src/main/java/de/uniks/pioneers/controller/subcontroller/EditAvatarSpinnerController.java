package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.image.Image;

import java.util.function.Consumer;

public class EditAvatarSpinnerController extends AvatarSpinnerController {
    private UserService userService;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public EditAvatarSpinnerController(Consumer<String> changeAvatar) {
        super(changeAvatar);
    }

    @Override
    protected void initImageView() {
        // get current user's avatar and display it
        String avatar = userService.getCurrentUser().avatar();
        setValue(0);
        if (avatar != null) {
            avatarImageView.setImage(new Image(avatar));
        } else {
            avatarImageView.setImage(null);
        }
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
