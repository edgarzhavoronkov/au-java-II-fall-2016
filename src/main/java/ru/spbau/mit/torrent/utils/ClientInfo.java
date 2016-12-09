package ru.spbau.mit.torrent.utils;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Эдгар on 09.11.2016.
 * Information about client, address, port et cetera
 */
@EqualsAndHashCode
public class ClientInfo {
    @Getter
    private final byte[] address;
    @Getter
    private final int port;

    private final long creationTime;

    private final Set<Long> files = new HashSet<>();

    /**
     * Creates client info with given address and port
     * @param address client address
     * @param port client port
     */
    public ClientInfo(byte[] address, int port) {
        this.address = address;
        this.port = port;
        creationTime = System.currentTimeMillis();
    }

    public boolean hasFile(long fileID) {
        return files.contains(fileID);
    }

    public boolean isActive() {
        return System.currentTimeMillis() - creationTime < TimeUnit.MINUTES.toMillis(5);
    }

    public void addFile(long fileID) {
        files.add(fileID);
    }
}
