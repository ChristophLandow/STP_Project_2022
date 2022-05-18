package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.UserService;
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

import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class GameChatController {
    public ScrollPane chatScrollPane;
    public VBox messageBox;
    public TextField messageText;
    public Button sendButton;
    private final EventListener eventListener;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final ObservableList<MessageDto> messages = FXCollections.observableArrayList();
    private final UserService userService;
    private final NewGameLobbyService newGameLobbyService;
    public Game game;

    public List<User> users;

    @Inject
    GameChatController(NewGameLobbyService newGameLobbyService, EventListener eventListener, UserService userService){
        this.newGameLobbyService = newGameLobbyService;
        this.eventListener = eventListener;
        this.userService = userService;
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
        disposable.add(newGameLobbyService.getMessages(game._id()).observeOn(FX_SCHEDULER)
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
        User user = null;
        for(User u: users){
            if(u._id().equals(message.sender())){
                user = u;
            }
        }

        if(user == null){
            user = userService.getUserById(message.sender()).blockingFirst();
        }

        Label textLabel = new Label(user.name() + ": " + message.body());
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
            disposable.add(newGameLobbyService.sendMessage(game._id(), new CreateMessageDto(message))
                    .observeOn(FX_SCHEDULER)
                    .doOnError(Throwable::printStackTrace)
                    .subscribe(result -> {
                        System.out.println("Message mit Id: " + result._id() + " von " + result.sender() + ":" + result.body());
                        this.messageText.clear();
                    }));
        }
    }
}
