package ru.spbau.mit.torrent.utils;

import lombok.Getter;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Эдгар on 05.12.2016.
 */
public class TorrentFile {
    private static final long CHUNK_SIZE = 10 * 1024 * 1024; // 10 Megabytes

    @Getter
    private final File file;
    @Getter
    private final long size;
    @Getter
    private final long fileID;
    @Getter
    private transient final Set<Integer> chunks = new HashSet<>();

    public TorrentFile(File file, long size, long fileID) {
        this.file = file;
        this.size = size;
        this.fileID = fileID;
    }

    public static TorrentFile empty(FileInfo info, File file) {
        return new TorrentFile(file, info.getSize(), info.getFileId());
    }

    public static TorrentFile full(File file, long size, long fileID) {
        TorrentFile res = new TorrentFile(file, size, fileID);
        for (int i = 0; i < res.chunksCount(); ++i) {
            res.addChunk(i);
        }
        return res;
    }

    private void addChunk(int chunk) {
        chunks.add(chunk);
    }

    private int chunksCount() {
        return (int) Math.ceil((size * 1.0) / CHUNK_SIZE);
    }

    private long getChunkSize(int chunk) {
        if (chunk >= chunksCount()) {
            return 0;
        }
        if (chunk < chunksCount() - 1) {
            return CHUNK_SIZE;
        }
        return size - (chunk  * CHUNK_SIZE);
    }
}
