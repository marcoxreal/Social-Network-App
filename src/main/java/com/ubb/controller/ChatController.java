package com.ubb.controller;

import com.ubb.domain.User;
import com.ubb.domain.Message;
import com.ubb.domain.ReplyMessage;
import com.ubb.event.MessageEvent;
import com.ubb.event.Observer;
import com.ubb.service.MessageService;
import com.ubb.service.UserService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

public class ChatController implements Observer<MessageEvent> {

    @FXML
    private ListView<Message> messageList;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    private User currentUser;
    private User selectedFriend;
    private MessageService messageService;
    private UserService userService;

    /** Mesajul selectat pentru reply */
    private Message replyTarget = null;

    public void setServices(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    public void setUsers(User currentUser, User selectedFriend) {
        this.currentUser = currentUser;
        this.selectedFriend = selectedFriend;

        setupCellFactory();
        setupSelectForReply();

        messageService.addObserver(this);

        loadConversation();
    }

    @Override
    public void update(MessageEvent event) {
        Message msg = event.getMessage();

        // verificăm dacă mesajul aparține acestei conversații
        boolean relevant =
                (msg.getFrom().getId().equals(currentUser.getId())
                        && msg.getTo().getId().equals(selectedFriend.getId()))
                        || (msg.getFrom().getId().equals(selectedFriend.getId())
                        && msg.getTo().getId().equals(currentUser.getId()));

        if (!relevant) return;

        Platform.runLater(() -> {
            messageList.getItems().add(msg);
            messageList.scrollTo(messageList.getItems().size() - 1);
        });
    }


    /** CLICK pe mesaj = îl selectăm pentru reply */
    private void setupSelectForReply() {
        messageList.setOnMouseClicked(e -> {
            Message selected = messageList.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            replyTarget = selected;

            String preview = selected.getMessage();
            if (preview.length() > 20) preview = preview.substring(0, 20) + "...";

            messageField.setPromptText("Replying to: " + preview);
        });
    }

    /** Bule grafice pentru Mesaj + ReplyMessage */
    private void setupCellFactory() {
        messageList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);

                if (empty || msg == null) {
                    setGraphic(null);
                    return;
                }

                boolean isMine = msg.getFrom().getId().equals(currentUser.getId());

                HBox bubble = new HBox();
                bubble.setPadding(new Insets(4, 10, 4, 10));

                VBox content = new VBox(3);

                // Dacă este reply, arată linia "Replied to ..."
                if (msg instanceof ReplyMessage rep && rep.getOriginalMessage() != null) {

                    String prev = rep.getOriginalMessage().getMessage();
                    if (prev.length() > 30) prev = prev.substring(0, 30) + "...";

                    Label repliedLabel = new Label("Replied to: " + prev);
                    repliedLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #777;");

                    content.getChildren().add(repliedLabel);
                }

                // Mesajul normal
                Label text = new Label(msg.getMessage());
                text.setWrapText(true);
                text.setMaxWidth(250);
                text.getStyleClass().add(isMine ? "bubble-right" : "bubble-left");

                // Ora
                Label time = new Label(msg.getDate().toLocalTime().toString());
                time.setStyle("-fx-font-size: 9px; -fx-text-fill: gray;");

                content.getChildren().addAll(text, time);

                bubble.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                bubble.getChildren().add(content);

                setGraphic(bubble);
            }
        });
    }

    private void loadConversation() {
        messageList.getItems().setAll(
                messageService.getConversation(currentUser.getId(), selectedFriend.getId())
        );
        messageList.scrollTo(messageList.getItems().size() - 1);
    }

    /** Trimite mesaj sau reply */
    @FXML
    private void handleSend() {
        String text = messageField.getText().trim();
        if (text.isEmpty()) return;

        if (replyTarget != null) {
            messageService.sendReply(currentUser, selectedFriend, text, replyTarget);
        } else {
            messageService.sendMessage(currentUser, selectedFriend, text);
        }

        replyTarget = null;
        messageField.clear();
        messageField.setPromptText("Type a message...");
    }
}
