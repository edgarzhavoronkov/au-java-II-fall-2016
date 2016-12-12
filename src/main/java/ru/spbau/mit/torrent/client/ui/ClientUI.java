package ru.spbau.mit.torrent.client.ui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Эдгар on 12.12.2016.
 */
public class ClientUI extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //this.primaryStage = primaryStage;
        primaryStage.setTitle("Connect");
        primaryStage.setResizable(false);
        primaryStage.setScene(new ConnectDialog(primaryStage));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
