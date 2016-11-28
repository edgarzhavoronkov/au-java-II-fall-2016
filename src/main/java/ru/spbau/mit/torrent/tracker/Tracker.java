package ru.spbau.mit.torrent.tracker;

import lombok.extern.log4j.Log4j2;
import ru.spbau.mit.torrent.exceptions.TrackerStartFailException;
import ru.spbau.mit.torrent.exceptions.TrackerStopFailException;
import ru.spbau.mit.torrent.io.TrackerSerializer;
import ru.spbau.mit.torrent.utils.ClientInfo;
import ru.spbau.mit.torrent.utils.FileInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Эдгар on 30.10.2016.
 */
@Log4j2
public class Tracker {
    private static final String workingDir = System.getProperty("user.dir");
    public static final int trackerPort = 8081;

    //fileId to active client, if he has part of file or whole file
    private Map<Long, InetSocketAddress> clients;
    private ServerSocket trackerSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //checks if his state can be deserialized from disk
    //if yes, reads it and starts
    public void start() throws TrackerStartFailException {
        try {
            trackerSocket = new ServerSocket(trackerPort);
            log.info("Started tracker on port " + trackerPort);
            //check before reading
            clients = TrackerSerializer.loadClients();
            //not here?
            while (!trackerSocket.isClosed()) {
                Socket incoming = trackerSocket.accept();
                executorService.execute(new ConnectionHandler(incoming));
            }

        } catch (IOException e) {
            throw new TrackerStartFailException(e);
        }
    }

    //dumps it's state on disk then shutdowns
    public void stop() throws TrackerStopFailException {
        TrackerSerializer.saveClients(clients, "");
        try {
            log.info("Trying to stop tracker");
            trackerSocket.close();
            executorService.shutdown();
        } catch (IOException e) {
            throw new TrackerStopFailException(e);
        } finally {
            log.info("Stopped tracker");
            trackerSocket = null;
        }
    }

    //TODO

    private List<FileInfo> handleList() {
        return null;
    }

    private long handleUpload() {
        return 0;
    }

    private List<ClientInfo> handleSources() {
        return null;
    }

    private boolean handleUpdate() {
        return false;
    }
}
