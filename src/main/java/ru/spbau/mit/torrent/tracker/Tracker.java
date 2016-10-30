package ru.spbau.mit.torrent.tracker;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Эдгар on 30.10.2016.
 */
public class Tracker {
    public static final int trackerPort = 8081;

    //map fileId to client's address
    private Map<UUID, InetSocketAddress> clients;

    //TODO
}
