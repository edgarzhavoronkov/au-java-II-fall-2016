package ru.spbau.mit.torrent.client;

import lombok.extern.log4j.Log4j2;
import ru.spbau.mit.torrent.utils.FileInfo;

import java.io.DataInputStream;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

/**
 * Created by Эдгар on 30.10.2016.
 */
@Log4j2
public class Client {

    public void connect(InetSocketAddress trackerSocketAddress) {

    }

    public void disconnect() {

    }

    private void sendRequest() {

    }

    private void handleStat() {

    }

    private void handleGet() {

    }

    public List<FileInfo> executeList() {
        return Collections.emptyList();
    }

    public long executeUpload(String filename) {
        return 0;
    }

    public DataInputStream executeGet(Long fileId, Integer partNum) {
        return null;
    }
}
