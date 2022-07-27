package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.services.MapBrowserService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Objects;

import static de.uniks.pioneers.Constants.NOT_VOTED;
import static de.uniks.pioneers.Constants.VOTED;


public class MapBrowserListElementController {
    private final HBox element;
    private final MapBrowserService mapBrowserService;
    private final MapTemplate map;
    private final UserService userService;
    private final PrefService prefService;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private Button voteButton;
    private ImageView voteButtonImageView;
    private Image thumbUp;
    private Image thumbDown;


    public MapBrowserListElementController(PrefService prefService, UserService userService, MapBrowserService mapBrowserService, MapTemplate map, HBox element){
        this.element = element;
        this.map = map;
        this.mapBrowserService = mapBrowserService;
        this.userService = userService;
        this.prefService = prefService;
    }

    public void init(){
        voteButton = (Button) element.lookup("#VoteButton");
        voteButtonImageView = new ImageView();
        voteButtonImageView.setFitHeight(30);
        voteButtonImageView.setFitWidth(30);
        Image trash = new Image(Objects.requireNonNull(getClass().getResource("trash.png")).toString());
        thumbDown = new Image(Objects.requireNonNull(getClass().getResource("ThumbUp_NotFilled.png")).toString());
        thumbUp = new Image(Objects.requireNonNull(getClass().getResource("ThumbUp_Filled.png")).toString());
        
        voteButton.setOnAction(this::vote);
        if (this.map.createdBy().equals(userService.getCurrentUser()._id())) {
            voteButtonImageView.setImage(trash);
            voteButton.setOnAction(this::delete);
        } else if (prefService.getVoteButtonState(map._id())) {
            voteButtonImageView.setImage(thumbUp);
        } else {
            voteButtonImageView.setImage(thumbDown);
        }
        voteButton.setGraphic(voteButtonImageView);
    }

    private void delete(ActionEvent actionEvent) {
        disposable.add(mapBrowserService.deleteMap(map._id())
                .subscribe());
    }

    public void vote(ActionEvent actionEvent) {
        CreateVoteDto voteMove = new CreateVoteDto(1);
        if(prefService.getVoteButtonState(map._id()).equals(false)){
            mapBrowserService.vote(map._id(),voteMove);
            prefService.setVoteButtonState(map._id(), VOTED);
            voteButtonImageView.setImage(thumbUp);
            voteButton.setGraphic(voteButtonImageView);

        } else {
            mapBrowserService.deleteVote(map._id(), userService.getCurrentUser()._id());
            prefService.setVoteButtonState(map._id(), NOT_VOTED);
            voteButtonImageView.setImage(thumbDown);
            voteButton.setGraphic(voteButtonImageView);
        }
    }
}
