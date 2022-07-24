package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.MapTemplate;

import de.uniks.pioneers.model.Vote;
import de.uniks.pioneers.services.MapBrowserService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.UserService;
import javafx.event.ActionEvent;
import static de.uniks.pioneers.Constants.*;
import javafx.scene.control.Button;

import javafx.scene.layout.HBox;
import retrofit2.HttpException;

import java.io.IOException;
import java.util.Objects;


public class MapBrowserListElementController {
    private final HBox element;
    private final MapBrowserService mapBrowserService;
    private final MapTemplate map;
    private final UserService userService;
    private final PrefService prefService;


    public MapBrowserListElementController(PrefService prefService, UserService userService, MapBrowserService mapBrowserService, MapTemplate map, HBox element){
        this.element = element;
        this.map = map;
        this.mapBrowserService = mapBrowserService;
        this.userService = userService;
        this.prefService = prefService;
    }

    public void init(){
        Button voteButton = (Button) element.lookup("#VoteButton");
        voteButton.setOnAction(this::vote);
        try{
            mapBrowserService.getVoteFromUSer(map._id(), userService.getCurrentUser()._id());
        } catch (HttpException httpException) {
            prefService.setVoteButtonState(map._id(), NOT_VOTED);
        }
    }

    public void vote(ActionEvent actionEvent) {
        CreateVoteDto voteMove = new CreateVoteDto(1);
        if(prefService.getVoteButtonState(map._id()).equals(false)){
            mapBrowserService.vote(map._id(),voteMove);
            prefService.setVoteButtonState(map._id(),  VOTED);
        } else {
            mapBrowserService.deleteVote(map._id(), userService.getCurrentUser()._id());
            prefService.setVoteButtonState(map._id(),  NOT_VOTED);
        }



        //

    }
    public void testPrint(Vote vote){
        System.out.println(vote);
    }

    private  void handleHttpError(Throwable exception) throws IOException {
        String errorBody;
        if (exception instanceof HttpException httpException) {
            errorBody = Objects.requireNonNull(Objects.requireNonNull(httpException.response()).errorBody()).string();
        } else {
            return;
        }
        System.out.println("!!!An Http Error appeared!!!\n" + errorBody);
    }
}
