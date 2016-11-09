package ru.spbau.mit.torrent.tracker;

import ru.spbau.mit.torrent.utils.ClientInfo;
import ru.spbau.mit.torrent.utils.FileInfo;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * Created by Эдгар on 30.10.2016.
 */
public class Tracker {
    public static final int trackerPort = 8081;

    //map fileId to client's address
    private Map<Long, InetSocketAddress> clients;

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

    //checks if his state can be deserialized from disk
    //if yes, reads it and starts
    public void start() {

    }

    //dumps it's state on disk then shutdowns
    public void stop() {

    }
}
