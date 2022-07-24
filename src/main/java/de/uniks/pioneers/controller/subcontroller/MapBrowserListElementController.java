package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.services.MapBrowserService;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import retrofit2.HttpException;

import java.io.IOException;
import java.util.Objects;

public class MapBrowserListElementController {
    private final HBox element;
    private final MapBrowserService mapBrowserService;
    private final MapTemplate map;

    public MapBrowserListElementController(MapBrowserService mapBrowserService,MapTemplate map, HBox element){
        this.element = element;
        this.map = map;
        this.mapBrowserService = mapBrowserService;
    }

    public void init(){
        Button voteButton = (Button) element.lookup("#VoteButton");
        voteButton.setOnAction(this::vote);
    }

    public void vote(ActionEvent actionEvent) {
        CreateVoteDto voteMove = new CreateVoteDto(1);
        mapBrowserService.vote(map._id(),voteMove);
        for(Node n : element.getChildren()) {
            if (n.getId().equals("VotingLabel")) {
                ((Label) n).setText(Integer.toString(mapBrowserService.getActualScore()));
            }
        }
    }
}
