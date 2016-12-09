package ru.spbau.mit.torrent.utils;

import java.io.*;
import java.nio.channels.Channels;

/**
 * Created by Эдгар on 05.12.2016.
 * Class for physical interaction with chunks
 */
public class FileManager {
    /**
     * Reads chunk from file and sends it to net
     * @param torrentFile file to read chunk from
     * @param chunk number of chunk to read
     * @param out output stream to write to
     * @throws IOException if failed to send
     */
    public static void readChunk(TorrentFile torrentFile, int chunk, DataOutputStream out) throws IOException {
        long offset = chunk * TorrentFile.CHUNK_SIZE;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(torrentFile.getFile(), "r")) {
            randomAccessFile.seek(offset);
            copy(Channels.newInputStream(randomAccessFile.getChannel()), out, torrentFile.getChunkSize(chunk));
        }
    }

    /**
     * Reads chunk from net and writes it to file
     * @param torrentFile file to write chunk to
     * @param chunk number of chunk to write
     * @param in input stream to read from
     * @throws IOException if failed to send
     */
    public static void writeChunk(TorrentFile torrentFile, int chunk, DataInputStream in) throws IOException {
        long offset = chunk * TorrentFile.CHUNK_SIZE;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(torrentFile.getFile(), "rw")) {
            randomAccessFile.seek(offset);
            copy(in, Channels.newOutputStream(randomAccessFile.getChannel()), torrentFile.getChunkSize(chunk));
        }
    }

    private static void copy(InputStream inputStream, OutputStream out, int chunkSize) throws IOException {
        byte[] buffer = new byte[1024];
        int read = 0;
        while(chunkSize != 0 && read >= 0) {
            read = inputStream.read(buffer, 0, Math.min(chunkSize, 1024));
            out.write(buffer, 0, read);
            chunkSize -= read;
        }
    }
}
