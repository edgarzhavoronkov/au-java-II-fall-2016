package ru.spbau.mit.torrent.client.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.spbau.mit.torrent.client.Client;
import ru.spbau.mit.torrent.tracker.Tracker;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by Эдгар on 12.12.2016.
 */
public class ConnectDialog extends Scene {
    private Client client;

    ConnectDialog(Stage primaryStage) {
        super(new GridPane(), 300, 160);
        GridPane layout = (GridPane) getRoot();
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setAlignment(Pos.BASELINE_CENTER);
        layout.setMaxWidth(Double.MAX_VALUE);
        layout.setMaxHeight(Double.MAX_VALUE);

        Label trackerAddressLabel = new Label("Tracker IP:");
        TextField trackerAddressTextField = new TextField();
        Label clientPortLabel = new Label("Client port:");
        TextField clientPortTextField = new TextField();
        Button connectButton = new Button("Connect");
        connectButton.setMaxWidth(Double.MAX_VALUE);

        layout.add(trackerAddressLabel, 0, 0);
        layout.add(trackerAddressTextField, 0, 1);
        layout.add(clientPortLabel, 0, 2);
        layout.add(clientPortTextField, 0, 3);
        layout.add(connectButton, 0, 4);

        GridPane.setHgrow(trackerAddressLabel, Priority.ALWAYS);
        GridPane.setHgrow(trackerAddressTextField, Priority.ALWAYS);
        GridPane.setHgrow(clientPortLabel, Priority.ALWAYS);
        GridPane.setHgrow(clientPortTextField, Priority.ALWAYS);
        GridPane.setHgrow(connectButton, Priority.ALWAYS);

        GridPane.setVgrow(trackerAddressLabel, Priority.ALWAYS);
        GridPane.setVgrow(trackerAddressTextField, Priority.ALWAYS);
        GridPane.setVgrow(clientPortLabel, Priority.ALWAYS);
        GridPane.setVgrow(clientPortTextField, Priority.ALWAYS);
        GridPane.setVgrow(connectButton, Priority.ALWAYS);

        connectButton.setOnAction(event -> {
            try {
                InetSocketAddress trackerAddress = new InetSocketAddress(InetAddress.getByName(trackerAddressTextField.getText()), Tracker.TRACKER_PORT);
                client = new Client(trackerAddress);
                client.start(Integer.parseInt(clientPortTextField.getText()));
                primaryStage.setScene(new ClientWindow());
            } catch (Throwable e) {
                showError("Cannot connect to tracker: " + e.getMessage());
            }
        });
    }

    private void showError(String message) {
        System.err.println(message);
    }
}
