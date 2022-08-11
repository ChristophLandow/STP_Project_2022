package de.uniks.pioneers.controller.PopUpController.ElementController;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Objects;


import static de.uniks.pioneers.Constants.FX_SCHEDULER;


public class TradePopUpPlayerListElementController {

    private final CompositeDisposable disposable = new CompositeDisposable();
    private final UserService userService;
    private final IngameService ingameService;

    @FXML
    public Label playerNameLabel;
    @FXML
    public ImageView playerAvatar;
    @FXML
    public ImageView acceptedMark;

    @Inject
    public TradePopUpPlayerListElementController(UserService userService, IngameService ingameService) {
        this.userService = userService;
        this.ingameService = ingameService;
    }

    public Parent render() {
        Parent node = null;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/PopUps/PopUpElements/TradePopUpPlayerListElement.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return node;
    }

    public void init(String userId) {
        disposable.add(userService.getUserById(userId)
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    if (!user.avatar().equals("")) {
                        playerAvatar.setImage(new Image(user.avatar()));
                    }
                    playerNameLabel.setText(user.name());
                    acceptedMark.setId(userId);
                })
        );
    }

    public void stop() {
        disposable.dispose();
    }

    public void displayAcceptedMark() {
        final String resourceURL = "/de/uniks/pioneers/controller/subcontroller/images/trade_accepted.png";
        final Image img = new Image(Objects.requireNonNull(getClass().getResource(resourceURL)).toString());
        acceptedMark.setImage(img);
    }

    public void setCheckmarkAction() {
        if (this.acceptedMark.getImage() != null) {
            this.acceptedMark.setOnMouseClicked(event -> {
                ingameService.acceptPartner(acceptedMark.getId());
                ingameService.tradeAccepted.set(true);
            });
        }
    }
}
