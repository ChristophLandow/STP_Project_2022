package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javax.inject.Inject;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class GameChatController {
    public ScrollPane chatScrollPane;
    public VBox messageBox;
    public TextField messageText;
    public Button sendButton;
    private final EventListener eventListener;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final ObservableList<MessageDto> messages = FXCollections.observableArrayList();
    private final GameService gameService;
    public Game game;

    @Inject
    GameChatController(GameService gameService, EventListener eventListener){
        this.gameService = gameService;
        this.eventListener = eventListener;
    }

    public void render(){
        messageBox.heightProperty().addListener(u->chatScrollPane.setVvalue(1));

        sendButton.setOnAction(this::sendMessage);

        this.messages.addListener((ListChangeListener<? super MessageDto>) c->{
            c.next();
            if(c.wasAdded()){
                c.getAddedSubList().forEach(this::renderMessage);
            }
        });
    }

    public void init(){
        disposable.add(gameService.getMessages(game._id()).observeOn(FX_SCHEDULER)
                .subscribe(this.messages::setAll));

        disposable.add(eventListener.listen("games." + game._id() + ".messages.*.*", MessageDto.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(messageEvent -> {
                    final MessageDto message = messageEvent.data();
                    System.out.println(message);
                    if (messageEvent.event().endsWith(".created") && !messageAlreadyRendered(message)){
                        messages.add(message);
                    }
                }));
    }

    public void stop(){
        disposable.dispose();
    }

    public void renderMessage(MessageDto message){
        Label textLabel = new Label(message.sender() + ": " + message.body());
        textLabel.setFont(new Font(15));
        this.messageBox.getChildren().add(textLabel);
    }

    public boolean messageAlreadyRendered(MessageDto message){
        for(MessageDto m : messages){
            if(m._id().equals(message._id())){
                return true;
            }
        }
        return false;
    }

    private void sendMessage(ActionEvent actionEvent) {
        String message = this.messageText.getText();
        if(!message.isEmpty()) {
            disposable.add(gameService.sendMessage(game._id(), new CreateMessageDto(message))
                    .observeOn(FX_SCHEDULER)
                    .doOnError(Throwable::printStackTrace)
                    .subscribe(result -> {
                        System.out.println("Message mit Id: " + result._id() + " von " + result.sender() + ":" + result.body());
                        this.messageText.clear();
                    }));
        }
    }
}
