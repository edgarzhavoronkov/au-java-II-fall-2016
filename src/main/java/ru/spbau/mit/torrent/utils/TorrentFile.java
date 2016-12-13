package ru.spbau.mit.torrent.utils;

import lombok.Getter;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Эдгар on 05.12.2016.
 * Abstraction for file and it's chunks
 */
public class TorrentFile {
    public static final int CHUNK_SIZE = 10 * 1024 * 1024; // 10 Megabytes

    @Getter
    private final File file;
    @Getter
    private final long size;
    @Getter
    private final long fileID;
    @Getter
    private final Set<Integer> chunks = new HashSet<>();
    @Getter
    private final Set<Integer> chunksInProgress = new HashSet<>();

    private TorrentFile(File file, long size, long fileID) {
        this.file = file;
        this.size = size;
        this.fileID = fileID;
    }

    /**
     * Creates empty(with no chunks) file from given FileInfo
     * @param info information about file
     * @param file file itself
     * @return result
     */
    public static TorrentFile empty(FileInfo info, File file) {
        return new TorrentFile(file, info.getSize(), info.getFileId());
    }

    /**
     * Creates file with all chunks in it
     * @param file file itself
     * @param size size
     * @param fileID ID
     * @return result
     */
    public static TorrentFile full(File file, long size, long fileID) {
        TorrentFile res = new TorrentFile(file, size, fileID);
        for (int i = 0; i < res.chunksCount(); ++i) {
            res.addChunk(i);
        }
        return res;
    }

    /**
     * Checks if file has all of chunks
     * @return true if yes and false otherwise
     */
    public boolean isFull() {
        return chunksCount() == chunks.size();
    }

    /**
     * Adds chunk with given number to in-progress queue
     * @param chunk chunk to add
     */
    public synchronized void startDownload(Integer chunk) {
        chunksInProgress.add(chunk);
    }

    /**
     * Adds chunk with given number
     * @param chunk chunk's number
     */
    public synchronized void addChunk(int chunk) {
        chunksInProgress.remove(chunk);
        chunks.add(chunk);
    }

    /**
     * Gets size of given chunk
     * @param chunk chunk's number
     * @return CHUNK_SIZE if chunk is not last and
     * residual size otherwise
     */
    public int getChunkSize(int chunk) {
        if (chunk >= chunksCount()) {
            return 0;
        }
        if (chunk < chunksCount() - 1) {
            return CHUNK_SIZE;
        }
        return (int) (size - (chunk  * CHUNK_SIZE));
    }

    /**
     * Returns number of chunks of given file
     * @return number of chunks of torrent file
     */
    public int chunksCount() {
        return (int) Math.ceil((size * 1.0) / CHUNK_SIZE);
    }
}
