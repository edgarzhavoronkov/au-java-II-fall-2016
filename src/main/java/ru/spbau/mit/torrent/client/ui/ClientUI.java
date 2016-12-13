package ru.spbau.mit.torrent.client.ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.spbau.mit.torrent.client.Client;
import ru.spbau.mit.torrent.exceptions.*;
import ru.spbau.mit.torrent.tracker.Tracker;
import ru.spbau.mit.torrent.utils.FileInfo;
import ru.spbau.mit.torrent.utils.TorrentFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Эдгар on 12.12.2016.
 */
public class ClientUI extends Application {
    private Stage primaryStage;
    private static Client client;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: client-ui <tracker ip> <port>");
            System.exit(-1);
        }
        try {
            InetSocketAddress trackerAddress = new InetSocketAddress(InetAddress.getByName(args[0]), Tracker.TRACKER_PORT);
            int port = Integer.parseInt(args[1]);
            client = new Client(trackerAddress);
            client.start(port);
            launch(args);
        } catch (ClientStartFailException | UnknownHostException e) {
            showError(e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Client");
        primaryStage.setResizable(false);

        Scene scene = new ClientWindow();

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            try {
                client.stop();
                primaryStage.close();
            } catch (ClientStopFailedException e) {
                showError(e.getMessage());
            }
            System.exit(0);
        });
        primaryStage.show();
    }

    private class ClientWindow extends Scene {
        private final ListView<FileInfo> trackerFiles;
        private final ObservableList<String> clientFiles;

        ClientWindow() {
            super(new GridPane(), 720, 540);
            GridPane layout = (GridPane) getRoot();
            layout.setAlignment(Pos.BASELINE_CENTER);
            layout.setPadding(new Insets(10, 10, 10, 10));

            Label trackerFilesLabel = new Label("Tracker files:");
            trackerFiles = new ListView<>();
            trackerFiles.setPrefSize(360, 540);
            layout.add(trackerFilesLabel, 0, 0);
            layout.add(trackerFiles, 0, 1);

            clientFiles = FXCollections.observableArrayList();

            Label loadingFilesLabel = new Label("Loading:");
            ListView<String> loadingFiles = new ListView<>();
            loadingFiles.setPrefSize(360, 540);
            loadingFiles.setItems(clientFiles);
            layout.add(loadingFilesLabel, 1, 0);
            layout.add(loadingFiles, 1, 1);

            for (TorrentFile file : client.getFiles()) {
                if (file.isFull()) {
                    clientFiles.add(file.getFile().getName() + ": download completed");
                } else {
                    clientFiles.add(file.getFile().getName() + ": downloaded " + file.getChunks() + ":" + file.chunksCount());
                }
            }
            refreshFiles();

            final ContextMenu cm = new ContextMenu();
            MenuItem downloadItem = new MenuItem("Download");
            downloadItem.setOnAction(event -> {
                FileInfo f = (FileInfo)cm.getUserData();
                showDownloadDialog(f);
            });

            cm.getItems().add(downloadItem);
            trackerFiles.setOnMouseClicked(event -> {
                final FileInfo file = trackerFiles.getSelectionModel().getSelectedItem();
                if (event.getButton() == MouseButton.SECONDARY) {
                    if (file != null) {
                        cm.setUserData(file);
                        cm.show(trackerFiles, event.getScreenX(), event.getScreenY());
                    }
                }

            });

            Button uploadButton = new Button("Upload");
            layout.add(uploadButton, 1, 2);

            uploadButton.setOnAction(event -> {
                File f = showUploadDialog();
                if (f != null) {
                    try {
                        client.executeUpload(f);
                        refreshFiles();
                        clientFiles.add(f.getName());
                    } catch (FileNotFoundException | UploadFailException e) {
                        showError(e.getMessage());
                    }
                }
            });

            Button listButton = new Button("List files:");
            layout.add(listButton, 0, 2);
            listButton.setOnAction(event -> refreshFiles());

        }

        private void refreshFiles() {
            try {
                List<FileInfo> files = client.executeList();
                trackerFiles.setItems(FXCollections.observableArrayList(files));
            } catch (ListFailException e) {
                showError(e.getMessage());
            }
        }

        private void showDownloadDialog(FileInfo file) {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            dialog.setResizable(false);

            GridPane layout = new GridPane();
            layout.setPadding(new Insets(10, 10, 10, 10));
            layout.setAlignment(Pos.BASELINE_CENTER);
            layout.setMaxWidth(Double.MAX_VALUE);
            layout.setMaxHeight(Double.MAX_VALUE);


            Label fileNameLabel = new Label("Filename: " + file.getName());
            Label fileSizeLabel = new Label("Size: " + Long.valueOf(file.getSize()).toString());
            Button saveButton = new Button("Save as");

            layout.add(fileNameLabel, 0, 0);
            layout.add(fileSizeLabel, 0, 1);
            layout.add(saveButton, 0, 2);

            GridPane.setHgrow(fileNameLabel, Priority.ALWAYS);
            GridPane.setHgrow(fileSizeLabel, Priority.ALWAYS);
            GridPane.setHgrow(saveButton, Priority.ALWAYS);

            GridPane.setVgrow(fileNameLabel, Priority.ALWAYS);
            GridPane.setVgrow(fileSizeLabel, Priority.ALWAYS);
            GridPane.setVgrow(saveButton, Priority.ALWAYS);

            dialog.setScene(new Scene(layout, 320, 160));
            dialog.show();

            saveButton.setOnAction(event -> {
                File target = showSaveDialog();
                dialog.close();

                if (target != null) {
                    int idx = clientFiles.size();
                    String initial = target.getName() + ": download started";
                    clientFiles.add(initial);

                    Task<Void> downloadTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                client.getFile(file, target, this::updateProgress);
                            } catch (NoSeedsFoundException | LoadFailException e) {
                                throw new Exception(e);
                            }
                            return null;
                        }
                    };
                    downloadTask.progressProperty()
                            .asObject()
                            .addListener(
                                (observable, oldValue, newValue) -> clientFiles.set(idx, file.getName() + toPercent(newValue))
                    );
                    downloadTask.setOnSucceeded(event1 -> clientFiles.set(idx, file.getName() + ": download finished"));
                    downloadTask.setOnFailed(event1 -> clientFiles.set(idx, file.getName() + ": download failed"));
                    Thread downloadThread = new Thread(downloadTask);
                    downloadThread.start();
                }
            });
        }

        private String toPercent(Double value) {
            return String.format("%.2f %%", value * 100);
        }

        private File showUploadDialog() {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Upload");
            return chooser.showOpenDialog(primaryStage);
        }

        private File showSaveDialog() {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save As");
            return chooser.showSaveDialog(primaryStage);
        }
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                alert.close();
            }
        });
    }
}
