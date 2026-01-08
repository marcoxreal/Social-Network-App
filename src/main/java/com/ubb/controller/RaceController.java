package com.ubb.controller;

import com.ubb.domain.Duck;
import com.ubb.domain.User;
import com.ubb.event.Observer;
import com.ubb.event.RaceEvent;
import com.ubb.event.RaceEventEvent;
import com.ubb.event.RaceResult;
import com.ubb.service.MessageService;
import com.ubb.service.UserService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class RaceController implements Observer<RaceEventEvent> {

    @FXML private Label lblTitle;
    @FXML private Label lblResult;
    @FXML private ListView<String> listNotifications;

    private final ObservableList<String> model = FXCollections.observableArrayList();

    private Stage stage;

    private RaceEvent raceEvent;

    private MessageService messageService;
    private User loggedUser; // cine pornește cursa

    public void init(Stage stage,
                     RaceEvent raceEvent,
                     MessageService messageService,
                     User loggedUser) {
        this.stage = stage;
        this.raceEvent = raceEvent;
        this.messageService = messageService;
        this.loggedUser = loggedUser;

        lblTitle.setText("Race: " + raceEvent.getName());

        listNotifications.setItems(model);

        // subscribes to live notifications
        raceEvent.addObserver(this);
    }

    public void startRace() {
        raceEvent.startAsync().thenAccept(result -> {
            Platform.runLater(() -> showFinalResult(result));
        });
    }

    @Override
    public void update(RaceEventEvent ev) {
        Platform.runLater(() -> {
            model.add(ev.getMessage());
            listNotifications.scrollTo(model.size() - 1);
        });
    }

    private void showFinalResult(RaceResult result) {
        lblResult.setText(result.getMessage());
    }

    public void shutdown() {
        try {
            if (raceEvent != null) {
                raceEvent.removeObserver(this);
                raceEvent.shutdown();
            }
        } catch (Exception ignored) {}
    }

    @FXML
    private void onClose() {
        shutdown();
        stage.close();
    }
}
