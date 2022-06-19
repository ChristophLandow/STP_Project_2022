package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Constants;
import de.uniks.pioneers.controller.ChatController;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.services.UserlistService;
import de.uniks.pioneers.ws.EventListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javax.inject.Inject;
import javax.inject.Provider;

public class LobbyUserlistController extends OnlineUserlistController {
    public VBox usersVBox;
    private final App app;
    private final Provider<ChatController> chatControllerProvider;

    @Inject
    public LobbyUserlistController(App app, UserService userService, MessageService messageService, UserlistService userlistService,
                                   EventListener eventListener, Provider<ChatController> chatControllerProvider){
        super(userService, messageService, userlistService, eventListener);
        this.app = app;
        this.chatControllerProvider = chatControllerProvider;
    }

    @Override
    public void render(){
        this.usersVBox.getChildren().clear();
        usersVBox.getStyleClass().add("vBox");
        super.render();
    }

    @Override
    public void renderUser(User user){
        // For each user create a GridPane with username, hidden userid and avatar
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid");
        Label username = new Label(user.name());
        username.getStyleClass().add("NameLabel");
        username.setOnMouseClicked(this::openChat);

        ImageView imgView;
        try {
            imgView = new ImageView(new Image(user.avatar()));
        } catch (IllegalArgumentException | NullPointerException e) {
            imgView = new ImageView(new Image(Constants.DEFAULT_AVATAR));
        }

        imgView.setOnMouseClicked(this::openChat);
        imgView.setFitHeight(40);
        imgView.setFitWidth(40);

        Label userid = new Label(user._id());
        userid.setVisible(false);
        userid.setFont(new Font(0));

        gridPane.addRow(0, username, imgView, userid);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(200), new ColumnConstraints(45));

        this.usersVBox.getChildren().add(gridPane);
    }

    @Override
    public void removeUser(User user){
        usersVBox.getChildren().removeIf(n -> {
            GridPane gpane = (GridPane) n;
            return ((Label) gpane.getChildren().get(2)).getText().equals(user._id());
        });
    }

    @Override
    public void updateUser(User user){
        for(Node n: usersVBox.getChildren()){
            GridPane gpane = (GridPane) n;
            Label chatWithUserid = ((Label) gpane.getChildren().get(2));

            if(chatWithUserid.getText().equals(user._id())){
                ((Label) gpane.getChildren().get(0)).setText(user.name());

                try {
                    ((ImageView) gpane.getChildren().get(1)).setImage(new Image(user.avatar()));
                }catch(IllegalArgumentException | NullPointerException e){
                    ((ImageView) gpane.getChildren().get(1)).setImage(new Image(Constants.DEFAULT_AVATAR));
                }
            }
        }
    }

    @Override
    public void openChat(MouseEvent event){
        // Open chat of user who was clicked
        GridPane newChatUserParent = (GridPane) ((Node) event.getSource()).getParent();
        Label chatWithUsername = (Label) newChatUserParent.getChildren().get(0);

        User findUser = new User("","","","");
        for(User user : this.userlistService.getUsers()){
            if(user.name() != null){
                if(user.name().equals(chatWithUsername.getText())){
                    findUser = user;
                    break;
                }
            }
        }

        final User openUser = findUser;

        this.messageService.getchatUserList().removeIf(u->u._id().equals(openUser._id()));
        this.messageService.addUserToChatUserList(openUser);

        if(this.messageService.getchatUserList().size() > Constants.MAX_OPEN_CHATS){
            this.messageService.getchatUserList().remove(0);
        }
        app.show(chatControllerProvider.get());
    }

    public App getApp() {
        return this.app;
    }
}
