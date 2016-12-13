package ru.spbau.mit.torrent.client;

/**
 * Created by Эдгар on 14.12.2016.
 */
public interface OnChunkDownload {
    void fire(int loaded, int total);
}
