package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.MapTemplate;

import de.uniks.pioneers.services.MapBrowserService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.UserService;
import javafx.event.ActionEvent;
import static de.uniks.pioneers.Constants.*;
import javafx.scene.control.Button;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import retrofit2.HttpException;

import java.util.Objects;


public class MapBrowserListElementController {
    private final HBox element;
    private final MapBrowserService mapBrowserService;
    private final MapTemplate map;
    private final UserService userService;
    private final PrefService prefService;

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
        thumbDown = new Image(Objects.requireNonNull(getClass().getResource("thumbDown.jpg")).toString());
        thumbUp = new Image(Objects.requireNonNull(getClass().getResource("thumbUp.jpg")).toString());
        voteButton.setOnAction(this::vote);
        prefService.setVoteButtonState(map._id(),  VOTED);
        voteButton.setId("VoteButtonVoted");
        voteButtonImageView.setImage(thumbDown);
        voteButton.setGraphic(voteButtonImageView);
        try{
            mapBrowserService.getVoteFromUSer(map._id(), userService.getCurrentUser()._id());
        } catch (HttpException httpException) {
            prefService.setVoteButtonState(map._id(), NOT_VOTED);
            voteButton.setId("VoteButtonNotVoted");
            voteButtonImageView.setImage(thumbUp);
            voteButton.setGraphic(voteButtonImageView);
        }
    }

    public void vote(ActionEvent actionEvent) {
        CreateVoteDto voteMove = new CreateVoteDto(1);
        if(prefService.getVoteButtonState(map._id()).equals(false)){
            mapBrowserService.vote(map._id(),voteMove);
            prefService.setVoteButtonState(map._id(), VOTED);
            voteButton.setId("VoteButtonVoted");
            voteButtonImageView.setImage(thumbDown);
            voteButton.setGraphic(voteButtonImageView);

        } else {
            mapBrowserService.deleteVote(map._id(), userService.getCurrentUser()._id());
            prefService.setVoteButtonState(map._id(), NOT_VOTED);
            voteButton.setId("VoteButtonNotVoted");
            voteButtonImageView.setImage(thumbUp);
            voteButton.setGraphic(voteButtonImageView);
        }
    }
}
