package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.ChatController;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.MessageService;
import de.uniks.pioneers.services.UserService;
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

public class LobbyUserlistControler extends OnlineUserlistController {
    public VBox usersVBox;
    private final App app;
    private final Provider<ChatController> chatControllerProvider;

    @Inject
    public LobbyUserlistControler(App app, UserService userService, MessageService messageService, EventListener eventListener,
                                  Provider<ChatController> chatControllerProvider){
        super(userService, messageService, eventListener);
        this.app = app;
        this.chatControllerProvider = chatControllerProvider;
    }

    @Override
    public void render(){
        this.usersVBox.getChildren().clear();
        super.render();
    }

    @Override
    public void renderUser(User user){
        GridPane gridPane = new GridPane();

        Label username = new Label(user.name());
        username.setOnMouseClicked(this::openChat);

        ImageView imgView;
        try {
            imgView = new ImageView(new Image(user.avatar()));
        } catch (NullPointerException e) {
            imgView = new ImageView();
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
                }catch(NullPointerException e){
                    ((ImageView) gpane.getChildren().get(1)).setImage(null);
                }
            }
        }
    }

    @Override
    public void openChat(MouseEvent event){
        GridPane newChatUserParent = (GridPane) ((Node) event.getSource()).getParent();
        Label chatWithUsername = (Label) newChatUserParent.getChildren().get(0);
        ImageView chatWithAvatar = (ImageView) newChatUserParent.getChildren().get(1);
        Label chatWithUserid = (Label) newChatUserParent.getChildren().get(2);

        this.messageService.getchatUserList().removeIf(u->u.name().equals(chatWithUsername.getText()));
        this.messageService.addUserToChatUserList(
                new User(chatWithUserid.getText(), chatWithUsername.getText(),"", chatWithAvatar.getImage().getUrl()));
        app.show(chatControllerProvider.get());
    }
}
